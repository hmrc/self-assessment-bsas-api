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

package v1.fixtures.selfEmployment

import java.time.LocalDate

import play.api.libs.json.{JsValue, Json}
import v1.models.domain.TypeOfBusiness
import v1.models.response.retrieveBsas.selfEmployment._
import v1.models.response.retrieveBsas.{AccountingPeriod, Loss, Profit, TotalBsas}

object RetrieveSelfEmploymentBsasFixtures {

  private val now = "2019-04-06"
  private val aYearFromNow = "2020-04-05"

  val additionsBreakdownModel: AdditionsBreakdown = AdditionsBreakdown(
    costOfGoodsBoughtDisallowable = Some(100.49),
    cisPaymentsToSubcontractorsDisallowable = Some(100.49),
    staffCostsDisallowable = Some(100.49),
    travelCostsDisallowable = Some(100.49),
    premisesRunningCostsDisallowable = Some(100.49),
    maintenanceCostsDisallowable = Some(100.49),
    adminCostsDisallowable = Some(100.49),
    advertisingCostsDisallowable = Some(100.49),
    businessEntertainmentCostsDisallowable = Some(100.49),
    interestDisallowable = Some(100.49),
    financialChargesDisallowable = Some(100.49),
    badDebtDisallowable = Some(100.49),
    professionalFeesDisallowable = Some(100.49),
    depreciationDisallowable = Some(100.49),
    otherDisallowable = Some(100.49)
  )

  val mtdAdditionsBreakdownJson: JsValue = Json.parse(
    """{
      |  "costOfGoodsBoughtDisallowable": 100.49,
      |  "cisPaymentsToSubcontractorsDisallowable": 100.49,
      |  "staffCostsDisallowable": 100.49,
      |  "travelCostsDisallowable": 100.49,
      |  "premisesRunningCostsDisallowable": 100.49,
      |  "maintenanceCostsDisallowable": 100.49,
      |  "adminCostsDisallowable": 100.49,
      |  "advertisingCostsDisallowable": 100.49,
      |  "businessEntertainmentCostsDisallowable": 100.49,
      |  "interestDisallowable": 100.49,
      |  "financialChargesDisallowable": 100.49,
      |  "badDebtDisallowable": 100.49,
      |  "professionalFeesDisallowable": 100.49,
      |  "depreciationDisallowable": 100.49,
      |  "otherDisallowable": 100.49
      |}""".stripMargin
  )

  val desAdditionsBreakdownJson: JsValue = Json.parse(
    """{
      |  "costOfGoodsDisallowable": 100.49,
      |  "paymentsToSubContractorsDisallowable": 100.49,
      |  "wagesAndStaffCostsDisallowable": 100.49,
      |  "carVanTravelExpensesDisallowable": 100.49,
      |  "premisesRunningCostsDisallowable": 100.49,
      |  "maintenanceCostsDisallowable": 100.49,
      |  "adminCostsDisallowable": 100.49,
      |  "advertisingCostsDisallowable": 100.49,
      |  "businessEntertainmentCostsDisallowable": 100.49,
      |  "interestOnBankOtherLoansDisallowable": 100.49,
      |  "financeChargesDisallowable": 100.49,
      |  "irrecoverableDebtsDisallowable": 100.49,
      |  "professionalFeesDisallowable": 100.49,
      |  "depreciationDisallowable": 100.49,
      |  "otherExpensesDisallowable": 100.49
      |}""".stripMargin
  )

  val incomeBreakdownModel: IncomeBreakdown = IncomeBreakdown(
    turnover = 100.49,
    other = Some(100.49)
  )

  val mtdIncomeBreakdownJson: JsValue = Json.parse(
    """{
      |  "turnover": 100.49,
      |  "other": 100.49
      |}""".stripMargin
  )

  val desIncomeBreakdownJson: JsValue = Json.parse(
    """{
      |  "turnover": 100.49,
      |  "other": 100.49
      |}""".stripMargin
  )

  val expensesBreakdownModel: ExpensesBreakdown = ExpensesBreakdown(
    costOfGoodsBought = Some(100.49),
    cisPaymentsToSubcontractors = Some(100.49),
    staffCosts = Some(100.49),
    travelCosts = Some(100.49),
    premisesRunningCosts = Some(100.49),
    maintenanceCosts = Some(100.49),
    adminCosts = Some(100.49),
    advertisingCosts = Some(100.49),
    businessEntertainmentCosts = Some(100.49),
    interest = Some(100.49),
    financialCharges = Some(100.49),
    badDebt = Some(100.49),
    professionalFees = Some(100.49),
    depreciation = Some(100.49),
    other = Some(100.49),
    consolidatedExpenses = Some(100.49)
  )

  val mtdExpensesBreakdownJson: JsValue = Json.parse(
    """{
      |  "costOfGoodsBought": 100.49,
      |  "cisPaymentsToSubcontractors": 100.49,
      |  "staffCosts": 100.49,
      |  "travelCosts": 100.49,
      |  "premisesRunningCosts": 100.49,
      |  "maintenanceCosts": 100.49,
      |  "adminCosts": 100.49,
      |  "advertisingCosts": 100.49,
      |  "businessEntertainmentCosts": 100.49,
      |  "interest": 100.49,
      |  "financialCharges": 100.49,
      |  "badDebt": 100.49,
      |  "professionalFees": 100.49,
      |  "depreciation": 100.49,
      |  "other": 100.49,
      |  "consolidatedExpenses": 100.49
      |}""".stripMargin
  )

  val desExpensesBreakdownJson: JsValue = Json.parse(
    """{
      |  "costOfGoodsAllowable": 100.49,
      |  "paymentsToSubContractorsAllowable": 100.49,
      |  "wagesAndStaffCostsAllowable": 100.49,
      |  "carVanTravelExpensesAllowable": 100.49,
      |  "premisesRunningCostsAllowable": 100.49,
      |  "maintenanceCostsAllowable": 100.49,
      |  "adminCostsAllowable": 100.49,
      |  "advertisingCostsAllowable": 100.49,
      |  "businessEntertainmentCostsAllowable": 100.49,
      |  "interestOnBankOtherLoansAllowable": 100.49,
      |  "financeChargesAllowable": 100.49,
      |  "irrecoverableDebtsAllowable": 100.49,
      |  "professionalFeesAllowable": 100.49,
      |  "depreciationAllowable": 100.49,
      |  "otherExpensesAllowable": 100.49,
      |  "consolidatedExpenses": 100.49
      |}""".stripMargin
  )

  val bsasDetailModel: BsasDetail = BsasDetail(
    total = TotalBsas(100.49, Some(100.49), Some(100.49), Some(100.49)),
    accountingAdjustments = Some(100.49),
    profit = Some(Profit(Some(100.49), Some(100.49))),
    loss = Some(Loss(Some(100.49), Some(100.49))),
    incomeBreakdown = incomeBreakdownModel,
    expensesBreakdown = Some(expensesBreakdownModel),
    additionsBreakdown = Some(additionsBreakdownModel)
  )

  val mtdBsasDetailJson: JsValue = Json.parse(
    s"""{
       |  "total": {
       |    "income": 100.49,
       |    "expenses": 100.49,
       |    "additions": 100.49,
       |    "deductions": 100.49
       |  },
       |  "accountingAdjustments": 100.49,
       |  "profit": {
       |    "net": 100.49,
       |    "taxable": 100.49
       |  },
       |  "loss": {
       |    "net": 100.49,
       |    "adjustedIncomeTax": 100.49
       |  },
       |  "incomeBreakdown": $mtdIncomeBreakdownJson,
       |  "expensesBreakdown": $mtdExpensesBreakdownJson,
       |  "additionsBreakdown": $mtdAdditionsBreakdownJson
       |}""".stripMargin
  )

  val desBsasDetailJson: JsValue = Json.parse(
    s"""{
       |  "totalIncome": 100.49,
       |  "totalExpenses": 100.49,
       |  "totalAdditions": 100.49,
       |  "totalDeductions": 100.49,
       |  "accountingAdjustments": 100.49,
       |  "netProfit": 100.49,
       |  "taxableProfit": 100.49,
       |  "netLoss": 100.49,
       |  "adjustedIncomeTaxLoss": 100.49,
       |  "income": $desIncomeBreakdownJson,
       |  "expenses": $desExpensesBreakdownJson,
       |  "additions": $desAdditionsBreakdownJson
       |}""".stripMargin
  )

  val metadataModel: Boolean => Metadata = adjustedSummary =>
    Metadata(
      typeOfBusiness = TypeOfBusiness.`self-employment`,
      selfEmploymentId = Some("X0IS12345678901"),
      accountingPeriod = AccountingPeriod(LocalDate.parse(now), LocalDate.parse(aYearFromNow)),
      taxYear = "2019-20",
      bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      requestedDateTime = "2020-04-07T23:59:59.000Z",
      summaryStatus = "valid",
      adjustedSummary = adjustedSummary
    )

  val mtdMetadataJson: Boolean => JsValue = adjustedSummary =>
    Json.parse(
      s"""{
         |  "typeOfBusiness": "self-employment",
         |  "selfEmploymentId": "X0IS12345678901",
         |  "accountingPeriod": {
         |    "startDate": "$now",
         |    "endDate": "$aYearFromNow"
         |  },
         |  "taxYear": "2019-20",
         |  "bsasId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
         |  "requestedDateTime": "2020-04-07T23:59:59.000Z",
         |  "summaryStatus": "valid",
         |  "adjustedSummary": $adjustedSummary
         |}""".stripMargin
    )

  val retrieveBsasResponseModelAdjusted: RetrieveSelfEmploymentBsasResponse = RetrieveSelfEmploymentBsasResponse(
    metadata = metadataModel(true),
    bsas = Some(bsasDetailModel)
  )

  val retrieveBsasResponseModelAdjustable: RetrieveSelfEmploymentBsasResponse = RetrieveSelfEmploymentBsasResponse(
    metadata = metadataModel(false),
    bsas = Some(bsasDetailModel)
  )

  val mtdRetrieveBsasResponseJson: Boolean => JsValue = adjustedSummary =>
    Json.parse(
      s"""{
         |  "metadata": ${mtdMetadataJson(adjustedSummary)},
         |  "bsas": $mtdBsasDetailJson
         |}""".stripMargin
    )

  val desRetrieveBsasResponseJsonAdjusted: JsValue = Json.parse(
    s"""{
       |  "inputs": {
       |    "incomeSourceType": "01",
       |    "incomeSourceId": "X0IS12345678901",
       |    "accountingPeriodStartDate": "$now",
       |    "accountingPeriodEndDate": "$aYearFromNow"
       |  },
       |  "metadata": {
       |    "taxYear": "2020",
       |    "calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
       |    "requestedDateTime": "2020-04-07T23:59:59.000Z",
       |    "status": "valid"
       |  },
       |  "adjustedSummaryCalculation": $desBsasDetailJson
       |}""".stripMargin
  )

  val desRetrieveBsasResponseJsonAdjustable: JsValue = Json.parse(
    s"""{
       |  "inputs": {
       |    "incomeSourceType": "01",
       |    "incomeSourceId": "X0IS12345678901",
       |    "accountingPeriodStartDate": "$now",
       |    "accountingPeriodEndDate": "$aYearFromNow"
       |  },
       |  "metadata": {
       |    "taxYear": "2020",
       |    "calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
       |    "requestedDateTime": "2020-04-07T23:59:59.000Z",
       |    "status": "valid"
       |  },
       |  "adjustableSummaryCalculation": $desBsasDetailJson
       |}""".stripMargin
  )

  val hateoasResponseForSelfAssessment = (nino: String, bsasId: String) =>
    s"""
       |	{
       |		"metadata": {
       |			"typeOfBusiness": "self-employment",
       |			"selfEmploymentId": "X0IS12345678901",
       |			"accountingPeriod": {
       |				"startDate": "2019-04-06",
       |				"endDate": "2020-04-05"
       |			},
       |			"taxYear": "2019-20",
       |			"bsasId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
       |			"requestedDateTime": "2020-04-07T23:59:59.000Z",
       |			"summaryStatus": "valid",
       |			"adjustedSummary": true
       |		},
       |		"bsas": {
       |			"total": {
       |				"income": 100.49,
       |				"expenses": 100.49,
       |				"additions": 100.49,
       |				"deductions": 100.49
       |			},
       |			"accountingAdjustments": 100.49,
       |			"profit": {
       |				"net": 100.49,
       |				"taxable": 100.49
       |			},
       |			"loss": {
       |				"net": 100.49,
       |				"adjustedIncomeTax": 100.49
       |			},
       |			"incomeBreakdown": {
       |				"turnover": 100.49,
       |				"other": 100.49
       |			},
       |			"expensesBreakdown": {
       |				"consolidatedExpenses": 100.49
       |			},
       |			"additionsBreakdown": {
       |				"costOfGoodsBoughtDisallowable": 100.49,
       |				"cisPaymentsToSubcontractorsDisallowable": 100.49,
       |				"staffCostsDisallowable": 100.49,
       |				"travelCostsDisallowable": 100.49,
       |				"premisesRunningCostsDisallowable": 100.49,
       |				"maintenanceCostsDisallowable": 100.49,
       |				"adminCostsDisallowable": 100.49,
       |				"advertisingCostsDisallowable": 100.49,
       |				"businessEntertainmentCostsDisallowable": 100.49,
       |				"interestDisallowable": 100.49,
       |				"financialChargesDisallowable": 100.49,
       |				"badDebtDisallowable": 100.49,
       |				"professionalFeesDisallowable": 100.49,
       |				"depreciationDisallowable": 100.49,
       |				"otherDisallowable": 100.49
       |			}
       |		},
       |		"links": [{
       |			"href": "/individuals/self-assessment/adjustable-summary/AA123456A/self-employment/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
       |			"method": "GET",
       |			"rel": "self"
       |		}, {
       |			"href": "/individuals/self-assessment/adjustable-summary/AA123456A/self-employment/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c/adjust",
       |			"method": "POST",
       |			"rel": "submit-summary-adjustments"
       |		}]
       |	}
    """.stripMargin

  val hateoasResponseForAdjustedSelfAssessment = (nino: String, bsasId: String) =>
    s"""
       {
       |	"metadata": {
       |		"typeOfBusiness": "self-employment",
       |		"selfEmploymentId": "X0IS12345678901",
       |		"accountingPeriod": {
       |			"startDate": "2019-04-06",
       |			"endDate": "2020-04-05"
       |		},
       |		"taxYear": "2019-20",
       |		"bsasId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
       |		"requestedDateTime": "2020-04-07T23:59:59.000Z",
       |		"summaryStatus": "valid",
       |		"adjustedSummary": true
       |	},
       |	"bsas": {
       |		"total": {
       |			"income": 100.49,
       |			"expenses": 100.49,
       |			"additions": 100.49,
       |			"deductions": 100.49
       |		},
       |		"accountingAdjustments": 100.49,
       |		"profit": {
       |			"net": 100.49,
       |			"taxable": 100.49
       |		},
       |		"loss": {
       |			"net": 100.49,
       |			"adjustedIncomeTax": 100.49
       |		},
       |		"incomeBreakdown": {
       |			"turnover": 100.49,
       |			"other": 100.49
       |		},
       |		"expensesBreakdown": {
       |			"costOfGoodsBought": 100.49,
       |			"cisPaymentsToSubcontractors": 100.49,
       |			"staffCosts": 100.49,
       |			"travelCosts": 100.49,
       |			"premisesRunningCosts": 100.49,
       |			"maintenanceCosts": 100.49,
       |			"adminCosts": 100.49,
       |			"advertisingCosts": 100.49,
       |			"businessEntertainmentCosts": 100.49,
       |			"interest": 100.49,
       |			"financialCharges": 100.49,
       |			"badDebt": 100.49,
       |			"professionalFees": 100.49,
       |			"depreciation": 100.49,
       |			"other": 100.49,
       |			"consolidatedExpenses": 100.49
       |		},
       |		"additionsBreakdown": {
       |			"costOfGoodsBoughtDisallowable": 100.49,
       |			"cisPaymentsToSubcontractorsDisallowable": 100.49,
       |			"staffCostsDisallowable": 100.49,
       |			"travelCostsDisallowable": 100.49,
       |			"premisesRunningCostsDisallowable": 100.49,
       |			"maintenanceCostsDisallowable": 100.49,
       |			"adminCostsDisallowable": 100.49,
       |			"advertisingCostsDisallowable": 100.49,
       |			"businessEntertainmentCostsDisallowable": 100.49,
       |			"interestDisallowable": 100.49,
       |			"financialChargesDisallowable": 100.49,
       |			"badDebtDisallowable": 100.49,
       |			"professionalFeesDisallowable": 100.49,
       |			"depreciationDisallowable": 100.49,
       |			"otherDisallowable": 100.49
       |		}
       |	},
       |	"links": [{
       |		"href": "/individuals/self-assessment/adjustable-summary/AA123456A/self-employment/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
       |		"method": "GET",
       |		"rel": "self"
       |	}, {
       |		"href": "/individuals/self-assessment/adjustable-summary/AA123456A/self-employment/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c/adjust",
       |		"method": "POST",
       |		"rel": "submit-summary-adjustments"
       |	}]
       |}
    """.stripMargin

  val desRetrieveBsasResponse = Json.parse(
    """ {
      | 	"inputs": {
      | 		"incomeSourceType": "01",
      | 		"incomeSourceId": "X0IS12345678901",
      | 		"accountingPeriodStartDate": "2019-04-06",
      | 		"accountingPeriodEndDate": "2020-04-05"
      | 	},
      | 	"metadata": {
      | 		"taxYear": "2020",
      | 		"calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      | 		"requestedDateTime": "2020-04-07T23:59:59.000Z",
      | 		"status": "valid"
      | 	},
      | 	"adjustableSummaryCalculation": {
      | 		"totalIncome": 100.49,
      | 		"totalExpenses": 100.49,
      | 		"totalAdditions": 100.49,
      | 		"totalDeductions": 100.49,
      | 		"accountingAdjustments": 100.49,
      | 		"netProfit": 100.49,
      | 		"taxableProfit": 100.49,
      | 		"netLoss": 100.49,
      | 		"adjustedIncomeTaxLoss": 100.49,
      | 		"income": {
      | 			"turnover": 100.49,
      | 			"other": 100.49
      | 		},
      | 		"expenses": {
      | 			"consolidatedExpenses": 100.49
      | 		},
      | 		"additions": {
      | 			"costOfGoodsDisallowable": 100.49,
      | 			"paymentsToSubContractorsDisallowable": 100.49,
      | 			"wagesAndStaffCostsDisallowable": 100.49,
      | 			"carVanTravelExpensesDisallowable": 100.49,
      | 			"premisesRunningCostsDisallowable": 100.49,
      | 			"maintenanceCostsDisallowable": 100.49,
      | 			"adminCostsDisallowable": 100.49,
      | 			"advertisingCostsDisallowable": 100.49,
      | 			"businessEntertainmentCostsDisallowable": 100.49,
      | 			"interestOnBankOtherLoansDisallowable": 100.49,
      | 			"financeChargesDisallowable": 100.49,
      | 			"irrecoverableDebtsDisallowable": 100.49,
      | 			"professionalFeesDisallowable": 100.49,
      | 			"depreciationDisallowable": 100.49,
      | 			"otherExpensesDisallowable": 100.49
      | 		}
      | 	},
      | 	"adjustedSummaryCalculation": {
      | 		"totalIncome": 100.49,
      | 		"totalExpenses": 100.49,
      | 		"totalAdditions": 100.49,
      | 		"totalDeductions": 100.49,
      | 		"accountingAdjustments": 100.49,
      | 		"netProfit": 100.49,
      | 		"taxableProfit": 100.49,
      | 		"netLoss": 100.49,
      | 		"adjustedIncomeTaxLoss": 100.49,
      | 		"income": {
      | 			"turnover": 100.49,
      | 			"other": 100.49
      | 		},
      | 		"expenses": {
      | 			"consolidatedExpenses": 100.49
      | 		},
      | 		"additions": {
      | 			"costOfGoodsDisallowable": 100.49,
      | 			"paymentsToSubContractorsDisallowable": 100.49,
      | 			"wagesAndStaffCostsDisallowable": 100.49,
      | 			"carVanTravelExpensesDisallowable": 100.49,
      | 			"premisesRunningCostsDisallowable": 100.49,
      | 			"maintenanceCostsDisallowable": 100.49,
      | 			"adminCostsDisallowable": 100.49,
      | 			"advertisingCostsDisallowable": 100.49,
      | 			"businessEntertainmentCostsDisallowable": 100.49,
      | 			"interestOnBankOtherLoansDisallowable": 100.49,
      | 			"financeChargesDisallowable": 100.49,
      | 			"irrecoverableDebtsDisallowable": 100.49,
      | 			"professionalFeesDisallowable": 100.49,
      | 			"depreciationDisallowable": 100.49,
      | 			"otherExpensesDisallowable": 100.49
      | 		}
      | 	}
      | }""".stripMargin)

  val desRetrieveBsasResponseWithInvalidTypeOfBusiness = Json.parse(
    """ {
      | 	"inputs": {
      | 		"incomeSourceType": "04",
      | 		"incomeSourceId": "X0IS12345678901",
      | 		"accountingPeriodStartDate": "2019-04-06",
      | 		"accountingPeriodEndDate": "2020-04-05"
      | 	},
      | 	"metadata": {
      | 		"taxYear": "2020",
      | 		"calculationId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      | 		"requestedDateTime": "2020-04-07T23:59:59.000Z",
      | 		"status": "valid"
      | 	},
      | 	"adjustableSummaryCalculation": {
      | 		"totalIncome": 100.49,
      | 		"totalExpenses": 100.49,
      | 		"totalAdditions": 100.49,
      | 		"totalDeductions": 100.49,
      | 		"accountingAdjustments": 100.49,
      | 		"netProfit": 100.49,
      | 		"taxableProfit": 100.49,
      | 		"netLoss": 100.49,
      | 		"adjustedIncomeTaxLoss": 100.49,
      | 		"income": {
      | 			"turnover": 100.49,
      | 			"other": 100.49
      | 		},
      | 		"expenses": {
      | 			"consolidatedExpenses": 100.49
      | 		},
      | 		"additions": {
      | 			"costOfGoodsDisallowable": 100.49,
      | 			"paymentsToSubContractorsDisallowable": 100.49,
      | 			"wagesAndStaffCostsDisallowable": 100.49,
      | 			"carVanTravelExpensesDisallowable": 100.49,
      | 			"premisesRunningCostsDisallowable": 100.49,
      | 			"maintenanceCostsDisallowable": 100.49,
      | 			"adminCostsDisallowable": 100.49,
      | 			"advertisingCostsDisallowable": 100.49,
      | 			"businessEntertainmentCostsDisallowable": 100.49,
      | 			"interestOnBankOtherLoansDisallowable": 100.49,
      | 			"financeChargesDisallowable": 100.49,
      | 			"irrecoverableDebtsDisallowable": 100.49,
      | 			"professionalFeesDisallowable": 100.49,
      | 			"depreciationDisallowable": 100.49,
      | 			"otherExpensesDisallowable": 100.49
      | 		}
      | 	},
      | 	"adjustedSummaryCalculation": {
      | 		"totalIncome": 100.49,
      | 		"totalExpenses": 100.49,
      | 		"totalAdditions": 100.49,
      | 		"totalDeductions": 100.49,
      | 		"accountingAdjustments": 100.49,
      | 		"netProfit": 100.49,
      | 		"taxableProfit": 100.49,
      | 		"netLoss": 100.49,
      | 		"adjustedIncomeTaxLoss": 100.49,
      | 		"income": {
      | 			"turnover": 100.49,
      | 			"other": 100.49
      | 		},
      | 		"expenses": {
      | 			"consolidatedExpenses": 100.49
      | 		},
      | 		"additions": {
      | 			"costOfGoodsDisallowable": 100.49,
      | 			"paymentsToSubContractorsDisallowable": 100.49,
      | 			"wagesAndStaffCostsDisallowable": 100.49,
      | 			"carVanTravelExpensesDisallowable": 100.49,
      | 			"premisesRunningCostsDisallowable": 100.49,
      | 			"maintenanceCostsDisallowable": 100.49,
      | 			"adminCostsDisallowable": 100.49,
      | 			"advertisingCostsDisallowable": 100.49,
      | 			"businessEntertainmentCostsDisallowable": 100.49,
      | 			"interestOnBankOtherLoansDisallowable": 100.49,
      | 			"financeChargesDisallowable": 100.49,
      | 			"irrecoverableDebtsDisallowable": 100.49,
      | 			"professionalFeesDisallowable": 100.49,
      | 			"depreciationDisallowable": 100.49,
      | 			"otherExpensesDisallowable": 100.49
      | 		}
      | 	}
      | }""".stripMargin)
}
