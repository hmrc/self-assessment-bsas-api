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
 * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v3.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{ JsValue, Json }
import play.api.libs.ws.{ WSRequest, WSResponse }
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v3.models.errors._
import v3.stubs.{ AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub, NrsStub }

class SubmitForeignPropertyBsasControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino: String            = "AA123456A"
    val calculationId: String   = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"
    val taxYear: Option[String] = None

    val nrsSuccess: JsValue = Json.parse(
      s"""
         |{
         |  "nrSubmissionId":"2dd537bc-4244-4ebf-bac9-96321be13cdc",
         |  "cadesTSignature":"30820b4f06092a864886f70111111111c0445c464",
         |  "timestamp":""
         |}
         """.stripMargin
    )

    val requestBodyForeignPropertyJson: JsValue = Json.parse("""
         |{
         |  "nonFurnishedHolidayLet": [
         |    {
         |      "countryCode": "FRA",
         |      "income": {
         |        "totalRentsReceived": 123.12,
         |        "premiumsOfLeaseGrant": 123.12,
         |        "foreignTaxTakenOff": 123.12,
         |        "otherPropertyIncome": 123.12
         |      },
         |      "expenses": {
         |        "premisesRunningCosts": 123.12,
         |        "repairsAndMaintenance": 123.12,
         |        "financialCosts": 123.12,
         |        "professionalFees": 123.12,
         |        "travelCosts": 123.12,
         |        "costOfServices": 123.12,
         |        "residentialFinancialCost": 123.12,
         |        "other": 123.12
         |      }
         |    }
         |  ]
         |}
         |""".stripMargin)

    val requestBodyForeignPropertyConsolidatedJson: JsValue = Json.parse("""
        |{
        |  "nonFurnishedHolidayLet": [
        |    {
        |      "countryCode": "FRA",
        |      "income": {
        |        "totalRentsReceived": 123.12,
        |        "premiumsOfLeaseGrant": 123.12,
        |        "foreignTaxTakenOff": 123.12,
        |        "otherPropertyIncome": 123.12
        |      },
        |      "expenses": {
        |        "residentialFinancialCost": 123.12,
        |        "consolidatedExpenses": 123.12
        |      }
        |    }
        |  ]
        |}
        |""".stripMargin)

    val requestBodyForeignFhlEeaJson: JsValue = Json.parse("""
         |{
         |    "foreignFhlEea": {
         |        "income": {
         |            "totalRentsReceived": 123.12
         |        },
         |        "expenses": {
         |            "premisesRunningCosts": 123.12,
         |            "repairsAndMaintenance": 123.12,
         |            "financialCosts": 123.12,
         |            "professionalFees": 123.12,
         |            "costOfServices": 123.12,
         |            "travelCosts": 123.12,
         |            "other": 123.12
         |        }
         |    }
         |}
         |""".stripMargin)

    val requestBodyForeignFhlEeaConsolidatedJson: JsValue = Json.parse("""
         |{
         |    "foreignFhlEea": {
         |        "income": {
         |            "totalRentsReceived": 123.12
         |        },
         |        "expenses": {
         |            "consolidatedExpenses": 123.12
         |        }
         |    }
         |}
         |""".stripMargin)

    val responseBody: JsValue = Json.parse(s"""
         |{
         |  "links":[
         |    {
         |      "href":"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$calculationId",
         |      "rel":"self",
         |      "method":"GET"
         |    }
         |  ]
         |}
         |""".stripMargin)

    def setupStubs(): StubMapping

    def uri: String = s"/$nino/foreign-property/$calculationId/adjust"

    def downstreamUrl: String = s"/income-tax/adjustable-summary-calculation/$nino/$calculationId"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withQueryStringParameters(taxYear.map(ty => Seq("taxYear" -> ty)).getOrElse(Nil): _*)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.3.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "message"
         |      }
    """.stripMargin
  }

  "Calling the submit foreign property bsas endpoint" should {

    "return a 200 status code" when {

      "any valid foreignProperty request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          NrsStub.onSuccess(NrsStub.PUT, s"/mtd-api-nrs-proxy/$nino/itsa-annual-adjustment", ACCEPTED, nrsSuccess)
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUrl, OK)
        }

        val response: WSResponse = await(request().post(requestBodyForeignPropertyJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }

      "any valid foreignProperty request is made with a failed nrs call" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          NrsStub.onError(NrsStub.PUT, s"/mtd-api-nrs-proxy/$nino/itsa-annual-adjustment", INTERNAL_SERVER_ERROR, "An internal server error occurred")
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUrl, OK)
        }

        val response: WSResponse = await(request().post(requestBodyForeignPropertyJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }

      "any valid foreignFhlEea request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUrl, OK)
        }

        val response: WSResponse = await(request().post(requestBodyForeignFhlEeaJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
      "any valid foreignProperty consolidated request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUrl, OK)
        }

        val response: WSResponse = await(request().post(requestBodyForeignPropertyConsolidatedJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
      "any valid foreignFhlEea consolidated request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUrl, OK)
        }

        val response: WSResponse = await(request().post(requestBodyForeignFhlEeaConsolidatedJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }

    "return error according to spec" when {

      val validRequestBody: JsValue = Json.parse(s"""
           |{
           |    "foreignFhlEea": {
           |        "income": {
           |            "rentIncome": 123.12
           |        },
           |        "expenses": {
           |            "consolidatedExpenses": 123.12
           |        }
           |    }
           |}
           |""".stripMargin)

      val requestBodyIncorrectBody: JsValue = Json.parse(s"""
           |{
           |    "foreignFhlEea": {
           |    }
           |}
           |""".stripMargin)

      val requestBodyBothExpenses: JsValue = Json.parse("""
          |{
          |  "nonFurnishedHolidayLet": [
          |    {
          |      "countryCode": "FRA",
          |      "income": {
          |        "totalRentsReceived": 123.12,
          |        "premiumsOfLeaseGrant": 123.12,
          |        "foreignTaxTakenOff": 123.12,
          |        "otherPropertyIncome": 123.12
          |      },
          |      "expenses": {
          |        "premisesRunningCosts": 123.12,
          |        "repairsAndMaintenance": 123.12,
          |        "financialCosts": 123.12,
          |        "professionalFees": 123.12,
          |        "travelCosts": 123.12,
          |        "costOfServices": 123.12,
          |        "residentialFinancialCost": 123.12,
          |        "other": 123.12,
          |        "consolidatedExpenses": 123.12
          |      }
          |    }
          |  ]
          |}
          |""".stripMargin)

      val requestBodyInvalidCountryCode: JsValue = Json.parse(s"""
           |{
           |  "nonFurnishedHolidayLet": [
           |    {
           |      "countryCode": "FRE",
           |      "income": {
           |        "totalRentsReceived": 123.12,
           |        "premiumsOfLeaseGrant": 123.12,
           |        "foreignTaxTakenOff": 123.12,
           |        "otherPropertyIncome": 123.12
           |      },
           |      "expenses": {
           |        "premisesRunningCosts": 123.12,
           |        "repairsAndMaintenance": 123.12,
           |        "financialCosts": 123.12,
           |        "professionalFees": 123.12,
           |        "travelCosts": 123.12,
           |        "costOfServices": 123.12,
           |        "residentialFinancialCost": 123.12,
           |        "other": 123.12
           |      }
           |    }
           |  ]
           |}
           |""".stripMargin)

      val requestBodyUnformattedCountryCode: JsValue = Json.parse(s"""
           |{
           |  "nonFurnishedHolidayLet": [
           |    {
           |      "countryCode": "FRANCE",
           |      "income": {
           |        "totalRentsReceived": 123.12,
           |        "premiumsOfLeaseGrant": 123.12,
           |        "foreignTaxTakenOff": 123.12,
           |        "otherPropertyIncome": 123.12
           |      },
           |      "expenses": {
           |        "premisesRunningCosts": 123.12,
           |        "repairsAndMaintenance": 123.12,
           |        "financialCosts": 123.12,
           |        "professionalFees": 123.12,
           |        "travelCosts": 123.12,
           |        "costOfServices": 123.12,
           |        "residentialFinancialCost": 123.12,
           |        "other": 123.12
           |      }
           |    }
           |  ]
           |}
           |""".stripMargin)

      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestCalculationId: String,
                                requestTaxYear: Option[String],
                                requestBody: JsValue,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String                            = requestNino
            override val calculationId: String                   = requestCalculationId
            override val taxYear: Option[String]                 = requestTaxYear
            override val requestBodyForeignPropertyJson: JsValue = requestBody

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(requestNino)
            }

            val response: WSResponse = await(request().post(requestBodyForeignPropertyJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          ("Walrus", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", None, validRequestBody, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "BAD_CALC_ID", None, validRequestBody, BAD_REQUEST, CalculationIdFormatError),
          ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2022-23"), validRequestBody, BAD_REQUEST, InvalidTaxYearParameterError),
          ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("BAD_TAX_YEAR"), validRequestBody, BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", Some("2022-24"), validRequestBody, BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A",
           "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
           None,
           requestBodyIncorrectBody,
           BAD_REQUEST,
           RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/foreignFhlEea")))),
          ("AA123456A",
           "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
           None,
           requestBodyBothExpenses,
           BAD_REQUEST,
           RuleBothExpensesError.copy(paths = Some(Seq("/nonFurnishedHolidayLet/0/expenses")))),
          ("AA123456A",
           "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
           None,
           requestBodyInvalidCountryCode,
           BAD_REQUEST,
           RuleCountryCodeError.copy(paths = Some(Seq("/nonFurnishedHolidayLet/0/countryCode")))),
          ("AA123456A",
           "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
           None,
           requestBodyUnformattedCountryCode,
           BAD_REQUEST,
           CountryCodeFormatError.copy(paths = Some(Seq("/nonFurnishedHolidayLet/0/countryCode"))))
        )
        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.PUT, downstreamUrl, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().post(requestBodyForeignPropertyJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, DownstreamError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "BVR_FAILURE_C15320", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "BVR_FAILURE_C55508", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "BVR_FAILURE_C55509", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "BVR_FAILURE_C559107", FORBIDDEN, RulePropertyIncomeAllowanceClaimed),
          (FORBIDDEN, "BVR_FAILURE_C559103", FORBIDDEN, RulePropertyIncomeAllowanceClaimed),
          (FORBIDDEN, "BVR_FAILURE_C559099", FORBIDDEN, RuleOverConsolidatedExpensesThreshold),
          (FORBIDDEN, "BVR_FAILURE_C55503", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "BVR_FAILURE_C55316", INTERNAL_SERVER_ERROR, DownstreamError),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (CONFLICT, "ASC_ALREADY_SUPERSEDED", FORBIDDEN, RuleSummaryStatusSuperseded),
          (CONFLICT, "ASC_ALREADY_ADJUSTED", FORBIDDEN, RuleAlreadyAdjusted),
          (UNPROCESSABLE_ENTITY, "UNALLOWABLE_VALUE", FORBIDDEN, RuleResultingValueNotPermitted),
          (UNPROCESSABLE_ENTITY, "ASC_ID_INVALID", FORBIDDEN, RuleSummaryStatusInvalid),
          (UNPROCESSABLE_ENTITY, "INCOMESOURCE_TYPE_NOT_MATCHED", BAD_REQUEST, RuleTypeOfBusinessIncorrectError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError),
        )
        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
