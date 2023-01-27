/*
 * Copyright 2023 HM Revenue & Customs
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

package v2.fixtures.foreignProperty

import java.time.LocalDate

import play.api.libs.json.{JsValue, Json}
import v2.models.domain.TypeOfBusiness
import v2.models.response.retrieveBsas.{AccountingPeriod, Loss, Profit, TotalBsas}
import v2.models.response.retrieveBsas.foreignProperty._

object RetrieveForeignPropertyBsasFixtures {

  private val now = "2020-04-06"
  private val aYearFromNow = "2021-04-05"

  val mtdRetrieveBsasResponseJson: Boolean => JsValue = adjustedSummary =>
    Json.parse(
      s"""{
         |  "metadata": ${mtdMetadataJson(adjustedSummary)},
         |  "bsas": $mtdBsasDetailJson
         |}""".stripMargin
    )

  val mtdMetadataJson: Boolean => JsValue = adjustedSummary =>
    Json.parse(
      s"""{
         |  "typeOfBusiness": "foreign-property",
         |  "accountingPeriod": {
         |    "startDate": "$now",
         |    "endDate": "$aYearFromNow"
         |  },
         |  "taxYear": "2020-21",
         |  "requestedDateTime": "2021-10-14T11:33:27Z",
         |  "bsasId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
         |  "summaryStatus": "valid",
         |  "adjustedSummary": $adjustedSummary
         |}""".stripMargin
    )

  val mtdBsasDetailJson: JsValue = Json.parse(
    s"""{
       |  "total": {
       |    "income": 100.49,
       |    "expenses": 100.49,
       |    "additions": 100.49,
       |    "deductions": 100.49
       |  },
       |  "profit": {
       |    "net": 100.49,
       |    "taxable": 100
       |  },
       |  "loss": {
       |    "net": 100.49,
       |    "adjustedIncomeTax": 100
       |  },
       |  "incomeBreakdown": {
       |    "rentIncome": 100.49,
       |    "premiumsOfLeaseGrant": 100.49,
       |    "otherPropertyIncome": 100.49
       |  },
       |  "expensesBreakdown": {
       |    "premisesRunningCosts": 100.49,
       |    "repairsAndMaintenance": 100.49,
       |    "financialCosts": 100.49,
       |    "professionalFees": 100.49,
       |    "travelCosts": 100.49,
       |    "costOfServices": 100.49,
       |    "residentialFinancialCost": 100.49,
       |    "broughtFwdResidentialFinancialCost": 100.49,
       |    "other": 100.49
       |  },
       |  "countryLevelDetail":[
       |  {
       |    "countryCode": "FRA",
       |    "total": {
       |      "income": 100.49,
       |      "expenses": 100.49,
       |      "additions": 100.49,
       |      "deductions": 100.49
       |    },
       |    "incomeBreakdown": {
       |      "rentIncome": 100.49,
       |      "premiumsOfLeaseGrant": 100.49,
       |      "otherPropertyIncome": 100.49
       |    },
       |    "expensesBreakdown": {
       |      "premisesRunningCosts": 100.49,
       |      "repairsAndMaintenance": 100.49,
       |      "financialCosts": 100.49,
       |      "professionalFees": 100.49,
       |      "travelCosts": 100.49,
       |      "costOfServices": 100.49,
       |      "residentialFinancialCost": 100.49,
       |      "broughtFwdResidentialFinancialCost": 100.49,
       |      "other": 100.49
       |      }
       |   }
       |  ]
       |}""".stripMargin
  )

  val totalBsasModel = TotalBsas(Some(100.49),Some(100.49),Some(100.49),Some(100.49))
  val profitModel = Profit(Some(100.49),Some(100))
  val lossModel = Loss(Some(100.49),Some(100))
  val incomeBreakdownModel = IncomeBreakdown(Some(100.49),Some(100.49),Some(100.49))
  val expensesBreakdownModel = ExpensesBreakdown(Some(100.49),Some(100.49),Some(100.49), Some(100.49),
    Some(100.49),Some(100.49),Some(100.49), Some(100.49), Some(100.49),None)
  val countryLevelDetail = CountryLevelDetail("FRA", totalBsasModel, Some(incomeBreakdownModel), Some(expensesBreakdownModel))
  val accountingPeriodModel = AccountingPeriod(LocalDate.parse("2020-04-06"), LocalDate.parse("2021-04-05"))

  val metadataModel = Metadata(typeOfBusiness = TypeOfBusiness.`foreign-property`,
    accountingPeriod = accountingPeriodModel,
    taxYear = "2020-21",
    requestedDateTime = "2021-10-14T11:33:27Z",
    bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
    summaryStatus = "valid",
    adjustedSummary = true
  )

  val bsasDetailModel = BsasDetail(total = totalBsasModel,
    profit = Some(profitModel),
    loss = Some(lossModel),
    incomeBreakdown = Some(incomeBreakdownModel),
    expensesBreakdown = Some(expensesBreakdownModel),
    countryLevelDetail = Some(Seq(countryLevelDetail)))

  val retrieveForeignPropertyBsasResponse = RetrieveForeignPropertyBsasResponse(metadataModel, Some(bsasDetailModel))

  val hateoasResponseForeignProperty = (nino: String, bsasId: String) =>
    s"""
      |{
      |  "metadata": ${mtdMetadataJson(true)},
      |  "bsas": $mtdBsasDetailJson,
      |   "links":[
      |    {
      |      "href":"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId",
      |      "method":"GET",
      |      "rel":"self"
      |    },
      |    {
      |      "href":"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId/adjust",
      |      "method":"POST",
      |      "rel":"submit-summary-adjustments"
      |    }
      |  ]
      |}
    """.stripMargin

  val desRetrieveBsasResponse: JsValue = Json.parse(
    s"""{
       |    "inputs": {
       |        "incomeSourceType": "15",
       |        "incomeSourceId": "XAIS12345678910",
       |        "accountingPeriodStartDate": "2020-04-06",
       |        "accountingPeriodEndDate": "2021-04-05"
       |    },
       |    "metadata": {
       |        "taxYear": 2021,
       |        "requestedDateTime": "2021-10-14T11:33:27Z",
       |        "calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
       |        "status": "valid",
       |        "adjusted": "True"
       |    },
       |    "adjustable": {
       |        "totalIncome": 100.49,
       |        "totalExpenses": 100.49,
       |        "totalAdditions": 100.49,
       |        "totalDeductions": 100.49,
       |        "accountingAdjustments": 100.49,
       |        "netProfit": 100.49,
       |        "taxableProfit": 100,
       |        "netLoss": 100.49,
       |        "adjustedIncomeTaxLoss": 100,
       |        "income": {
       |            "totalRentsReceived": 100.49,
       |            "premiumsOfLeaseGrant": 100.49,
       |            "otherPropertyIncome": 100.49
       |        },
       |        "expenses": {
       |            "premisesRunningCosts": 100.49,
       |            "repairsAndMaintenance": 100.49,
       |            "financialCosts": 100.49,
       |            "professionalFees": 100.49,
       |            "travelCosts": 100.49,
       |            "costOfServices": 100.49,
       |            "residentialFinancialCost": 100.49,
       |            "broughtFwdResidentialFinancialCost": 100.49,
       |            "other": 100.49
       |        }
       |    },
       |    "adjustedSummaryCalculation": {
       |        "totalIncome": 100.49,
       |        "totalExpenses": 100.49,
       |        "totalAdditions": 100.49,
       |        "totalDeductions": 100.49,
       |        "accountingAdjustments": 100.49,
       |        "netProfit": 100.49,
       |        "taxableProfit": 100,
       |        "netLoss": 100.49,
       |        "adjustedIncomeTaxLoss": 100,
       |        "income": {
       |            "rent": 100.49,
       |            "premiumsOfLeaseGrant": 100.49,
       |            "otherPropertyIncome": 100.49
       |        },
       |        "expenses": {
       |            "premisesRunningCosts": 100.49,
       |            "repairsAndMaintenance": 100.49,
       |            "financialCosts": 100.49,
       |            "professionalFees": 100.49,
       |            "travelCosts": 100.49,
       |            "costOfServices": 100.49,
       |            "residentialFinancialCost": 100.49,
       |            "broughtFwdResidentialFinancialCost": 100.49,
       |            "other": 100.49
       |        },
       |        "countryLevelDetail": [
       |        {
       |          "countryCode": "FRA",
       |          "total": {
       |            "totalIncome": 100.49,
       |            "totalExpenses": 100.49,
       |            "totalAdditions": 100.49,
       |            "totalDeductions": 100.49
       |          },
       |          "income": {
       |            "rent": 100.49,
       |            "premiumsOfLeaseGrant": 100.49,
       |            "otherPropertyIncome": 100.49
       |          },
       |          "expenses": {
       |            "premisesRunningCosts": 100.49,
       |            "repairsAndMaintenance": 100.49,
       |            "financialCosts": 100.49,
       |            "professionalFees": 100.49,
       |            "travelCosts": 100.49,
       |            "costOfServices": 100.49,
       |            "residentialFinancialCost": 100.49,
       |            "broughtFwdResidentialFinancialCost": 100.49,
       |            "other": 100.49
       |          }
       |        }
       |      ]
       |     }
       |}""".stripMargin
  )

  val desRetrieveBsasResponseWithInvalidTypeOfBusiness: JsValue = Json.parse(
    s"""{
       |    "inputs": {
       |        "incomeSourceType": "01",
       |        "incomeSourceId": "XAIS12345678910",
       |        "accountingPeriodStartDate": "2020-04-06",
       |        "accountingPeriodEndDate": "2020-05-05"
       |    },
       |    "metadata": {
       |        "taxYear": 2021,
       |        "requestedDateTime": "2020-10-14T11:33:27Z",
       |        "calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
       |        "status": "valid",
       |        "adjusted": "True"
       |    },
       |    "adjustable": {
       |        "totalIncome": 100.49,
       |        "totalExpenses": 100.49,
       |        "totalAdditions": 100.49,
       |        "totalDeductions": 100.49,
       |        "accountingAdjustments": 100.49,
       |        "netProfit": 100.49,
       |        "taxableProfit": 100,
       |        "netLoss": 100.49,
       |        "adjustedIncomeTaxLoss": 100,
       |        "income": {
       |            "rentIncome": 100.49,
       |            "premiumsOfLeaseGrant": 100.49,
       |            "otherPropertyIncome": 100.49
       |        },
       |        "expenses": {
       |            "premisesRunningCosts": 100.49,
       |            "repairsAndMaintenance": 100.49,
       |            "financialCosts": 100.49,
       |            "professionalFees": 100.49,
       |            "travelCosts": 100.49,
       |            "costOfServices": 100.49,
       |            "residentialFinancialCost": 100.49,
       |            "broughtFwdResidentialFinancialCost": 100.49,
       |            "other": 100.49
       |        }
       |    },
       |    "adjustedSummaryCalculation": {
       |        "totalIncome": 100.49,
       |        "totalExpenses": 100.49,
       |        "totalAdditions": 100.49,
       |        "totalDeductions": 100.49,
       |        "accountingAdjustments": 100.49,
       |        "netProfit": 100.49,
       |        "taxableProfit": 100,
       |        "netLoss": 100.49,
       |        "adjustedIncomeTaxLoss": 100,
       |        "income": {
       |            "rentIncome": 100.49,
       |            "premiumsOfLeaseGrant": 100.49,
       |            "otherPropertyIncome": 100.49
       |        },
       |        "expenses": {
       |            "premisesRunningCosts": 100.49,
       |            "repairsAndMaintenance": 100.49,
       |            "financialCosts": 100.49,
       |            "professionalFees": 100.49,
       |            "travelCosts": 100.49,
       |            "costOfServices": 100.49,
       |            "residentialFinancialCost": 100.49,
       |            "broughtFwdResidentialFinancialCost": 100.49,
       |            "other": 100.49
       |        },
       |                "countryLevelDetail": [
       |        {
       |          "countryCode": "FRA",
       |          "total": {
       |            "totalIncome": 100.49,
       |            "totalExpenses": 100.49,
       |            "totalAdditions": 100.49,
       |            "totalDeductions": 100.49
       |          },
       |          "income": {
       |            "rent": 100.49,
       |            "premiumsOfLeaseGrant": 100.49,
       |            "otherPropertyIncome": 100.49
       |          },
       |          "expenses": {
       |            "premisesRunningCosts": 100.49,
       |            "repairsAndMaintenance": 100.49,
       |            "financialCosts": 100.49,
       |            "professionalFees": 100.49,
       |            "travelCosts": 100.49,
       |            "costOfServices": 100.49,
       |            "residentialFinancialCost": 100.49,
       |            "broughtFwdResidentialFinancialCost": 100.49,
       |            "other": 100.49
       |          }
       |        }
       |      ]
       |    }
       |}""".stripMargin
  )
}
