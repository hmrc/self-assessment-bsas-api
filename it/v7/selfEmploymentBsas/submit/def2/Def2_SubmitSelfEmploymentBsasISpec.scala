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
 * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v7.selfEmploymentBsas.submit.def2

import common.errors._
import play.api.http.HeaderNames.ACCEPT
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers._
import shared.models.errors._
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import v7.selfEmploymentBsas.submit.def2.model.request.fixtures.SubmitSelfEmploymentBsasFixtures._

class Def2_SubmitSelfEmploymentBsasISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino          = "AA123456A"
    val calculationId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"

    def setupStubs(): Unit = ()

    def mtdUri: String

    def downstreamUrl: String
    def taxYear: String

    def stubDownstreamSuccess(downstreamRequestBody: JsValue): Unit =
      DownstreamStub
        .when(DownstreamStub.PUT, downstreamUrl)
        .withRequestBody(downstreamRequestBody)
        .thenReturn(OK)

    def request(): WSRequest = {
      AuditStub.audit()
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      setupStubs()
      buildRequest(mtdUri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.7.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def errorBody(code: String): String =
      s"""
        | {
        |   "code": "$code",
        |   "reason": "error message"
        | }
      """.stripMargin

  }

  private trait HipTest extends Test {
    override def taxYear: String = "2024-25"
    def mtdUri: String           = s"/$nino/self-employment/$calculationId/adjust/$taxYear"
    def downstreamUrl: String    = s"/itsa/income-tax/v1/24-25/adjustable-summary-calculation/$nino/$calculationId"
  }

  "Calling the Submit Adjustments endpoint for self-employment" should {
    "return a 200 status code" when {
      List(
        ("without zero adjustments", mtdRequestJson, requestToIfs),
        (
          "with only zero adjustments set to true",
          mtdRequestWithOnlyZeroAdjustments(true),
          downstreamRequestWithOnlyZeroAdjustments
        )
      ).foreach { case (scenario, mtdRequestBodyJson, downstreamRequestBodyJson) =>
        s"any valid request $scenario is made" in new HipTest {
          override def setupStubs(): Unit = stubDownstreamSuccess(downstreamRequestBodyJson)

          val result: WSResponse = await(request().post(mtdRequestBodyJson))
          result.status shouldBe OK
          result.header("Content-Type") shouldBe None
        }
      }
    }

    "return error according to spec" when {
      def validationErrorTest(requestNino: String,
                              requestTaxYear: String,
                              requestCalculationId: String,
                              expectedStatus: Int,
                              expectedBody: MtdError,
                              requestBodyJson: JsValue,
                              errorWrapper: Option[ErrorWrapper]): Unit =
        s"validation fails with ${expectedBody.code} error" in new HipTest {
          override val nino: String          = requestNino
          override val taxYear: String       = requestTaxYear
          override val calculationId: String = requestCalculationId

          val expectedBodyJson: JsValue = errorWrapper match {
            case Some(wrapper) => Json.toJson(wrapper)
            case None          => Json.toJson(expectedBody)
          }

          val response: WSResponse = await(request().post(requestBodyJson))
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBodyJson)
        }

      val input = List(
        ("AA1234A", "2024-25", "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2", BAD_REQUEST, NinoFormatError, mtdRequestJson, None),
        ("AA123456A", "2024", "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2", BAD_REQUEST, TaxYearFormatError, mtdRequestJson, None),
        ("AA123456A", "2024-26", "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2", BAD_REQUEST, RuleTaxYearRangeInvalidError, mtdRequestJson, None),
        ("AA123456A", "2024-25", "041f7e4d-87b9-4d4a", BAD_REQUEST, CalculationIdFormatError, mtdRequestJson, None),
        (
          "AA123456A",
          "2024-25",
          "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2",
          BAD_REQUEST,
          RuleBothExpensesError.withPath("/expenses"),
          mtdRequestWithBothExpenses,
          None
        ),
        (
          "AA123456A",
          "2024-25",
          "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2",
          BAD_REQUEST,
          RuleBothAdjustmentsSuppliedError,
          mtdRequestWithZeroAndOtherAdjustments(true),
          None
        ),
        (
          "AA123456A",
          "2024-25",
          "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2",
          BAD_REQUEST,
          BadRequestError,
          mtdRequestWithZeroAndOtherAdjustments(false),
          Some(
            ErrorWrapper(
              "123",
              BadRequestError,
              Some(List(RuleBothAdjustmentsSuppliedError, RuleZeroAdjustmentsInvalidError.withPath("/zeroAdjustments")))
            )
          )
        ),
        (
          "AA123456A",
          "2024-25",
          "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2",
          BAD_REQUEST,
          RuleZeroAdjustmentsInvalidError.withPath("/zeroAdjustments"),
          mtdRequestWithOnlyZeroAdjustments(false),
          None
        )
      )

      input.foreach(args => (validationErrorTest _).tupled(args))

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new HipTest {
            override def setupStubs(): Unit =
              DownstreamStub.onError(DownstreamStub.PUT, downstreamUrl, downstreamStatus, errorBody(downstreamCode))

            val response: WSResponse = await(request().post(mtdRequestJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val errors = List(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
          (UNPROCESSABLE_ENTITY, "ASC_ID_INVALID", BAD_REQUEST, RuleSummaryStatusInvalid),
          (CONFLICT, "ASC_ALREADY_SUPERSEDED", BAD_REQUEST, RuleSummaryStatusSuperseded),
          (CONFLICT, "ASC_ALREADY_ADJUSTED", BAD_REQUEST, RuleAlreadyAdjusted),
          (UNPROCESSABLE_ENTITY, "UNALLOWABLE_VALUE", BAD_REQUEST, RuleResultingValueNotPermitted),
          (FORBIDDEN, "BVR_FAILURE_C55316", BAD_REQUEST, RuleOverConsolidatedExpensesThreshold),
          (FORBIDDEN, "BVR_FAILURE_C15320", BAD_REQUEST, RuleTradingIncomeAllowanceClaimed),
          (FORBIDDEN, "BVR_FAILURE_C55503", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C55508", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C55509", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C559107", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C559103", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C559099", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "INCOMESOURCE_TYPE_NOT_MATCHED", BAD_REQUEST, RuleTypeOfBusinessIncorrectError),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "OUTSIDE_AMENDMENT_WINDOW", BAD_REQUEST, RuleOutsideAmendmentWindowError)
        )

        val extraTysErrors = List(
          (UNPROCESSABLE_ENTITY, "INCOME_SOURCE_TYPE_NOT_MATCHED", BAD_REQUEST, RuleTypeOfBusinessIncorrectError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (BAD_REQUEST, "RULE_TAX_YEAR_RANGE_INVALID", BAD_REQUEST, RuleTaxYearRangeInvalidError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

}
