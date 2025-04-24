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

package v5.selfEmploymentBsas.retrieve.def1

import common.errors._
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors._
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import v5.common.model.IncomeSourceType.{`02`, `03`, `04`, `15`}
import v5.selfEmploymentBsas.retrieve.def1.model.Def1_RetrieveSelfEmploymentBsasFixtures._

class Def1_RetrieveSelfEmploymentBsasHipISpec extends IntegrationBaseSpec {

  override def servicesConfig: Map[String, Any] =
    Map(
      "api.5.0.endpoints.allow-request-cannot-be-fulfilled-header" -> true,
      "feature-switch.ifs_hip_migration_1876.enabled"              -> true
    ) ++ super.servicesConfig

  "Calling the retrieve Self-assessment Bsas endpoint" should {
    "return a valid response with status OK" when {
      "given a valid request" in new NonTysTest {
        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUrl, OK, downstreamRetrieveBsasResponseJson)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe mtdRetrieveBsasResponseJson
        response.header("Deprecation") shouldBe Some("Thu, 24 Apr 2025 23:59:59 GMT")
      }

      "given a valid TYS request" in new TysIfsTest {
        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUrl, OK, downstreamRetrieveBsasResponseJson)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe mtdRetrieveBsasResponseJson
        response.header("Deprecation") shouldBe Some("Thu, 24 Apr 2025 23:59:59 GMT")
      }
    }

    "return error response with status BAD_REQUEST" when {
      Seq(`02`, `03`, `04`, `15`).foreach { incomeSourceType =>
        Seq(
          (
            "Non-TYS",
            new NonTysTest {
              override def setupStubs(): Unit =
                DownstreamStub.onSuccess(
                  DownstreamStub.GET,
                  downstreamUrl,
                  OK,
                  downstreamRetrieveBsasResponseJsonInvalidIncomeSourceType(incomeSourceType)
                )
            }
          ),
          (
            "TYS",
            new TysIfsTest {
              override def setupStubs(): Unit =
                DownstreamStub.onSuccess(
                  DownstreamStub.GET,
                  downstreamUrl,
                  OK,
                  downstreamRetrieveBsasResponseJsonInvalidIncomeSourceType(incomeSourceType)
                )
            }
          )
        ).foreach { case (scenario, testInstance) =>
          s"given a valid $scenario request but downstream response has invalid income source type $incomeSourceType" in {
            val response: WSResponse = await(testInstance.request.get())

            response.status shouldBe BAD_REQUEST
            response.header("Content-Type") shouldBe Some("application/json")
            response.json shouldBe RuleTypeOfBusinessIncorrectError.asJson
          }
        }
      }
    }

    "return error according to spec" when {
      def validationErrorTest(requestNino: String,
                              requestBsasId: String,
                              taxYearString: Option[String],
                              expectedStatus: Int,
                              expectedBody: MtdError,
                              maybeGovTestScenario: Option[String]): Unit = {
        s"validation fails with ${expectedBody.code} error" in new TysIfsTest {

          override val nino: String          = requestNino
          override val calculationId: String = requestBsasId

          override def taxYear: Option[String] = taxYearString

          override def setupStubs(): Unit = {}

          private val req = maybeGovTestScenario match {
            case Some(govTestScenario) =>
              request.addHttpHeaders("Gov-Test-Scenario" -> govTestScenario)
            case None => request
          }

          val response: WSResponse = await(req.get())
          withClue(s"Returned ${response.status} with body:\n${response.body}\n") {
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }
      }

      val input = List(
        ("AA1123A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2023-24"), BAD_REQUEST, NinoFormatError, None),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-beans", Some("2023-24"), BAD_REQUEST, CalculationIdFormatError, None),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2023-2024"), BAD_REQUEST, TaxYearFormatError, None),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2019-20"), BAD_REQUEST, InvalidTaxYearParameterError, None),
        (
          "AA123456A",
          "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          Some("2023-24"),
          UNPROCESSABLE_ENTITY,
          RuleRequestCannotBeFulfilledError,
          Some("REQUEST_CANNOT_BE_FULFILLED"))
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

      val errors = List(
        (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
        (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
        (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
        (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_RETURN", INTERNAL_SERVER_ERROR, InternalError),
        (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
        (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
        (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
        (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
        (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
      )
      errors.foreach(args => (serviceErrorTest _).tupled(args))
    }
  }

  private trait Test {
    val nino          = "AA123456A"
    val calculationId = "03e3bc8b-910d-4f5b-88d7-b627c84f2ed7"

    def request: WSRequest = {
      AuditStub.audit()
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      setupStubs()
      buildRequest(uri)
        .withQueryStringParameters(taxYear.map(ty => List("taxYear" -> ty)).getOrElse(Nil): _*)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.5.0+json"),
          (AUTHORIZATION, "Bearer 123")
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

    def downstreamUrl: String = s"/itsa/income-tax/v1/23-24/adjustable-summary-calculation/$nino/$calculationId"
  }

}
