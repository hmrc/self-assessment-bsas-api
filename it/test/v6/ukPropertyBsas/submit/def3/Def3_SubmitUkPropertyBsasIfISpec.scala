/*
 * Copyright 2025 HM Revenue & Customs
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

package v6.ukPropertyBsas.submit.def3

import common.errors.*
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.*
import play.api.libs.json.*
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors.*
import shared.models.utils.JsonErrorValidators
import shared.services.*
import shared.support.IntegrationBaseSpec
import v6.ukPropertyBsas.submit.def3.model.request.SubmitUKPropertyBsasRequestBodyFixtures.fullRequestJson

class Def3_SubmitUkPropertyBsasIfISpec extends IntegrationBaseSpec with JsonErrorValidators {

  override def servicesConfig: Map[String, Any] =
    Map("feature-switch.ifs_hip_migration_1874.enabled" -> false) ++ super.servicesConfig

  "Calling the Submit UK Property Accounting Adjustments endpoint" should {
    "return a 200 status code" when {

      "any valid request is made for a TYS tax year" in new TysIfsTest {
        override def setupStubs(): Unit = {
          stubDownstreamSuccess()
        }

        val response: WSResponse = await(request().post(fullRequestJson))
        response.status shouldBe OK
        response.header("Content-Type") shouldBe None
      }
    }

    "return validation error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestCalculationId: String,
                                requestTaxYear: String,
                                requestBody: JsValue,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new TysIfsTest {

            override val nino: String          = requestNino
            override val calculationId: String = requestCalculationId
            override val taxYear: String       = requestTaxYear

            val response: WSResponse = await(request().post(requestBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = List(
          ("AA1234A", "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2", "2025-26", fullRequestJson, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "041f7e4d87b9", "2025-26", fullRequestJson, BAD_REQUEST, CalculationIdFormatError),
          ("AA123456A", "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2", "BAD_TAX_YEAR", fullRequestJson, BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2", "2022-24", fullRequestJson, BAD_REQUEST, RuleTaxYearRangeInvalidError),
          (
            "AA123456A",
            "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2",
            "2025-26",
            fullRequestJson.replaceWithEmptyObject("/ukProperty/income"),
            BAD_REQUEST,
            RuleIncorrectOrEmptyBodyError.copy(paths = Some(List("/ukProperty/income")))
          ),
          (
            "AA123456A",
            "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2",
            "2025-26",
            fullRequestJson.update("/ukProperty/expenses/consolidatedExpenses", JsNumber(1.23)),
            BAD_REQUEST,
            RuleBothExpensesError.copy(paths = Some(List("/ukProperty/expenses")))
          )
        )
        input.foreach(validationErrorTest.tupled)
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new TysIfsTest {

            override def setupStubs(): Unit = {
              DownstreamStub.onError(DownstreamStub.PUT, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().post(fullRequestJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val errors = List(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C55316", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C15320", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C55508", BAD_REQUEST, RulePropertyIncomeAllowanceClaimed),
          (FORBIDDEN, "BVR_FAILURE_C55503", BAD_REQUEST, RuleOverConsolidatedExpensesThreshold),
          (FORBIDDEN, "BVR_FAILURE_C55509", BAD_REQUEST, RulePropertyIncomeAllowanceClaimed),
          (FORBIDDEN, "BVR_FAILURE_C559107", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C559103", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C559099", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (CONFLICT, "ASC_ALREADY_SUPERSEDED", BAD_REQUEST, RuleSummaryStatusSuperseded),
          (CONFLICT, "ASC_ALREADY_ADJUSTED", BAD_REQUEST, RuleAlreadyAdjusted),
          (UNPROCESSABLE_ENTITY, "UNALLOWABLE_VALUE", BAD_REQUEST, RuleResultingValueNotPermitted),
          (UNPROCESSABLE_ENTITY, "ASC_ID_INVALID", BAD_REQUEST, RuleSummaryStatusInvalid),
          (UNPROCESSABLE_ENTITY, "INCOMESOURCE_TYPE_NOT_MATCHED", BAD_REQUEST, RuleTypeOfBusinessIncorrectError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )
        val extraTysErrors = List(
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (UNPROCESSABLE_ENTITY, "INCOME_SOURCE_TYPE_NOT_MATCHED", BAD_REQUEST, RuleTypeOfBusinessIncorrectError)
        )

        (errors ++ extraTysErrors).foreach(serviceErrorTest.tupled)
      }
    }
  }

  private trait Test {

    val nino: String          = "AA123456A"
    val calculationId: String = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"

    def downstreamUri: String

    def stubDownstreamSuccess(): Unit = {
      DownstreamStub
        .onSuccess(DownstreamStub.PUT, downstreamUri, OK)
    }

    def request(): WSRequest = {
      AuditStub.audit()
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      setupStubs()
      buildRequest(s"/$nino/uk-property/$calculationId/adjust/$taxYear")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.6.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def taxYear: String

    def setupStubs(): Unit = ()

    def errorBody(code: String): String =
      s"""
         |{
         |  "code": "$code",
         |  "reason": "message"
         |}
       """.stripMargin

  }

  private trait TysIfsTest extends Test {
    override def taxYear: String = "2025-26"

    def downstreamUri: String = s"/income-tax/adjustable-summary-calculation/25-26/$nino/$calculationId"
  }

}
