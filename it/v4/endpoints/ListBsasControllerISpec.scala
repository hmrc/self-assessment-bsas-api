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

package v4.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import common.errors._
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.{
  BusinessIdFormatError,
  InternalError,
  MtdError,
  NinoFormatError,
  NotFoundError,
  RuleTaxYearNotSupportedError,
  RuleTaxYearRangeInvalidError,
  TaxYearFormatError
}
import shared.stubs.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import support.IntegrationBaseSpec
import v4.fixtures.ListBsasFixture

class ListBsasControllerISpec extends IntegrationBaseSpec with ListBsasFixture {

  private trait Test {
    // common
    val nino                           = "AA123456B"
    val typeOfBusiness: Option[String] = Some("self-employment")
    val businessId: Option[String]     = Some("XAIS12345678910")

    def taxYear: Option[String]

    // downstream
    def downstreamUri: String

    def setupStubs(): StubMapping

    def request: WSRequest = {
      setupStubs()
      buildRequest(mtdUri)
        .addQueryStringParameters(mtdQueryParams: _*)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.4.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

    // mtd
    def mtdUri: String = s"/$nino"

    def mtdQueryParams: Seq[(String, String)] =
      Seq("typeOfBusiness" -> typeOfBusiness, "businessId" -> businessId, "taxYear" -> taxYear)
        .collect { case (k, Some(v)) =>
          (k, v)
        }

    def errorBody(code: String): String =
      s"""{
         |  "code": "$code",
         |  "reason": "error message"
         |}""".stripMargin

  }

  private trait NonTysTest extends Test {
    def taxYear: Option[String] = Some("2019-20")

    override def downstreamUri: String = s"/income-tax/adjustable-summary-calculation/$nino"
  }

  private trait TysIfsTest extends Test {

    private val mtdTaxYear: String        = TaxYear.now().asMtd
    private val downstreamTaxYear: String = TaxYear.now().asTysDownstream

    def taxYear: Option[String] = Some(mtdTaxYear)

    override def downstreamUri: String = s"/income-tax/adjustable-summary-calculation/$downstreamTaxYear/$nino"
  }

  "Calling the list Bsas endpoint" should {
    "return a valid response with status OK" when {
      "valid request is made" in new NonTysTest {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, listBsasDownstreamJsonMultiple)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe summariesJSONWithHateoas(Nino(nino))

      }

      "valid request is made with a Tax Year Specific (TYS) tax year" in new TysIfsTest {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, listBsasDownstreamJsonMultiple)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe summariesJSONWithHateoas(Nino(nino), Some("2024-25"))
      }

      "valid request is made with foreign property" in new NonTysTest {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, listBsasResponseDownstreamJsonForeign)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe summariesJSONForeignWithHateoas(Nino(nino))
      }

      "valid request is made with foreign property and a Tax Year Specific (TYS) tax year" in new TysIfsTest {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, listBsasResponseDownstreamJsonForeign)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe summariesJSONForeignWithHateoas(Nino(nino), Some("2024-25"))
      }

      "valid request is made without a tax year so that he current tax year is used" in new TysIfsTest {
        private val currentTaxYear = TaxYear.now()

        override val taxYear: Option[String] = None

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, listBsasDownstreamJsonMultiple)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe summariesJSONWithHateoas(Nino(nino), Some(currentTaxYear.asMtd))
      }
    }

    "return error according to spec" when {

      def validationErrorTest(requestNino: String,
                              requestTaxYear: String,
                              requestTypeOfBusiness: Option[String],
                              requestBusinessId: Option[String],
                              expectedStatus: Int,
                              expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new NonTysTest {

          override val nino: String                   = requestNino
          override val taxYear: Option[String]        = Some(requestTaxYear)
          override val typeOfBusiness: Option[String] = requestTypeOfBusiness
          override val businessId: Option[String]     = requestBusinessId

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request.get())
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

    "downstream service error" when {

      def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"downstream returns an $downstreamCode error and status $downstreamStatus" in new NonTysTest {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            DownstreamStub.onError(DownstreamStub.GET, downstreamUri, downstreamStatus, errorBody(downstreamCode))
          }

          val response: WSResponse = await(request.get())
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val errors = List(
        (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
        (BAD_REQUEST, "INVALID_TAXYEAR", BAD_REQUEST, TaxYearFormatError),
        (BAD_REQUEST, "INVALID_INCOMESOURCEID", BAD_REQUEST, BusinessIdFormatError),
        (BAD_REQUEST, "INVALID_INCOMESOURCE_TYPE", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
        (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
        (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_REQUEST", INTERNAL_SERVER_ERROR, InternalError)
      )

      val extraTysErrors = List(
        (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
        (BAD_REQUEST, "INVALID_INCOMESOURCE_ID", BAD_REQUEST, BusinessIdFormatError),
        (BAD_REQUEST, "NOT_FOUND", NOT_FOUND, NotFoundError),
        (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
      )

      (errors ++ extraTysErrors).foreach(args => (serviceErrorTest _).tupled(args))
    }
  }

}
