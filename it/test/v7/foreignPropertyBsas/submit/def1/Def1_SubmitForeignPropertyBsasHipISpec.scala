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

package v7.foreignPropertyBsas.submit.def1

import common.errors.*
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.*
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors.*
import shared.services.*
import shared.support.IntegrationBaseSpec
import v7.foreignPropertyBsas.submit.def1.model.request.SubmitForeignPropertyBsasFixtures.{downstreamRequestValid, mtdRequestFull, mtdRequestValid}

class Def1_SubmitForeignPropertyBsasHipISpec extends IntegrationBaseSpec {

  "Calling the submit foreign property bsas endpoint" should {
    "return a 200 status code" when {
      "a valid request is made for TYS" in new HipTest {
        override def setupStubs(): Unit = {
          stubDownstreamSuccess()
        }

        val response: WSResponse = await(request().post(mtdRequestValid))
        response.status shouldBe OK
        response.header("X-CorrelationId") should not be empty
      }

    }

    "return error according to spec" when {

      def requestBodyWithCountryCode(code: String): JsValue = Json.parse(s"""
           |{
           |  "foreignProperty": [
           |    {
           |      "countryCode": "$code",
           |      "income": {
           |        "totalRentsReceived": 123.12
           |      }
           |    }
           |  ]
           |}
           |""".stripMargin)

      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestCalculationId: String,
                                requestTaxYear: String,
                                requestBody: JsValue,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new HipTest {

            override val nino: String          = requestNino
            override val calculationId: String = requestCalculationId
            override val taxYear: String       = requestTaxYear

            val response: WSResponse = await(request().post(requestBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = List(
          ("Walrus", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2023-24", mtdRequestValid, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "BAD_CALC_ID", "2023-24", mtdRequestValid, BAD_REQUEST, CalculationIdFormatError),
          ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "BAD_TAX_YEAR", mtdRequestValid, BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2022-24", mtdRequestValid, BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "2023-24", JsObject.empty, BAD_REQUEST, RuleIncorrectOrEmptyBodyError),
          (
            "AA123456A",
            "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
            "2023-24",
            mtdRequestFull,
            BAD_REQUEST,
            RuleBothExpensesError.copy(paths = Some(List("/foreignProperty/0/expenses")))),
          (
            "AA123456A",
            "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
            "2023-24",
            requestBodyWithCountryCode("XXX"),
            BAD_REQUEST,
            RuleCountryCodeError.copy(paths = Some(List("/foreignProperty/0/countryCode")))),
          (
            "AA123456A",
            "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
            "2023-24",
            requestBodyWithCountryCode("FRANCE"),
            BAD_REQUEST,
            CountryCodeFormatError.copy(paths = Some(List("/foreignProperty/0/countryCode"))))
        )
        input.foreach(validationErrorTest.tupled)
      }

      "service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new HipTest {

            override def setupStubs(): Unit =
              DownstreamStub.onError(DownstreamStub.PUT, downstreamUrl, downstreamStatus, errorBody(downstreamCode))

            val response: WSResponse = await(request().post(mtdRequestValid))
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
          (UNPROCESSABLE_ENTITY, "ASC_ID_INVALID", BAD_REQUEST, RuleSummaryStatusInvalid),
          (UNPROCESSABLE_ENTITY, "INCOMESOURCE_TYPE_NOT_MATCHED", BAD_REQUEST, RuleTypeOfBusinessIncorrectError)
        )

        val extraTysErrors = List(
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "INCOME_SOURCE_TYPE_NOT_MATCHED", BAD_REQUEST, RuleTypeOfBusinessIncorrectError)
        )

        (errors ++ extraTysErrors).foreach(serviceErrorTest.tupled)
      }
    }
  }

  private trait Test {

    val nino: String          = "AA123456A"
    val calculationId: String = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"
    // Downstream returns the adjustments and adjusted calculation - we ignore whatever we get back...
    val ignoredDownstreamResponse: JsValue = Json.parse("""{"ignored": "doesn't matter"}""")

    def downstreamUrl: String

    def stubDownstreamSuccess(): Unit = {
      DownstreamStub
        .when(DownstreamStub.PUT, downstreamUrl)
        .withRequestBody(downstreamRequestValid)
        .thenReturn(OK, ignoredDownstreamResponse)
    }

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
         |{
         |  "response": {
         |    "failures": [
         |      {
         |        "type": "$code",
         |        "reason": "message"
         |      }
         |    ]
         |  }
         |}
    """.stripMargin

  }

  private trait HipTest extends Test {
    override def taxYear: String = "2023-24"

    def downstreamUrl: String = s"/itsa/income-tax/v1/23-24/adjustable-summary-calculation/$nino/$calculationId"

  }

}
