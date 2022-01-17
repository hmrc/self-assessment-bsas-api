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

package v3.fixtures.selfEmployment

import java.time.LocalDate

import play.api.libs.json.{JsValue, Json}
import v2.models.domain.TypeOfBusiness
import v2.models.response.retrieveBsas.AccountingPeriod
import v2.models.response.retrieveBsasAdjustments.selfEmployment._

object RetrieveSelfEmploymentAdjustmentsFixtures {

  val incomeModel: IncomeBreakdown =
    IncomeBreakdown(
      turnover = Some(100.49),
      other = Some(100.49)
    )

  val expenseBreakdownModel: ExpensesBreakdown =
    ExpensesBreakdown(
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

  val additionsBreakdownModel: AdditionsBreakdown =
    AdditionsBreakdown(
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

  val bsasDetailModel: BsasDetail =
    BsasDetail(
      income = Some(incomeModel),
      expenses = Some(expenseBreakdownModel),
      additions = Some(additionsBreakdownModel)
    )

  val accountPeriodModel: AccountingPeriod =
    AccountingPeriod(
      startDate = LocalDate.parse("2018-10-11"),
      endDate = LocalDate.parse("2019-10-10")
    )

  val metaDataModel: Metadata =
    Metadata(
      typeOfBusiness = TypeOfBusiness.`self-employment`,
      businessId = Some("000000000000210"),
      accountingPeriod = accountPeriodModel,
      taxYear = "2019-20",
      requestedDateTime = "2019-10-14T11:33:27Z",
      bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      summaryStatus = "superseded",
      adjustedSummary = true
    )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |   "metadata": {
      |      "typeOfBusiness": "self-employment",
      |      "businessId": "000000000000210",
      |      "accountingPeriod": {
      |         "startDate": "2018-10-11",
      |         "endDate": "2019-10-10"
      |      },
      |      "taxYear": "2019-20",
      |      "requestedDateTime": "2019-10-14T11:33:27Z",
      |      "bsasId": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |      "summaryStatus": "superseded",
      |      "adjustedSummary": true
      |   },
      |   "adjustments": {
      |      "income": {
      |         "turnover": 100.49,
      |         "other": 100.49
      |      },
      |      "expenses": {
      |         "costOfGoodsBought": 100.49,
      |         "cisPaymentsToSubcontractors": 100.49,
      |         "staffCosts": 100.49,
      |         "travelCosts": 100.49,
      |         "premisesRunningCosts": 100.49,
      |         "maintenanceCosts": 100.49,
      |         "adminCosts": 100.49,
      |         "advertisingCosts": 100.49,
      |         "businessEntertainmentCosts": 100.49,
      |         "interest": 100.49,
      |         "financialCharges": 100.49,
      |         "badDebt": 100.49,
      |         "professionalFees": 100.49,
      |         "depreciation": 100.49,
      |         "other": 100.49,
      |         "consolidatedExpenses" :100.49
      |      },
      |      "additions": {
      |         "costOfGoodsBoughtDisallowable": 100.49,
      |         "cisPaymentsToSubcontractorsDisallowable": 100.49,
      |         "staffCostsDisallowable": 100.49,
      |         "travelCostsDisallowable": 100.49,
      |         "premisesRunningCostsDisallowable": 100.49,
      |         "maintenanceCostsDisallowable": 100.49,
      |         "adminCostsDisallowable": 100.49,
      |         "advertisingCostsDisallowable": 100.49,
      |         "businessEntertainmentCostsDisallowable": 100.49,
      |         "interestDisallowable": 100.49,
      |         "financialChargesDisallowable": 100.49,
      |         "badDebtDisallowable": 100.49,
      |         "professionalFeesDisallowable": 100.49,
      |         "depreciationDisallowable": 100.49,
      |         "otherDisallowable": 100.49
      |      }
      |   }
      |}
    """.stripMargin
  )

  val desJson: JsValue = Json.parse(
    """{
      | "inputs": {
      |   "incomeSourceType" : "01",
      |   "incomeSourceId" : "000000000000210",
      |   "accountingPeriodStartDate" : "2018-10-11",
      |   "accountingPeriodEndDate" : "2019-10-10"
      | },
      | "metadata": {
      |   "taxYear" : 2020,
      |   "calculationId" : "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |   "requestedDateTime" : "2019-10-14T11:33:27Z",
      |   "status" : "superseded"
      | },
      | "adjustments" : {
      |    "income": {
      |       "turnover": 100.49,
      |       "other": 100.49
      |    },
      |    "expenses" : {
      |     "costOfGoodsAllowable" : 100.49,
      |     "paymentsToSubcontractorsAllowable" :100.49,
      |     "wagesAndStaffCostsAllowable" :100.49,
      |     "carVanTravelExpensesAllowable" :100.49,
      |     "premisesRunningCostsAllowable" :100.49,
      |     "maintenanceCostsAllowable" :100.49,
      |     "adminCostsAllowable" :100.49,
      |     "advertisingCostsAllowable" :100.49,
      |     "businessEntertainmentCostsAllowable" :100.49,
      |     "interestOnBankOtherLoansAllowable" :100.49,
      |     "financeChargesAllowable" :100.49,
      |     "irrecoverableDebtsAllowable" :100.49,
      |     "professionalFeesAllowable" :100.49,
      |     "depreciationAllowable" :100.49,
      |     "otherExpensesAllowable" :100.49,
      |     "consolidatedExpenses" :100.49
      |   },
      |   "additions" :{
      |     "costOfGoodsDisallowable" : 100.49,
      |     "paymentsToSubcontractorsDisallowable" : 100.49,
      |     "wagesAndStaffCostsDisallowable" : 100.49,
      |     "carVanTravelExpensesDisallowable" : 100.49,
      |     "premisesRunningCostsDisallowable" : 100.49,
      |     "maintenanceCostsDisallowable" : 100.49,
      |     "adminCostsDisallowable" : 100.49,
      |     "advertisingCostsDisallowable" : 100.49,
      |     "businessEntertainmentCostsDisallowable" : 100.49,
      |     "interestOnBankOtherLoansDisallowable" : 100.49,
      |     "financeChargesDisallowable" : 100.49,
      |     "irrecoverableDebtsDisallowable" : 100.49,
      |     "professionalFeesDisallowable" : 100.49,
      |     "depreciationDisallowable" : 100.49,
      |     "otherExpensesDisallowable" : 100.49
      |   }
      | }
      |}
    """.stripMargin)

  val desJsonWithoutAdditions: JsValue = Json.parse(
    """{
      | "inputs": {
      |   "incomeSourceType" : "01",
      |   "incomeSourceId" : "000000000000210",
      |   "accountingPeriodStartDate" : "2018-10-11",
      |   "accountingPeriodEndDate" : "2019-10-10"
      | },
      | "metadata": {
      |   "taxYear" : 2020,
      |   "calculationId" : "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |   "requestedDateTime" : "2019-10-14T11:33:27Z",
      |   "status" : "superseded"
      | },
      | "adjustments" : {
      |    "income": {
      |       "turnover": 100.49,
      |       "other": 100.49
      |    },
      |    "expenses" : {
      |     "costOfGoodsAllowable" : 100.49,
      |     "paymentsToSubcontractorsAllowable" :100.49,
      |     "wagesAndStaffCostsAllowable" :100.49,
      |     "carVanTravelExpensesAllowable" :100.49,
      |     "premisesRunningCostsAllowable" :100.49,
      |     "maintenanceCostsAllowable" :100.49,
      |     "adminCostsAllowable" :100.49,
      |     "advertisingCostsAllowable" :100.49,
      |     "businessEntertainmentCostsAllowable" :100.49,
      |     "interestOnBankOtherLoansAllowable" :100.49,
      |     "financeChargesAllowable" :100.49,
      |     "irrecoverableDebtsAllowable" :100.49,
      |     "professionalFeesAllowable" :100.49,
      |     "depreciationAllowable" :100.49,
      |     "otherExpensesAllowable" :100.49,
      |     "consolidatedExpenses" :100.49
      |   }
      | }
      |}
    """.stripMargin)

  val desJsonWithWrongTypeOfBusiness: JsValue = Json.parse(
    """{
      | "inputs": {
      |   "incomeSourceType" : "02",
      |   "incomeSourceId" : "000000000000210",
      |   "accountingPeriodStartDate" : "2018-10-11",
      |   "accountingPeriodEndDate" : "2019-10-10"
      | },
      | "metadata": {
      |   "taxYear" : 2020,
      |   "calculationId" : "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |   "requestedDateTime" : "2019-10-14T11:33:27Z",
      |   "status" : "superseded"
      | },
      | "adjustments" : {
      |    "income": {
      |       "turnover": 100.49,
      |       "other": 100.49
      |    },
      |    "expenses" : {
      |     "costOfGoodsAllowable" : 100.49,
      |     "paymentsToSubcontractorsAllowable" :100.49,
      |     "wagesAndStaffCostsAllowable" :100.49,
      |     "carVanTravelExpensesAllowable" :100.49,
      |     "premisesRunningCostsAllowable" :100.49,
      |     "maintenanceCostsAllowable" :100.49,
      |     "adminCostsAllowable" :100.49,
      |     "advertisingCostsAllowable" :100.49,
      |     "businessEntertainmentCostsAllowable" :100.49,
      |     "interestOnBankOtherLoansAllowable" :100.49,
      |     "financeChargesAllowable" :100.49,
      |     "irrecoverableDebtsAllowable" :100.49,
      |     "professionalFeesAllowable" :100.49,
      |     "depreciationAllowable" :100.49,
      |     "otherExpensesAllowable" :100.49,
      |     "consolidatedExpenses" :100.49
      |   },
      |   "additions" :{
      |     "costOfGoodsDisallowable" : 100.49,
      |     "paymentsToSubcontractorsDisallowable" : 100.49,
      |     "wagesAndStaffCostsDisallowable" : 100.49,
      |     "carVanTravelExpensesDisallowable" : 100.49,
      |     "premisesRunningCostsDisallowable" : 100.49,
      |     "maintenanceCostsDisallowable" : 100.49,
      |     "adminCostsDisallowable" : 100.49,
      |     "advertisingCostsDisallowable" : 100.49,
      |     "businessEntertainmentCostsDisallowable" : 100.49,
      |     "interestOnBankOtherLoansDisallowable" : 100.49,
      |     "financeChargesDisallowable" : 100.49,
      |     "irrecoverableDebtsDisallowable" : 100.49,
      |     "professionalFeesDisallowable" : 100.49,
      |     "depreciationDisallowable" : 100.49,
      |     "otherExpensesDisallowable" : 100.49
      |   }
      | }
      |}
    """.stripMargin)

  val retrieveSelfEmploymentAdjustmentResponseModel = RetrieveSelfEmploymentAdjustmentsResponse(metaDataModel, bsasDetailModel)

  val hateoasResponseForSelfEmploymentAdjustments: (String, String) => String = (nino: String, bsasId: String) =>
    s"""
       |{
       |   "metadata": {
       |      "typeOfBusiness": "self-employment",
       |      "businessId": "000000000000210",
       |      "accountingPeriod": {
       |         "startDate": "2018-10-11",
       |         "endDate": "2019-10-10"
       |      },
       |      "taxYear": "2019-20",
       |      "requestedDateTime": "2019-10-14T11:33:27Z",
       |      "bsasId": "$bsasId",
       |      "summaryStatus": "superseded",
       |      "adjustedSummary": true
       |   },
       |   "adjustments": {
       |      "income": {
       |         "turnover": 100.49,
       |         "other": 100.49
       |      },
       |      "expenses": {
       |         "costOfGoodsBought": 100.49,
       |         "cisPaymentsToSubcontractors": 100.49,
       |         "staffCosts": 100.49,
       |         "travelCosts": 100.49,
       |         "premisesRunningCosts": 100.49,
       |         "maintenanceCosts": 100.49,
       |         "adminCosts": 100.49,
       |         "advertisingCosts": 100.49,
       |         "businessEntertainmentCosts": 100.49,
       |         "interest": 100.49,
       |         "financialCharges": 100.49,
       |         "badDebt": 100.49,
       |         "professionalFees": 100.49,
       |         "depreciation": 100.49,
       |         "other": 100.49,
       |         "consolidatedExpenses" :100.49
       |      },
       |      "additions": {
       |         "costOfGoodsBoughtDisallowable": 100.49,
       |         "cisPaymentsToSubcontractorsDisallowable": 100.49,
       |         "staffCostsDisallowable": 100.49,
       |         "travelCostsDisallowable": 100.49,
       |         "premisesRunningCostsDisallowable": 100.49,
       |         "maintenanceCostsDisallowable": 100.49,
       |         "adminCostsDisallowable": 100.49,
       |         "advertisingCostsDisallowable": 100.49,
       |         "businessEntertainmentCostsDisallowable": 100.49,
       |         "interestDisallowable": 100.49,
       |         "financialChargesDisallowable": 100.49,
       |         "badDebtDisallowable": 100.49,
       |         "professionalFeesDisallowable": 100.49,
       |         "depreciationDisallowable": 100.49,
       |         "otherDisallowable": 100.49
       |      }
       |   },
       |	"links": [{
       |		"href": "/individuals/self-assessment/adjustable-summary/$nino/self-employment/$bsasId?adjustedStatus=true",
       |		"method": "GET",
       |		"rel": "retrieve-adjustable-summary"
       |	}, {
       |		"href": "/individuals/self-assessment/adjustable-summary/$nino/self-employment/$bsasId/adjust",
       |		"method": "GET",
       |		"rel": "self"
       |	}]
       |}
    """.stripMargin

  val hateoasResponseWithoutAdditionsSEAdjustments: (String, String) => String = (nino: String, bsasId: String) =>
    s"""
       |{
       |   "metadata": {
       |      "typeOfBusiness": "self-employment",
       |      "businessId": "000000000000210",
       |      "accountingPeriod": {
       |         "startDate": "2018-10-11",
       |         "endDate": "2019-10-10"
       |      },
       |      "taxYear": "2019-20",
       |      "requestedDateTime": "2019-10-14T11:33:27Z",
       |      "bsasId": "$bsasId",
       |      "summaryStatus": "superseded",
       |      "adjustedSummary": true
       |   },
       |   "adjustments": {
       |      "income": {
       |         "turnover": 100.49,
       |         "other": 100.49
       |      },
       |      "expenses": {
       |         "costOfGoodsBought": 100.49,
       |         "cisPaymentsToSubcontractors": 100.49,
       |         "staffCosts": 100.49,
       |         "travelCosts": 100.49,
       |         "premisesRunningCosts": 100.49,
       |         "maintenanceCosts": 100.49,
       |         "adminCosts": 100.49,
       |         "advertisingCosts": 100.49,
       |         "businessEntertainmentCosts": 100.49,
       |         "interest": 100.49,
       |         "financialCharges": 100.49,
       |         "badDebt": 100.49,
       |         "professionalFees": 100.49,
       |         "depreciation": 100.49,
       |         "other": 100.49,
       |         "consolidatedExpenses" :100.49
       |      }
       |   },
       |	"links": [{
       |		"href": "/individuals/self-assessment/adjustable-summary/$nino/self-employment/$bsasId?adjustedStatus=true",
       |		"method": "GET",
       |		"rel": "retrieve-adjustable-summary"
       |	}, {
       |		"href": "/individuals/self-assessment/adjustable-summary/$nino/self-employment/$bsasId/adjust",
       |		"method": "GET",
       |		"rel": "self"
       |	}]
       |}
    """.stripMargin
}
