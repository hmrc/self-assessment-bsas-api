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

package v2.fixtures.ukProperty

import java.time.LocalDate
import play.api.libs.json.{JsValue, Json}
import v2.models.domain.TypeOfBusiness
import v2.models.response.retrieveBsas.ukProperty._
import v2.models.response.retrieveBsas.{AccountingPeriod, Loss, Profit, TotalBsas}

object RetrieveUkPropertyBsasFixtures {

  val totalBsasModel: TotalBsas = TotalBsas(Some(100.49),Some(100.49),Some(100.49),Some(100.49))
  val profitModel: Profit = Profit(Some(100.49),Some(100))
  val lossModel: Loss = Loss(Some(100.49),Some(100))
  val incomeBreakdownModel: IncomeBreakdown = IncomeBreakdown(Some(100.49),Some(100.49),Some(100.49),Some(100.49), Some(100.49))
  val expensesBreakdownModel: ExpensesBreakdown = ExpensesBreakdown(Some(100.49),Some(100.49),Some(100.49), Some(100.49),
    Some(100.49),Some(100.49),Some(100.49), Some(100.49), Some(100.49),None)
  val accountingPeriodModel: AccountingPeriod = AccountingPeriod(LocalDate.parse("2019-04-06"), LocalDate.parse("2020-04-05"))

  val bsasDetailModel: BsasDetail = BsasDetail(total = totalBsasModel,
    profit = Some(profitModel),
    loss = Some(lossModel),
    incomeBreakdown = Some(incomeBreakdownModel),
    expensesBreakdown = Some(expensesBreakdownModel))

  val metadataModel: Metadata = Metadata(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`,
    businessId = Some("111111111111111"),
    accountingPeriod = accountingPeriodModel,
    taxYear = "2019-20",
    requestedDateTime = "2020-10-14T11:33:27.111Z",
    bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
    summaryStatus = "valid",
    adjustedSummary = true
  )

  val metadataModelWithAdjustableSummary: Metadata = Metadata(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`,
    businessId = Some("111111111111111"),
    accountingPeriod = accountingPeriodModel,
    taxYear = "2019-20",
    requestedDateTime = "2020-10-14T11:33:27.111Z",
    bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
    summaryStatus = "valid",
    adjustedSummary = false
  )

  val retrieveUkPropertyBsasResponseModel: RetrieveUkPropertyBsasResponse = RetrieveUkPropertyBsasResponse(metadataModel, Some(bsasDetailModel))

  val mtdResponse: JsValue = Json.parse(
    """{
      |   "metadata": {
      |      "typeOfBusiness": "uk-property-fhl",
      |      "businessId": "111111111111111",
      |      "accountingPeriod": {
      |         "startDate": "2019-04-06",
      |         "endDate": "2020-04-05"
      |      },
      |      "taxYear": "2019-20",
      |      "requestedDateTime": "2020-10-14T11:33:27.111Z",
      |      "bsasId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |      "summaryStatus": "valid",
      |      "adjustedSummary": true
      |   },
      |   "bsas": {
      |      "total": {
      |         "income": 100.49,
      |         "expenses": 100.49,
      |         "additions": 100.49,
      |         "deductions": 100.49
      |      },
      |      "profit": {
      |         "net": 100.49,
      |         "taxable": 100
      |      },
      |      "loss": {
      |         "net": 100.49,
      |         "adjustedIncomeTax": 100
      |      },
      |      "incomeBreakdown": {
      |         "rentIncome": 100.49,
      |         "premiumsOfLeaseGrant": 100.49,
      |         "reversePremiums": 100.49,
      |         "otherPropertyIncome":100.49,
      |         "rarRentReceived":100.49
      |      },
      |      "expensesBreakdown": {
      |         "premisesRunningCosts": 100.49,
      |         "repairsAndMaintenance": 100.49,
      |         "financialCosts": 100.49,
      |         "professionalFees": 100.49,
      |         "travelCosts": 100.49,
      |         "costOfServices": 100.49,
      |         "residentialFinancialCost": 100.49,
      |         "broughtFwdResidentialFinancialCost": 100.49,
      |         "other": 100.49
      |      }
      |   }
      |}""".stripMargin)

  val mtdBsasDetailJson: JsValue = Json.parse(
    """{
      |      "total": {
      |         "income": 100.49,
      |         "expenses": 100.49,
      |         "additions": 100.49,
      |         "deductions": 100.49
      |      },
      |      "profit": {
      |         "net": 100.49,
      |         "taxable": 100
      |      },
      |      "loss": {
      |         "net": 100.49,
      |         "adjustedIncomeTax": 100
      |      },
      |      "incomeBreakdown": {
      |         "rentIncome": 100.49,
      |         "premiumsOfLeaseGrant": 100.49,
      |         "reversePremiums": 100.49,
      |         "otherPropertyIncome":100.49,
      |         "rarRentReceived":100.49
      |      },
      |      "expensesBreakdown": {
      |         "premisesRunningCosts": 100.49,
      |         "repairsAndMaintenance": 100.49,
      |         "financialCosts": 100.49,
      |         "professionalFees": 100.49,
      |         "travelCosts": 100.49,
      |         "costOfServices": 100.49,
      |         "residentialFinancialCost": 100.49,
      |         "broughtFwdResidentialFinancialCost": 100.49,
      |         "other": 100.49
      |      }
      |}""".stripMargin)

  val mtdMetadataJson: JsValue = Json.parse(
    """{
      |"typeOfBusiness": "uk-property-fhl",
      |"businessId": "111111111111111",
      |"accountingPeriod": {
      |    "startDate": "2019-04-06",
      |    "endDate": "2020-04-05"
      |  },
      |    "taxYear": "2019-20",
      |    "requestedDateTime": "2020-10-14T11:33:27.111Z",
      |    "bsasId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |    "summaryStatus": "valid",
      |    "adjustedSummary": true
      |}""".stripMargin)

  val downstreamRetrieveBsasResponse: JsValue = Json.parse(
    """{
      | "metadata": {
      |  "calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |  "requestedDateTime": "2020-10-14T11:33:27.111Z",
      |  "taxableEntityId": "0",
      |  "taxYear": 2020,
      |  "status": "valid"
      | },
      | "inputs": {
      |  "incomeSourceId": "111111111111111",
      |  "incomeSourceType": "04",
      |  "accountingPeriodStartDate": "2019-04-06",
      |  "accountingPeriodEndDate": "2020-04-05",
      |  "source": "MTD-SA",
      |  "submissionPeriods": [
      |   {
      |    "periodId": "2222222222222222",
      |    "startDate": "2019-04-06",
      |    "endDate": "2020-04-05",
      |    "receivedDateTime": "a"
      |   }
      |  ]
      | },
      | "adjustableSummaryCalculation": {
      |  "totalIncome": 100.49,
      |  "income": {
      |   "rentReceived": 100.49,
      |   "premiumsOfLeaseGrant": 100.49,
      |   "reversePremiums": 100.49,
      |   "otherPropertyIncome": 100.49,
      |   "rarRentReceived": 100.49
      |  },
      |  "totalExpenses": 100.49,
      |  "expenses": {
      |   "premisesRunningCosts": 100.49,
      |   "repairsAndMaintenance": 100.49,
      |   "financialCosts": 100.49,
      |   "professionalFees": 100.49,
      |   "travelCosts": 100.49,
      |   "costOfServices": 100.49,
      |   "residentialFinancialCost": 100.49,
      |   "broughtFwdResidentialFinancialCost": 100.49,
      |   "other": 100.49
      |  },
      |  "netProfit": 100.49,
      |  "netLoss": 100.49,
      |  "totalAdditions": 100.49,
      |  "totalDeductions": 100.49,
      |  "accountingAdjustments": 100.49,
      |  "taxableProfit": 100,
      |  "adjustedIncomeTaxLoss": 100
      | },
      | "adjustedSummaryCalculation": {
      |  "totalIncome": 100.49,
      |  "income": {
      |   "rentReceived": 100.49,
      |   "premiumsOfLeaseGrant": 100.49,
      |   "reversePremiums": 100.49,
      |   "otherPropertyIncome": 100.49,
      |   "rarRentReceived": 100.49
      |  },
      |  "totalExpenses": 100.49,
      |  "expenses": {
      |   "premisesRunningCosts": 100.49,
      |   "repairsAndMaintenance": 100.49,
      |   "financialCosts": 100.49,
      |   "professionalFees": 100.49,
      |   "travelCosts": 100.49,
      |   "costOfServices": 100.49,
      |   "residentialFinancialCost": 100.49,
      |   "broughtFwdResidentialFinancialCost": 100.49,
      |   "other": 100.49
      |  },
      |  "netProfit": 100.49,
      |  "netLoss": 100.49,
      |  "totalAdditions": 100.49,
      |  "totalDeductions": 100.49,
      |  "accountingAdjustments": 100.49,
      |  "taxableProfit": 100,
      |  "adjustedIncomeTaxLoss": 100
      | }
      |}""".stripMargin)

  val downstreamRetrieveBsasResponseWithAdjustableSummary: JsValue = Json.parse(
    """{
      | "metadata": {
      |  "calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |  "requestedDateTime": "2020-10-14T11:33:27.111Z",
      |  "taxableEntityId": "0",
      |  "taxYear": 2020,
      |  "status": "valid"
      | },
      | "inputs": {
      |  "incomeSourceId": "111111111111111",
      |  "incomeSourceType": "04",
      |  "accountingPeriodStartDate": "2019-04-06",
      |  "accountingPeriodEndDate": "2020-04-05",
      |  "source": "MTD-SA",
      |  "submissionPeriods": [
      |   {
      |    "periodId": "2222222222222222",
      |    "startDate": "2019-04-06",
      |    "endDate": "2020-04-05",
      |    "receivedDateTime": "a"
      |   }
      |  ]
      | },
      | "adjustableSummaryCalculation": {
      |  "totalIncome": 100.49,
      |  "income": {
      |   "rentReceived": 100.49,
      |   "premiumsOfLeaseGrant": 100.49,
      |   "reversePremiums": 100.49,
      |   "otherPropertyIncome": 100.49,
      |   "rarRentReceived": 100.49
      |  },
      |  "totalExpenses": 100.49,
      |  "expenses": {
      |   "premisesRunningCosts": 100.49,
      |   "repairsAndMaintenance": 100.49,
      |   "financialCosts": 100.49,
      |   "professionalFees": 100.49,
      |   "travelCosts": 100.49,
      |   "costOfServices": 100.49,
      |   "residentialFinancialCost": 100.49,
      |   "broughtFwdResidentialFinancialCost": 100.49,
      |   "other": 100.49
      |  },
      |  "netProfit": 100.49,
      |  "netLoss": 100.49,
      |  "totalAdditions": 100.49,
      |  "totalDeductions": 100.49,
      |  "accountingAdjustments": 100.49,
      |  "taxableProfit": 100,
      |  "adjustedIncomeTaxLoss": 100
      | },
      | "adjustments": {
      |  "income": {
      |   "turnover": "100.49",
      |   "other": "100.49"
      |  },
      |  "expenses": {
      |   "premisesRunningCosts": 100.49,
      |   "repairsAndMaintenance": 100.49,
      |   "financialCosts": 100.49,
      |   "professionalFees": 100.49,
      |   "travelCosts": 100.49,
      |   "costOfServices": 100.49,
      |   "residentialFinancialCost": 100.49,
      |   "broughtFwdResidentialFinancialCost": 100.49,
      |   "other": 100.49
      |  }
      | }
      |}""".stripMargin)

  val downstreamRetrieveBsasResponseWithInvalidTypeOfBusiness: JsValue = Json.parse(
    """{
      | "metadata": {
      |  "calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |  "requestedDateTime": "2020-10-14T11:33:27.111Z",
      |  "taxableEntityId": "0",
      |  "taxYear": 2020,
      |  "status": "valid"
      | },
      | "inputs": {
      |  "incomeSourceId": "111111111111111",
      |  "incomeSourceType": "01",
      |  "accountingPeriodStartDate": "2019-04-06",
      |  "accountingPeriodEndDate": "2020-04-05",
      |  "source": "MTD-SA",
      |  "submissionPeriods": [
      |   {
      |    "periodId": "2222222222222222",
      |    "startDate": "2019-04-06",
      |    "endDate": "2020-04-05",
      |    "receivedDateTime": "a"
      |   }
      |  ]
      | },
      | "adjustableSummaryCalculation": {
      |  "totalIncome": 100.49,
      |  "income": {
      |   "totalRentsReceived": 100.49,
      |   "premiumsOfLeaseGrant": 100.49,
      |   "reversePremiums": 100.49,
      |   "otherPropertyIncome": 100.49,
      |   "rarRentReceived": 100.49
      |  },
      |  "totalExpenses": 100.49,
      |  "expenses": {
      |   "premisesRunningCosts": 100.49,
      |   "repairsAndMaintenance": 100.49,
      |   "financialCosts": 100.49,
      |   "professionalFees": 100.49,
      |   "travelCosts": 100.49,
      |   "costOfServices": 100.49,
      |   "residentialFinancialCost": 100.49,
      |   "broughtFwdResidentialFinancialCost": 100.49,
      |   "other": 100.49
      |  },
      |  "netProfit": 100.49,
      |  "netLoss": 100.49,
      |  "totalAdditions": 100.49,
      |  "totalDeductions": 100.49,
      |  "accountingAdjustments": 100.49,
      |  "taxableProfit": 100,
      |  "adjustedIncomeTaxLoss": 100
      | },
      | "adjustedSummaryCalculation": {
      |  "totalIncome": 100.49,
      |  "income": {
      |   "totalRentsReceived": 100.49,
      |   "premiumsOfLeaseGrant": 100.49,
      |   "reversePremiums": 100.49,
      |   "otherPropertyIncome": 100.49,
      |   "rarRentReceived": 100.49
      |  },
      |  "totalExpenses": 100.49,
      |  "expenses": {
      |   "premisesRunningCosts": 100.49,
      |   "repairsAndMaintenance": 100.49,
      |   "financialCosts": 100.49,
      |   "professionalFees": 100.49,
      |   "travelCosts": 100.49,
      |   "costOfServices": 100.49,
      |   "residentialFinancialCost": 100.49,
      |   "broughtFwdResidentialFinancialCost": 100.49,
      |   "other": 100.49
      |  },
      |  "netProfit": 100.49,
      |  "netLoss": 100.49,
      |  "totalAdditions": 100.49,
      |  "totalDeductions": 100.49,
      |  "accountingAdjustments": 100.49,
      |  "taxableProfit": 100,
      |  "adjustedIncomeTaxLoss": 100
      | }
      |}""".stripMargin)

  val hateoasResponseForProperty: (String, String) => String = (nino: String, bsasId: String) => s"""
                                                        |{
                                                        |   "metadata": {
                                                        |      "typeOfBusiness": "uk-property-fhl",
                                                        |      "businessId": "111111111111111",
                                                        |      "accountingPeriod": {
                                                        |         "startDate": "2019-04-06",
                                                        |         "endDate": "2020-04-05"
                                                        |      },
                                                        |      "taxYear": "2019-20",
                                                        |      "requestedDateTime": "2020-10-14T11:33:27.111Z",
                                                        |      "bsasId": "$bsasId",
                                                        |      "summaryStatus": "valid",
                                                        |      "adjustedSummary": true
                                                        |   },
                                                        |   "bsas": {
                                                        |      "total": {
                                                        |         "income": 100.49,
                                                        |         "expenses": 100.49,
                                                        |         "additions": 100.49,
                                                        |         "deductions": 100.49
                                                        |      },
                                                        |      "profit": {
                                                        |         "net": 100.49,
                                                        |         "taxable": 100
                                                        |      },
                                                        |      "loss": {
                                                        |         "net": 100.49,
                                                        |         "adjustedIncomeTax": 100
                                                        |      },
                                                        |      "incomeBreakdown": {
                                                        |         "rentIncome": 100.49,
                                                        |         "premiumsOfLeaseGrant": 100.49,
                                                        |         "reversePremiums": 100.49,
                                                        |         "otherPropertyIncome":100.49,
                                                        |         "rarRentReceived":100.49
                                                        |      },
                                                        |      "expensesBreakdown": {
                                                        |         "premisesRunningCosts": 100.49,
                                                        |         "repairsAndMaintenance": 100.49,
                                                        |         "financialCosts": 100.49,
                                                        |         "professionalFees": 100.49,
                                                        |         "travelCosts": 100.49,
                                                        |         "costOfServices": 100.49,
                                                        |         "residentialFinancialCost": 100.49,
                                                        |         "broughtFwdResidentialFinancialCost": 100.49,
                                                        |         "other": 100.49
                                                        |      }
                                                        |   },
                                                        |   "links":[
                                                        |    {
                                                        |      "href":"/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId",
                                                        |      "rel":"self",
                                                        |      "method":"GET"
                                                        |    },
                                                        |    {
                                                        |      "href":"/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId/adjust",
                                                        |      "rel":"submit-summary-adjustments",
                                                        |      "method":"POST"
                                                        |    }
                                                        |  ]
                                                        |}
    """.stripMargin
}
