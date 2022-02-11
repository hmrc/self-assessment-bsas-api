/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v3.endpoints

import play.api.http.HeaderNames.ACCEPT
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK, SERVICE_UNAVAILABLE}
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v3.fixtures.ListBsasFixture
import v3.models.errors._
import v3.stubs.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}

class ListBsasControllerISpec extends IntegrationBaseSpec with ListBsasFixture{

  private trait Test {
    val nino = "AA123456B"
    val taxYear: Option[String] = Some("2019-20")
    val typeOfBusiness: Option[String] = Some("self-employment")
    val businessId: Option[String] = None

    def uri: String = s"/$nino"
    def desUrl: String = s"/income-tax/adjustable-summary-calculation/$nino"
    def setupStubs(): StubMapping
    def request: WSRequest = {

      val queryParams = Seq("typeOfBusiness" -> typeOfBusiness, "businessId" -> businessId, "taxYear" -> taxYear)
        .collect {
          case (k, Some(v)) => (k, v)
        }
      setupStubs()
      buildRequest(uri)
        .addQueryStringParameters(queryParams: _*)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.3.0+json"))
    }
  }

  "Calling the list Bsas endpoint" should {
    "return a valid response with status OK" when {
      "valid request is made" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, desUrl, OK, listBsasDownstreamJsonMultiple)
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe summariesJSONWithHateoas(nino)
      }

      "valid request is made with foreign property" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, desUrl, OK, listBsasResponseDownstreamJsonForeign)
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe summariesJSONForeignWithHateoas(nino)
      }

      "valid request is made without a tax year" in new Test {
        override val taxYear: Option[String] = None

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, desUrl, OK, listBsasDownstreamJsonMultiple)
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe summariesJSONWithHateoas(nino)
      }
    }

    "return error according to spec" when {

      def validationErrorTest(requestNino: String, requestTaxYear: String,
                              requestTypeOfBusiness: Option[String], requestBusinessId: Option[String],
                              expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new Test {

          override val nino: String = requestNino
          override val taxYear: Option[String] = Some(requestTaxYear)
          override val typeOfBusiness: Option[String] = requestTypeOfBusiness
          override val businessId: Option[String] = requestBusinessId

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request.get)
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = Seq(
        ("AA1123A", "2019-20", Some("self-employment"), Some("X0IS00000000210"), BAD_REQUEST, NinoFormatError),
        ("AA123456A", "20177", Some("self-employment"), Some("X0IS00000000210"), BAD_REQUEST, TaxYearFormatError),
        ("AA123456A", "2018-19", Some("self-employment"), Some("X0IS00000000210"), BAD_REQUEST, RuleTaxYearNotSupportedError),
        ("AA123456A", "2019-20", Some("self-employment"), Some("X0IS00"), BAD_REQUEST, BusinessIdFormatError),
        ("AA123456A", "2019-20", Some("self-employments-or-not"), Some("X0IS00000000210"), BAD_REQUEST, TypeOfBusinessFormatError),
        ("AA123456A", "2019-21", Some("self-employment"), Some("X0IS00000000210"), BAD_REQUEST, RuleTaxYearRangeInvalidError)
      )

      input.foreach(args => (validationErrorTest _).tupled(args))
    }

    "des service error" when {

      def errorBody(code: String): String =
        s"""{
           |  "code": "$code",
           |  "reason": "des message"
           |}""".stripMargin

      def serviceErrorTest(desStatus: Int, desCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"des returns an $desCode error and status $desStatus" in new Test {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            DownstreamStub.onError(DownstreamStub.GET, desUrl, desStatus, errorBody(desCode))
          }

          val response: WSResponse = await(request.get)
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = Seq(
        (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
        (BAD_REQUEST, "INVALID_TAXYEAR", BAD_REQUEST, TaxYearFormatError),
        (BAD_REQUEST, "INVALID_INCOMESOURCEID", BAD_REQUEST, BusinessIdFormatError),
        (BAD_REQUEST, "INVALID_INCOMESOURCE_TYPE", INTERNAL_SERVER_ERROR, DownstreamError),
        (BAD_REQUEST, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
        (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
        (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError),
        (BAD_REQUEST, "INVALID_REQUEST", INTERNAL_SERVER_ERROR, DownstreamError)
      )

      input.foreach(args => (serviceErrorTest _).tupled(args))
    }
  }
}
