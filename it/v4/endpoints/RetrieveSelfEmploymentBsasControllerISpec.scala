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

import common.errors.RuleTypeOfBusinessIncorrectError
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors._
import shared.stubs.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import support.IntegrationBaseSpec
import v4.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures._
import v4.models.domain.IncomeSourceType

class RetrieveSelfEmploymentBsasControllerISpec extends IntegrationBaseSpec {

  private trait Test {
    val nino          = "AA123456A"
    val calculationId = "03e3bc8b-910d-4f5b-88d7-b627c84f2ed7"

    def request : WSRequest = request()

    def request(authorisedAsSecondaryAgent: Boolean = false): WSRequest = {
      AuditStub.audit()
      MtdIdLookupStub.ninoFound(nino)
      setupStubs()
      if (authorisedAsSecondaryAgent) {
        AuthStub.authorisedAsSecondaryAgent()
      } else {
        AuthStub.authorised()
      }
      buildRequest(uri)
        .withQueryStringParameters(taxYear.map(ty => Seq("taxYear" -> ty)).getOrElse(Nil): _*)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.4.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

    def taxYear: Option[String] = None

    def uri: String = s"/$nino/self-employment/$calculationId"

    def setupStubs(): Unit = ()
  }

  private trait NonTysTest extends Test {
    def downstreamUrl: String = s"/income-tax/adjustable-summary-calculation/$nino/$calculationId"

  }

  private trait TysIfsTest extends Test {
    override def taxYear: Option[String] = Some("2023-24")

    def downstreamUrl: String = s"/income-tax/adjustable-summary-calculation/23-24/${nino}/$calculationId"
  }

  "Calling the retrieve Self-assessment Bsas endpoint" should {
    "return a valid response with status OK" when {
      "valid request is made" in new NonTysTest {
        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUrl, OK, downstreamRetrieveBsasResponseJson)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe mtdRetrieveBsasReponseJsonWithHateoas(nino, calculationId)
        response.header("Deprecation") should not be None

      }
      "valid request is made for TYS" in new TysIfsTest {
        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUrl, OK, downstreamRetrieveBsasResponseJson)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe mtdRetrieveBsasReponseJsonWithHateoas(nino, calculationId, taxYear)
        response.header("Deprecation") should not be None

      }
    }

    "return error response with status FORBIDDEN" when {
      "valid request is made but downstream response has invalid type of business" in new NonTysTest {
        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(
            DownstreamStub.GET,
            downstreamUrl,
            OK,
            downstreamRetrieveBsasResponseJsonInvalidIncomeSourceType(IncomeSourceType.`15`))
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe BAD_REQUEST
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe RuleTypeOfBusinessIncorrectError.asJson
      }
    }

    "return error according to spec" when {
      def validationErrorTest(requestNino: String,
                              requestBsasId: String,
                              taxYearString: Option[String],
                              expectedStatus: Int,
                              expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new TysIfsTest {

          override val nino: String          = requestNino
          override val calculationId: String = requestBsasId

          override def taxYear: Option[String] = taxYearString

          override def setupStubs(): Unit = {}

          val response: WSResponse = await(request.get())
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = Seq(
        ("AA1123A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2023-24"), BAD_REQUEST, NinoFormatError),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-beans", Some("2023-24"), BAD_REQUEST, CalculationIdFormatError),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2023-2024"), BAD_REQUEST, TaxYearFormatError),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2019-20"), BAD_REQUEST, InvalidTaxYearParameterError)
      )
      input.foreach(args => (validationErrorTest _).tupled(args))
    }

    "service error" when {

      def errorBody(code: String): String =
        s"""{
           |  "code": "$code",
           |  "reason": "message"
           |}""".stripMargin

      def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"downstream returns an $downstreamStatus error and status $downstreamCode" in new TysIfsTest {

          override def setupStubs(): Unit = {
            DownstreamStub.onError(DownstreamStub.GET, downstreamUrl, downstreamStatus, errorBody(downstreamCode))
          }

          val response: WSResponse = await(request.get())
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val errors = Seq(
        (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
        (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
        (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
        (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_RETURN", INTERNAL_SERVER_ERROR, InternalError),
        (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
        (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
        (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
        (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
        (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
      )
      errors.foreach(args => (serviceErrorTest _).tupled(args))
    }

    "return success (200) for Secondary Agent" when {
      "Secondary Agent ia allowed access to the endpoint" in new NonTysTest {
        DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUrl, OK, downstreamRetrieveBsasResponseJson)
        val response: WSResponse =  await(request(authorisedAsSecondaryAgent = true).get())
        response.status shouldBe OK
      }
    }
  }

}
