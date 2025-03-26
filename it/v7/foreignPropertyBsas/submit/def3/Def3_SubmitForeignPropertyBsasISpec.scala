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

package v7.foreignPropertyBsas.submit.def3

import common.errors._
import play.api.http.HeaderNames.ACCEPT
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers._
import shared.models.errors._
import shared.models.utils.JsonErrorValidators
import shared.services._
import shared.support.IntegrationBaseSpec
import v7.foreignPropertyBsas.submit.def3.model.request.SubmitForeignPropertyBsasFixtures._

class Def3_SubmitForeignPropertyBsasISpec extends IntegrationBaseSpec with JsonErrorValidators {

  "Calling the submit foreign property bsas endpoint" should {
    "return a 200 status code" when {
      List(
        ("without zero adjustments", mtdRequestForeignPropertyValid, downstreamRequestValid),
        (
          "with only zero adjustments set to true",
          mtdRequestWithOnlyZeroAdjustments(true),
          downstreamRequestWithOnlyZeroAdjustments
        )
      ).foreach { case (scenario, mtdRequestBodyJson, downstreamRequestBodyJson) =>
        s"any valid request $scenario is made for TYS" in new TysTest {
          override def setupStubs(): Unit = stubDownstreamSuccess(downstreamRequestBodyJson)

          val response: WSResponse = await(request().post(mtdRequestBodyJson))
          response.status shouldBe OK
          response.header("X-CorrelationId") should not be empty
        }
      }
    }

    "return error according to spec" when {
      def requestBodyWithCountryCode(code: String): JsValue = Json.parse(
        s"""
          |{
          |    "foreignProperty": {
          |        "countryLevelDetail": [
          |            {
          |                "countryCode": "$code",
          |                "income": {
          |                    "totalRentsReceived": 123.12
          |                }
          |            }
          |        ]
          |    }
          |}
        """.stripMargin
      )

      def validationErrorTest(requestNino: String,
                              requestCalculationId: String,
                              requestTaxYear: String,
                              requestBody: JsValue,
                              expectedStatus: Int,
                              expectedBody: MtdError,
                              errorWrapper: Option[ErrorWrapper]): Unit = {
        s"validation fails with ${expectedBody.code} error" in new TysTest {
          override val nino: String          = requestNino
          override val calculationId: String = requestCalculationId
          override val taxYear: String       = requestTaxYear

          val expectedBodyJson: JsValue = errorWrapper match {
            case Some(wrapper) => Json.toJson(wrapper)
            case None          => Json.toJson(expectedBody)
          }

          val response: WSResponse = await(request().post(requestBody))
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBodyJson)
        }
      }

      val input = List(
        ("Walrus", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2025-26", mtdRequestForeignPropertyValid, BAD_REQUEST, NinoFormatError, None),
        ("AA123456A", "BAD_CALC_ID", "2025-26", mtdRequestForeignPropertyValid, BAD_REQUEST, CalculationIdFormatError, None),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "BAD_TAX_YEAR", mtdRequestForeignPropertyValid, BAD_REQUEST, TaxYearFormatError, None),
        (
          "AA123456A",
          "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          "2022-24",
          mtdRequestForeignPropertyValid,
          BAD_REQUEST,
          RuleTaxYearRangeInvalidError,
          None),
        ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2025-26", JsObject.empty, BAD_REQUEST, RuleIncorrectOrEmptyBodyError, None),
        (
          "AA123456A",
          "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          "2025-26",
          mtdRequestWithZeroAndOtherAdjustments(true),
          BAD_REQUEST,
          RuleBothAdjustmentsSuppliedError.withPath("/foreignProperty"),
          None
        ),
        (
          "AA123456A",
          "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          "2025-26",
          mtdRequestWithZeroAndOtherAdjustments(false),
          BAD_REQUEST,
          BadRequestError,
          Some(
            ErrorWrapper(
              "123",
              BadRequestError,
              Some(
                List(
                  RuleBothAdjustmentsSuppliedError.withPath("/foreignProperty"),
                  RuleZeroAdjustmentsInvalidError.withPath("/foreignProperty/zeroAdjustments")
                )
              )
            )
          )
        ),
        (
          "AA123456A",
          "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          "2025-26",
          mtdRequestWithOnlyZeroAdjustments(false),
          BAD_REQUEST,
          RuleZeroAdjustmentsInvalidError.withPath("/foreignProperty/zeroAdjustments"),
          None
        ),
        (
          "AA123456A",
          "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          "2025-26",
          mtdRequestForeignPropertyFull,
          BAD_REQUEST,
          RuleBothExpensesError.copy(paths = Some(List("/foreignProperty/countryLevelDetail/0/expenses"))),
          None
        ),
        (
          "AA123456A",
          "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          "2025-26",
          requestBodyWithCountryCode("XXX"),
          BAD_REQUEST,
          RuleCountryCodeError.copy(paths = Some(List("/foreignProperty/countryLevelDetail/0/countryCode"))),
          None
        ),
        (
          "AA123456A",
          "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          "2025-26",
          requestBodyWithCountryCode("FRANCE"),
          BAD_REQUEST,
          CountryCodeFormatError.copy(paths = Some(List("/foreignProperty/countryLevelDetail/0/countryCode"))),
          None
        ),
        (
          "AA123456A",
          "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          "2025-26",
          mtdRequestForeignPropertyInvalidResidentialCost,
          BAD_REQUEST,
          ValueFormatError.copy(
            message = "The value must be between 0 and 99999999999.99",
            paths = Some(List("/foreignProperty/countryLevelDetail/0/expenses/residentialFinancialCost"))
          ),
          None
        )
      )

      input.foreach(args => (validationErrorTest _).tupled(args))

      "service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new TysTest {
            override def setupStubs(): Unit =
              DownstreamStub.onError(DownstreamStub.PUT, downstreamUrl, downstreamStatus, errorBody(downstreamCode))

            val response: WSResponse = await(request().post(mtdRequestForeignPropertyValid))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val errors = List(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C15320", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C55508", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C55509", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C559107", BAD_REQUEST, RulePropertyIncomeAllowanceClaimed),
          (FORBIDDEN, "BVR_FAILURE_C559103", BAD_REQUEST, RulePropertyIncomeAllowanceClaimed),
          (FORBIDDEN, "BVR_FAILURE_C559099", BAD_REQUEST, RuleOverConsolidatedExpensesThreshold),
          (FORBIDDEN, "BVR_FAILURE_C55503", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C55316", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (CONFLICT, "ASC_ALREADY_SUPERSEDED", BAD_REQUEST, RuleSummaryStatusSuperseded),
          (CONFLICT, "ASC_ALREADY_ADJUSTED", BAD_REQUEST, RuleAlreadyAdjusted),
          (UNPROCESSABLE_ENTITY, "UNALLOWABLE_VALUE", BAD_REQUEST, RuleResultingValueNotPermitted),
          (UNPROCESSABLE_ENTITY, "OUTSIDE_AMENDMENT_WINDOW", BAD_REQUEST, RuleOutsideAmendmentWindowError),
          (UNPROCESSABLE_ENTITY, "ASC_ID_INVALID", BAD_REQUEST, RuleSummaryStatusInvalid),
          (UNPROCESSABLE_ENTITY, "INCOMESOURCE_TYPE_NOT_MATCHED", BAD_REQUEST, RuleTypeOfBusinessIncorrectError)
        )

        val extraTysErrors = List(
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "INCOME_SOURCE_TYPE_NOT_MATCHED", BAD_REQUEST, RuleTypeOfBusinessIncorrectError)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

  private trait Test {

    val nino: String          = "AA123456A"
    val calculationId: String = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"

    def downstreamUrl: String

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
      buildRequest(s"/$nino/foreign-property/$calculationId/adjust/$taxYear")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.7.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def taxYear: String

    def setupStubs(): Unit = ()

    def errorBody(code: String): String =
      s"""
        |      {
        |        "code": "$code",
        |        "reason": "message"
        |      }
      """.stripMargin

  }

  private trait TysTest extends Test {
    override def taxYear: String = "2025-26"

    def downstreamUrl: String = s"/income-tax/adjustable-summary-calculation/25-26/$nino/$calculationId"

  }

}
