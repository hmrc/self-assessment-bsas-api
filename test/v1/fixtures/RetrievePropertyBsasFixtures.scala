/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.fixtures

import java.time.LocalDate

import play.api.libs.json.Json
import v1.models.domain.TypeOfBusiness
import v1.models.response.retrieveBsas._

object RetrievePropertyBsasFixtures {

  val totalBsasModel = TotalBsas(100.49,Some(100.49),Some(100.49),Some(100.49))
  val profitModel = Profit(Some(100.49),Some(100.49))
  val lossModel = Loss(Some(100.49),Some(100.49))
  val incomeBreakdownModel = IncomeBreakdown(Some(100.49),Some(100.49),Some(100.49),Some(100.49), Some(100.49))
  val expensesBreakdownModel = ExpensesBreakdown(Some(100.49),Some(100.49),Some(100.49), Some(100.49),
    Some(100.49),Some(100.49),Some(100.49), Some(100.49), Some(100.49),None)
  val accountingPeriodModel = AccountingPeriod(LocalDate.parse("2019-04-06"), LocalDate.parse("2020-04-05"))

  val bsasDetailModel = BsasDetail(total = totalBsasModel,
    profit = Some(profitModel),
    loss = Some(lossModel),
    incomeBreakdown = incomeBreakdownModel,
    expensesBreakdown = Some(expensesBreakdownModel))

  val metadataModel = Metadata(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`,
    accountingPeriod = accountingPeriodModel,
    taxYear = "2019-20",
    requestedDateTime = "2020-10-14T11:33:27.111Z",
    bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
    summaryStatus = "valid",
    adjustedSummary = true
  )

  val metadataModelWithAdjustableSummary = Metadata(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`,
    accountingPeriod = accountingPeriodModel,
    taxYear = "2019-20",
    requestedDateTime = "2020-10-14T11:33:27.111Z",
    bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
    summaryStatus = "valid",
    adjustedSummary = false
  )

  val retrieveUkPropertyBsasResponseModel: RetrieveUkPropertyBsasResponse = RetrieveUkPropertyBsasResponse(metadataModel, Some(bsasDetailModel))

  val mtdResponse = Json.parse(
    """{
      |   "metadata": {
      |      "typeOfBusiness": "uk-property-fhl",
      |      "accountingPeriod": {
      |         "startDate": "2019-04-06",
      |         "endDate": "2020-04-05"
      |      },
      |      "taxYear": "2019-20",
      |      "requestedDateTime": "2020-10-14T11:33:27.111Z",
      |      "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
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
      |         "taxable": 100.49
      |      },
      |      "loss": {
      |         "net": 100.49,
      |         "adjustedIncomeTax": 100.49
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

  val mtdBsasDetailJson = Json.parse(
    """{
      |      "total": {
      |         "income": 100.49,
      |         "expenses": 100.49,
      |         "additions": 100.49,
      |         "deductions": 100.49
      |      },
      |      "profit": {
      |         "net": 100.49,
      |         "taxable": 100.49
      |      },
      |      "loss": {
      |         "net": 100.49,
      |         "adjustedIncomeTax": 100.49
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

  val mtdMetadataJson = Json.parse(
    """{
      |"typeOfBusiness": "uk-property-fhl",
      |    "accountingPeriod": {
      |    "startDate": "2019-04-06",
      |    "endDate": "2020-04-05"
      |  },
      |    "taxYear": "2019-20",
      |    "requestedDateTime": "2020-10-14T11:33:27.111Z",
      |    "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |    "summaryStatus": "valid",
      |    "adjustedSummary": true
      |}""".stripMargin)

  val desRetrieveBsasResponse = Json.parse(
    """{
      | "metadata": {
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2020-10-14T11:33:27.111Z",
      |  "taxableEntityId": "0",
      |  "taxYear": "2020",
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
      |  "taxableProfit": 100.49,
      |  "adjustedIncomeTaxLoss": 100.49
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
      |  "taxableProfit": 100.49,
      |  "adjustedIncomeTaxLoss": 100.49
      | }
      |}""".stripMargin)

  val desRetrieveBsasResponseWithAdjustableSummary = Json.parse(
    """{
      | "metadata": {
      |  "calculationId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
      |  "requestedDateTime": "2020-10-14T11:33:27.111Z",
      |  "taxableEntityId": "0",
      |  "taxYear": "2020",
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
      |  "taxableProfit": 100.49,
      |  "adjustedIncomeTaxLoss": 100.49
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
}
