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

package v2.endpoints

import api.models.errors._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{ JsValue, Json }
import play.api.libs.ws.{ WSRequest, WSResponse }
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v2.models.errors._
import v2.stubs._

class SubmitForeignPropertyBsasControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino   = "AA123456A"
    val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

    val nrsSuccess: JsValue = Json.parse(
      s"""
         |{
         |  "nrSubmissionId":"2dd537bc-4244-4ebf-bac9-96321be13cdc",
         |  "cadesTSignature":"30820b4f06092a864886f70111111111c0445c464",
         |  "timestamp":""
         |}
         """.stripMargin
    )

    val desResponse: String => String = (typeOfBusiness: String) => s"""
        |{
        |    "metadata": {
        |        "calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
        |        "requestedDateTime": "2000-01-01T10:10:10Z",
        |        "taxableEntityId": "AA123456A",
        |        "taxYear": 2020,
        |        "status": "valid"
        |    },
        |    "inputs": {
        |        "incomeSourceId": "111111111111111",
        |        "incomeSourceType": "$typeOfBusiness",
        |        "accountingPeriodStartDate": "2000-01-01",
        |        "accountingPeriodEndDate": "2000-01-01",
        |        "source": "MTD-SA",
        |        "submissionPeriods": [
        |            {
        |                "periodId": "0000000000000000",
        |                "startDate": "2000-01-01",
        |                "endDate": "2000-01-01",
        |                "receivedDateTime": "2000-01-01T10:10:10Z"
        |            }
        |        ]
        |    },
        |    "adjustableSummaryCalculation": {
        |        "totalIncome": 0.02,
        |        "income": {
        |            "turnover": 0.02,
        |            "other": 0.02
        |        },
        |        "totalExpenses": 0.02,
        |        "expenses": {
        |            "consolidatedExpenses": 0.02,
        |            "costOfGoodsAllowable": 0.02,
        |            "paymentsToSubcontractorsAllowable": 0.02,
        |            "wagesAndStaffCostsAllowable": 0.02,
        |            "carVanTravelExpensesAllowable": 0.02,
        |            "premisesRunningCostsAllowable": 0.02,
        |            "maintenanceCostsAllowable": 0.02,
        |            "adminCostsAllowable": 0.02,
        |            "interestOnBankOtherLoansAllowable": 0.02,
        |            "financeChargesAllowable": 0.02,
        |            "irrecoverableDebtsAllowable": 0.02,
        |            "professionalFeesAllowable": 0.02,
        |            "depreciationAllowable": 0.02,
        |            "otherExpensesAllowable": 0.02,
        |            "advertisingCostsAllowable": 0.02,
        |            "businessEntertainmentCostsAllowable": 0.02
        |        },
        |        "netProfit": 0.02,
        |        "netLoss": 0.02,
        |        "totalAdditions": 0.02,
        |        "additions": {
        |            "costOfGoodsDisallowable": 0.02,
        |            "paymentsToSubcontractorsDisallowable": 0.02,
        |            "wagesAndStaffCostsDisallowable": 0.02,
        |            "carVanTravelExpensesDisallowable": 0.02,
        |            "premisesRunningCostsDisallowable": 0.02,
        |            "maintenanceCostsDisallowable": 0.02,
        |            "adminCostsDisallowable": 0.02,
        |            "interestOnBankOtherLoansDisallowable": 0.02,
        |            "financeChargesDisallowable": 0.02,
        |            "irrecoverableDebtsDisallowable": 0.02,
        |            "professionalFeesDisallowable": 0.02,
        |            "depreciationDisallowable": 0.02,
        |            "otherExpensesDisallowable": 0.02,
        |            "advertisingCostsDisallowable": 0.02,
        |            "businessEntertainmentCostsDisallowable": 0.02,
        |            "outstandingBusinessIncome": 0.02,
        |            "balancingChargeOther": 0.02,
        |            "balancingChargeBpra": 0.02,
        |            "goodAndServicesOwnUse": 0.02
        |        },
        |        "totalDeductions": 0.02,
        |        "deductions": {
        |            "tradingAllowance": 0.02,
        |            "annualInvestmentAllowance": 0.02,
        |            "capitalAllowanceMainPool": 0.02,
        |            "capitalAllowanceSpecialRatePool": 0.02,
        |            "zeroEmissionGoods": 0.02,
        |            "businessPremisesRenovationAllowance": 0.02,
        |            "enhancedCapitalAllowance": 0.02,
        |            "allowanceOnSales": 0.02,
        |            "capitalAllowanceSingleAssetPool": 0.02,
        |            "includedNonTaxableProfits": 0.02
        |        },
        |        "accountingAdjustments": 0.02,
        |        "selfEmploymentAccountingAdjustments": {
        |            "basisAdjustment": 0.02,
        |            "overlapReliefUsed": 0.02,
        |            "accountingAdjustment": 0.02,
        |            "averagingAdjustment": 0.02
        |        },
        |        "taxableProfit": 0.02,
        |        "adjustedIncomeTaxLoss": 0.02
        |    },
        |    "adjustments": {
        |        "income": {
        |            "turnover": 0.02,
        |            "other": 0.02
        |        },
        |        "expenses": {
        |            "consolidatedExpenses": 0.02,
        |            "costOfGoodsAllowable": 0.02,
        |            "paymentsToSubcontractorsAllowable": 0.02,
        |            "wagesAndStaffCostsAllowable": 0.02,
        |            "carVanTravelExpensesAllowable": 0.02,
        |            "premisesRunningCostsAllowable": 0.02,
        |            "maintenanceCostsAllowable": 0.02,
        |            "adminCostsAllowable": 0.02,
        |            "interestOnBankOtherLoansAllowable": 0.02,
        |            "financeChargesAllowable": 0.02,
        |            "irrecoverableDebtsAllowable": 0.02,
        |            "professionalFeesAllowable": 0.02,
        |            "depreciationAllowable": 0.02,
        |            "otherExpensesAllowable": 0.02,
        |            "advertisingCostsAllowable": 0.02,
        |            "businessEntertainmentCostsAllowable": 0.02
        |        },
        |        "additions": {
        |            "costOfGoodsDisallowable": 0.02,
        |            "paymentsToSubcontractorsDisallowable": 0.02,
        |            "wagesAndStaffCostsDisallowable": 0.02,
        |            "carVanTravelExpensesDisallowable": 0.02,
        |            "premisesRunningCostsDisallowable": 0.02,
        |            "maintenanceCostsDisallowable": 0.02,
        |            "adminCostsDisallowable": 0.02,
        |            "interestOnBankOtherLoansDisallowable": 0.02,
        |            "financeChargesDisallowable": 0.02,
        |            "irrecoverableDebtsDisallowable": 0.02,
        |            "professionalFeesDisallowable": 0.02,
        |            "depreciationDisallowable": 0.02,
        |            "otherExpensesDisallowable": 0.02,
        |            "advertisingCostsDisallowable": 0.02,
        |            "businessEntertainmentCostsDisallowable": 0.02
        |        }
        |    },
        |    "adjustedSummaryCalculation": {
        |        "totalIncome": 0.02,
        |        "income": {
        |            "turnover": 0.02,
        |            "other": 0.02
        |        },
        |        "totalExpenses": 0.02,
        |        "expenses": {
        |            "consolidatedExpenses": 0.02,
        |            "costOfGoodsAllowable": 0.02,
        |            "paymentsToSubcontractorsAllowable": 0.02,
        |            "wagesAndStaffCostsAllowable": 0.02,
        |            "carVanTravelExpensesAllowable": 0.02,
        |            "premisesRunningCostsAllowable": 0.02,
        |            "maintenanceCostsAllowable": 0.02,
        |            "adminCostsAllowable": 0.02,
        |            "interestOnBankOtherLoansAllowable": 0.02,
        |            "financeChargesAllowable": 0.02,
        |            "irrecoverableDebtsAllowable": 0.02,
        |            "professionalFeesAllowable": 0.02,
        |            "depreciationAllowable": 0.02,
        |            "otherExpensesAllowable": 0.02,
        |            "advertisingCostsAllowable": 0.02,
        |            "businessEntertainmentCostsAllowable": 0.02
        |        },
        |        "netProfit": 0.02,
        |        "netLoss": 0.02,
        |        "totalAdditions": 0.02,
        |        "additions": {
        |            "costOfGoodsDisallowable": 0.02,
        |            "paymentsToSubcontractorsDisallowable": 0.02,
        |            "wagesAndStaffCostsDisallowable": 0.02,
        |            "carVanTravelExpensesDisallowable": 0.02,
        |            "premisesRunningCostsDisallowable": 0.02,
        |            "maintenanceCostsDisallowable": 0.02,
        |            "adminCostsDisallowable": 0.02,
        |            "interestOnBankOtherLoansDisallowable": 0.02,
        |            "financeChargesDisallowable": 0.02,
        |            "irrecoverableDebtsDisallowable": 0.02,
        |            "professionalFeesDisallowable": 0.02,
        |            "depreciationDisallowable": 0.02,
        |            "otherExpensesDisallowable": 0.02,
        |            "advertisingCostsDisallowable": 0.02,
        |            "businessEntertainmentCostsDisallowable": 0.02,
        |            "outstandingBusinessIncome": 0.02,
        |            "balancingChargeOther": 0.02,
        |            "balancingChargeBpra": 0.02,
        |            "goodAndServicesOwnUse": 0.02
        |        },
        |        "totalDeductions": 0.02,
        |        "deductions": {
        |            "tradingAllowance": 0.02,
        |            "annualInvestmentAllowance": 0.02,
        |            "capitalAllowanceMainPool": 0.02,
        |            "capitalAllowanceSpecialRatePool": 0.02,
        |            "zeroEmissionGoods": 0.02,
        |            "businessPremisesRenovationAllowance": 0.02,
        |            "enhancedCapitalAllowance": 0.02,
        |            "allowanceOnSales": 0.02,
        |            "capitalAllowanceSingleAssetPool": 0.02,
        |            "includedNonTaxableProfits": 0.02
        |        },
        |        "accountingAdjustments": 0.02,
        |        "selfEmploymentAccountingAdjustments": {
        |            "basisAdjustment": 0.02,
        |            "overlapReliefUsed": 0.02,
        |            "accountingAdjustment": 0.02,
        |            "averagingAdjustment": 0.02
        |        },
        |        "taxableProfit": 0.02,
        |        "adjustedIncomeTaxLoss": 0.02
        |    }
        |}
        |""".stripMargin

    val requestBodyForeignPropertyJson: JsValue = Json.parse("""
         |{
         |  "foreignProperty": [
         |    {
         |      "countryCode": "FRA",
         |      "income": {
         |        "rentIncome": 123.12,
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
        |  "foreignProperty": [
        |    {
        |      "countryCode": "FRA",
        |      "income": {
        |        "rentIncome": 123.12,
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
         |            "rentIncome": 123.12
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
         |            "rentIncome": 123.12
         |        },
         |        "expenses": {
         |            "consolidatedExpenses": 123.12
         |        }
         |    }
         |}
         |""".stripMargin)

    val responseBody: JsValue = Json.parse(s"""
         |{
         |  "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
         |  "links":[
         |    {
         |      "href":"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId/adjust",
         |      "rel":"self",
         |      "method":"GET"
         |    },
         |    {
         |      "href":"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId?adjustedStatus=true",
         |      "rel":"retrieve-adjustable-summary",
         |      "method":"GET"
         |    }
         |  ]
         |}
         |""".stripMargin)

    def setupStubs(): StubMapping

    def uri: String = s"/$nino/foreign-property/$bsasId/adjust"

    def desUrl: String = s"/income-tax/adjustable-summary-calculation/$nino/$bsasId"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.2.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "des message"
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
          DesStub.onSuccess(DesStub.PUT, desUrl, OK, Json.parse(desResponse("15")))
        }

        val response: WSResponse = await(request().post(requestBodyForeignPropertyJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Deprecation") shouldBe Some(
          "This endpoint is deprecated. See the service guide: https://developer.service.hmrc.gov.uk/guides/income-tax-mtd-end-to-end-service-guide/")
      }

      "any valid foreignProperty request is made with a failed nrs call" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          NrsStub.onError(NrsStub.PUT, s"/mtd-api-nrs-proxy/$nino/itsa-annual-adjustment", INTERNAL_SERVER_ERROR, "An internal server error occurred")
          DesStub.onSuccess(DesStub.PUT, desUrl, OK, Json.parse(desResponse("15")))
        }

        val response: WSResponse = await(request().post(requestBodyForeignPropertyJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Deprecation") shouldBe Some(
          "This endpoint is deprecated. See the service guide: https://developer.service.hmrc.gov.uk/guides/income-tax-mtd-end-to-end-service-guide/")
      }

      "any valid foreignFhlEea request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.PUT, desUrl, OK, Json.parse(desResponse("03")))
        }

        val response: WSResponse = await(request().post(requestBodyForeignFhlEeaJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Deprecation") shouldBe Some(
          "This endpoint is deprecated. See the service guide: https://developer.service.hmrc.gov.uk/guides/income-tax-mtd-end-to-end-service-guide/")
      }

      "any valid foreignProperty consolidated request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.PUT, desUrl, OK, Json.parse(desResponse("15")))
        }

        val response: WSResponse = await(request().post(requestBodyForeignPropertyConsolidatedJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Deprecation") shouldBe Some(
          "This endpoint is deprecated. See the service guide: https://developer.service.hmrc.gov.uk/guides/income-tax-mtd-end-to-end-service-guide/")
      }

      "any valid foreignFhlEea consolidated request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.PUT, desUrl, OK, Json.parse(desResponse("03")))
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

      val requestBodyAdjustmentValue: JsValue = Json.parse(s"""
           |{
           |    "foreignFhlEea": {
           |        "income": {
           |            "rentIncome": 123.12
           |        },
           |        "expenses": {
           |            "consolidatedExpenses": 123.12345
           |        }
           |    }
           |}
           |""".stripMargin)

      val requestBodyRangeInvalid: JsValue = Json.parse(s"""
           |{
           |    "foreignFhlEea": {
           |        "income": {
           |            "rentIncome": 123.12
           |        },
           |        "expenses": {
           |            "consolidatedExpenses": -100000000000.99
           |        }
           |    }
           |}
           |""".stripMargin)

      val requestBodyIncorrectBody: JsValue = Json.parse(s"""
           |{
           |    "foreignFhlEea": {
           |        "income": {},
           |        "expenses": {}
           |    }
           |}
           |""".stripMargin)

      val requestBodyBothExpenses: JsValue = Json.parse(s"""
           |{
           |  "foreignProperty": [
           |    {
           |      "countryCode": "FRA",
           |      "income": {
           |        "rentIncome": 123.12,
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
           |  "foreignProperty": [
           |    {
           |      "countryCode": "FRE",
           |      "income": {
           |        "rentIncome": 123.12,
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

      val requestBodyUnformattedCountryCode: JsValue = Json.parse(s"""
           |{
           |  "foreignProperty": [
           |    {
           |      "countryCode": "FRANCE",
           |      "income": {
           |        "rentIncome": 123.12,
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

      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestBsasId: String,
                                requestBody: JsValue,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String                            = requestNino
            override val bsasId: String                          = requestBsasId
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
          ("Walrus", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", validRequestBody, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "Walrus", validRequestBody, BAD_REQUEST, BsasIdFormatError),
          ("AA123456A",
           "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
           requestBodyAdjustmentValue,
           BAD_REQUEST,
           FormatAdjustmentValueError.copy(paths = Some(Seq("/foreignFhlEea/expenses/consolidatedExpenses")))),
          ("AA123456A",
           "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
           requestBodyRangeInvalid,
           BAD_REQUEST,
           RuleAdjustmentRangeInvalid.copy(paths = Some(Seq("/foreignFhlEea/expenses/consolidatedExpenses")))),
          ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", requestBodyIncorrectBody, BAD_REQUEST, RuleIncorrectOrEmptyBodyError),
          ("AA123456A", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", requestBodyBothExpenses, BAD_REQUEST, RuleBothExpensesError),
          ("AA123456A",
           "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
           requestBodyInvalidCountryCode,
           BAD_REQUEST,
           RuleCountryCodeError.copy(paths = Some(Seq("/foreignProperty/0/countryCode")))),
          ("AA123456A",
           "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
           requestBodyUnformattedCountryCode,
           BAD_REQUEST,
           CountryCodeFormatError.copy(paths = Some(Seq("/foreignProperty/0/countryCode"))))
        )
        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "des service error" when {
        def serviceErrorTest(desStatus: Int, desCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"des returns an $desCode error and status $desStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DesStub.onError(DesStub.PUT, desUrl, desStatus, errorBody(desCode))
            }

            val response: WSResponse = await(request().post(requestBodyForeignPropertyJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, BsasIdFormatError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INCOMESOURCE_TYPE_NOT_MATCHED", FORBIDDEN, RuleTypeOfBusinessError),
          (FORBIDDEN, "ASC_ID_INVALID", FORBIDDEN, RuleSummaryStatusInvalid),
          (FORBIDDEN, "ASC_ALREADY_SUPERSEDED", FORBIDDEN, RuleSummaryStatusSuperseded),
          (FORBIDDEN, "ASC_ALREADY_ADJUSTED", FORBIDDEN, RuleBsasAlreadyAdjusted),
          (FORBIDDEN, "UNALLOWABLE_VALUE", FORBIDDEN, RuleResultingValueNotPermitted),
          (FORBIDDEN, "BVR_FAILURE_C55316", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C15320", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "BVR_FAILURE_C55503", FORBIDDEN, RuleOverConsolidatedExpensesThreshold),
          (FORBIDDEN, "BVR_FAILURE_C55508", FORBIDDEN, RulePropertyIncomeAllowanceClaimed),
          (FORBIDDEN, "BVR_FAILURE_C55509", FORBIDDEN, RulePropertyIncomeAllowanceClaimed),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )
        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
