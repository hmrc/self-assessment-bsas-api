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

package v7.selfEmploymentBsas.submit.def2.model.request.fixtures

import play.api.libs.json.{JsObject, JsValue, Json}
import v7.selfEmploymentBsas.submit.def2.model.request.Def2_SubmitSelfEmploymentBsasRequestBody
import v7.selfEmploymentBsas.submit.def2.model.request.fixtures.AdditionsFixture._
import v7.selfEmploymentBsas.submit.def2.model.request.fixtures.ExpensesFixture._
import v7.selfEmploymentBsas.submit.def2.model.request.fixtures.IncomeFixture._

import scala.collection.mutable.ListBuffer

object SubmitSelfEmploymentBsasFixtures {

  val submitSelfEmploymentBsasRequestBody: Def2_SubmitSelfEmploymentBsasRequestBody =
    Def2_SubmitSelfEmploymentBsasRequestBody(
      income = Some(income),
      additions = Some(additions),
      expenses = Some(expenses),
      zeroAdjustments = None
    )

  val submitSelfEmploymentBsasRequestBodyWithoutIncome: Def2_SubmitSelfEmploymentBsasRequestBody =
    Def2_SubmitSelfEmploymentBsasRequestBody(
      income = None,
      additions = Some(additions),
      expenses = Some(expenses),
      zeroAdjustments = None
    )

  val emptySubmitSelfEmploymentBsasRequestBody: Def2_SubmitSelfEmploymentBsasRequestBody =
    Def2_SubmitSelfEmploymentBsasRequestBody(
      income = None,
      additions = None,
      expenses = None,
      zeroAdjustments = None
    )

  val mtdRequestJson: JsValue = Json.parse(
    """
      |{
      |   "income":{
      |      "turnover":1000.25,
      |      "other":1000.5
      |   },
      |   "expenses":{
      |     "costOfGoodsAllowable":2000.25,
      |     "paymentsToSubcontractorsAllowable":2000.5,
      |     "wagesAndStaffCostsAllowable":2000.75,
      |     "carVanTravelExpensesAllowable":-2000.25,
      |     "premisesRunningCostsAllowable":-2000.5,
      |     "maintenanceCostsAllowable":-2000.75,
      |     "adminCostsAllowable":2001.25,
      |     "interestOnBankOtherLoansAllowable":-2001.25,
      |     "financeChargesAllowable":-2001.5,
      |     "irrecoverableDebtsAllowable":-2001.75,
      |     "professionalFeesAllowable":2002.25,
      |     "depreciationAllowable":2002.5,
      |     "otherExpensesAllowable":2002.75,
      |     "advertisingCostsAllowable":2001.5,
      |     "businessEntertainmentCostsAllowable":2001.75
      |   },
      |   "additions":{
      |     "costOfGoodsDisallowable":3000.1,
      |     "paymentsToSubcontractorsDisallowable":3000.2,
      |     "wagesAndStaffCostsDisallowable":3000.3,
      |     "carVanTravelExpensesDisallowable":3000.4,
      |     "premisesRunningCostsDisallowable":3000.5,
      |     "maintenanceCostsDisallowable":-3000.1,
      |     "adminCostsDisallowable":-3000.2,
      |     "interestOnBankOtherLoansDisallowable":-3000.5,
      |     "financeChargesDisallowable":3000.6,
      |     "irrecoverableDebtsDisallowable":-3000.6,
      |     "professionalFeesDisallowable":3000.7,
      |     "depreciationDisallowable":-3000.7,
      |     "otherExpensesDisallowable":3000.8,
      |     "advertisingCostsDisallowable":-3000.3,
      |     "businessEntertainmentCostsDisallowable":-3000.4
      |   }
      |}
      |""".stripMargin
  )

  val parsedMtdRequestBody: Def2_SubmitSelfEmploymentBsasRequestBody =
    mtdRequestJson.as[Def2_SubmitSelfEmploymentBsasRequestBody]

  val downstreamRequestWithOnlyZeroAdjustments: JsValue = Json.parse(
    """
      |{
      |    "incomeSourceType": "01",
      |    "adjustments": {
      |        "zeroAdjustments": true
      |    }
      |}
    """.stripMargin
  )

  def mtdRequestWithOnlyZeroAdjustments(zeroAdjustments: Boolean): JsValue = Json.parse(
    s"""
      |{
      |    "zeroAdjustments": $zeroAdjustments
      |}
    """.stripMargin
  )

  val parsedMtdRequestWithOnlyZeroAdjustmentsBody: Def2_SubmitSelfEmploymentBsasRequestBody =
    mtdRequestWithOnlyZeroAdjustments(true).as[Def2_SubmitSelfEmploymentBsasRequestBody]

  def mtdRequestWithZeroAndOtherAdjustments(zeroAdjustments: Boolean): JsValue = Json.parse(
    s"""
      |{
      |    "zeroAdjustments": $zeroAdjustments,
      |    "income": {
      |        "turnover": 1000.25
      |    },
      |    "expenses": {
      |        "costOfGoodsAllowable": 2000.25
      |    },
      |    "additions": {
      |        "costOfGoodsDisallowable": 3000.1
      |    }
      |}
    """.stripMargin
  )

  val mtdRequestWithOnlyConsolidatedExpenses: JsValue = Json.parse("""
      |{
      |  "income": {
      |    "turnover": 1000.25,
      |    "other": 1000.5
      |  },
      |  "expenses": {
      |    "consolidatedExpenses": 2002.75
      |  }
      |}
      |""".stripMargin)

  val parsedMtdRequestWithOnlyConsolidatedExpensesBody: Def2_SubmitSelfEmploymentBsasRequestBody =
    mtdRequestWithOnlyConsolidatedExpenses.as[Def2_SubmitSelfEmploymentBsasRequestBody]

  val mtdRequestWithOnlyAdditionsExpenses: JsValue = Json.parse("""
      |{
      |  "income": {
      |    "turnover": 1000.25,
      |    "other": 1000.5
      |  },
      |  "additions":{
      |    "costOfGoodsDisallowable":3000.1,
      |    "paymentsToSubcontractorsDisallowable":3000.2,
      |    "wagesAndStaffCostsDisallowable":3000.3,
      |    "carVanTravelExpensesDisallowable":3000.4,
      |    "premisesRunningCostsDisallowable":3000.5,
      |    "maintenanceCostsDisallowable":-3000.1,
      |    "adminCostsDisallowable":-3000.2,
      |    "interestOnBankOtherLoansDisallowable":-3000.5,
      |    "financeChargesDisallowable":3000.6,
      |    "irrecoverableDebtsDisallowable":-3000.6,
      |    "professionalFeesDisallowable":3000.7,
      |    "depreciationDisallowable":-3000.7,
      |    "otherExpensesDisallowable":3000.8,
      |    "advertisingCostsDisallowable":-3000.3,
      |    "businessEntertainmentCostsDisallowable":-3000.4
      |  }
      |}
      |""".stripMargin)

  val parsedMtdRequestWithOnlyAdditionsExpenses: Def2_SubmitSelfEmploymentBsasRequestBody =
    mtdRequestWithOnlyAdditionsExpenses.as[Def2_SubmitSelfEmploymentBsasRequestBody]

  val mtdRequestWithBothExpenses: JsValue = Json.parse("""
      |{
      |  "income": {
      |    "turnover": 1000.25,
      |    "other": 1000.5
      |  },
      |  "additions":{
      |    "costOfGoodsDisallowable":3000.1,
      |    "paymentsToSubcontractorsDisallowable":3000.2,
      |    "wagesAndStaffCostsDisallowable":3000.3,
      |    "carVanTravelExpensesDisallowable":3000.4,
      |    "premisesRunningCostsDisallowable":3000.5,
      |    "maintenanceCostsDisallowable":-3000.1,
      |    "adminCostsDisallowable":-3000.2,
      |    "advertisingCostsDisallowable":-3000.3,
      |    "businessEntertainmentCostsDisallowable":-3000.4,
      |    "interestOnBankOtherLoansDisallowable":-3000.5,
      |    "financeChargesDisallowable":3000.6,
      |    "irrecoverableDebtsDisallowable":-3000.6,
      |    "professionalFeesDisallowable":3000.7,
      |    "depreciationDisallowable":-3000.7,
      |    "otherExpensesDisallowable":3000.8
      |  },
      |  "expenses":{
      |    "costOfGoodsAllowable":2000.25,
      |    "paymentsToSubcontractorsAllowable":2000.5,
      |    "wagesAndStaffCostsAllowable":2000.75,
      |    "carVanTravelExpensesAllowable":-2000.25,
      |    "premisesRunningCostsAllowable":-2000.5,
      |    "maintenanceCostsAllowable":-2000.75,
      |    "adminCostsAllowable":2001.25,
      |    "advertisingCostsAllowable":2001.5,
      |    "businessEntertainmentCostsAllowable":2001.75,
      |    "interestOnBankOtherLoansAllowable":-2001.25,
      |    "financeChargesAllowable":-2001.5,
      |    "irrecoverableDebtsAllowable":-2001.75,
      |    "professionalFeesAllowable":2002.25,
      |    "depreciationAllowable":2002.5,
      |    "otherExpensesAllowable":2002.75,
      |    "consolidatedExpenses": -2002.25
      |   }
      |}""".stripMargin)

  val requestToIfs: JsValue = Json.parse("""
      |{
      | "incomeSourceType": "01",
      | "adjustments": {
      |    "income": {
      |      "turnover": 1000.25,
      |      "other": 1000.5
      |    },
      |    "expenses": {
      |      "costOfGoodsAllowable": 2000.25,
      |      "paymentsToSubcontractorsAllowable": 2000.5,
      |      "wagesAndStaffCostsAllowable": 2000.75,
      |      "carVanTravelExpensesAllowable": -2000.25,
      |      "premisesRunningCostsAllowable": -2000.5,
      |      "maintenanceCostsAllowable": -2000.75,
      |      "adminCostsAllowable": 2001.25,
      |      "advertisingCostsAllowable": 2001.5,
      |      "businessEntertainmentCostsAllowable": 2001.75,
      |      "interestOnBankOtherLoansAllowable": -2001.25,
      |      "financeChargesAllowable": -2001.5,
      |      "irrecoverableDebtsAllowable": -2001.75,
      |      "professionalFeesAllowable": 2002.25,
      |      "depreciationAllowable": 2002.5,
      |      "otherExpensesAllowable": 2002.75
      |    },
      |    "additions": {
      |      "costOfGoodsDisallowable": 3000.1,
      |      "paymentsToSubcontractorsDisallowable": 3000.2,
      |      "wagesAndStaffCostsDisallowable": 3000.3,
      |      "carVanTravelExpensesDisallowable": 3000.4,
      |      "premisesRunningCostsDisallowable": 3000.5,
      |      "maintenanceCostsDisallowable": -3000.1,
      |      "adminCostsDisallowable": -3000.2,
      |      "advertisingCostsDisallowable": -3000.3,
      |      "businessEntertainmentCostsDisallowable": -3000.4,
      |      "interestOnBankOtherLoansDisallowable": -3000.5,
      |      "financeChargesDisallowable": 3000.6,
      |      "irrecoverableDebtsDisallowable": -3000.6,
      |      "professionalFeesDisallowable": 3000.7,
      |      "depreciationDisallowable": -3000.7,
      |      "otherExpensesDisallowable": 3000.8
      |    }
      |  }
      |}""".stripMargin)

  def submitSelfEmploymentBsasRequestBodyDesJson(dataObject: Def2_SubmitSelfEmploymentBsasRequestBody): JsValue = {
    import dataObject._

    val jsObjects: ListBuffer[JsObject] = ListBuffer.empty[JsObject]

    if (income.nonEmpty) {
      jsObjects += Json.obj("income" -> incomeJson(income.get))
    }

    if (additions.nonEmpty) {
      jsObjects += Json.obj("additions" -> additionsFromVendorJson(additions.get))
    }

    if (expenses.nonEmpty) {
      jsObjects += Json.obj("expenses" -> expensesFromMtdJson(expenses.get))
    }

    val json = jsObjects.fold(Json.parse("""{}""").as[JsObject])((a: JsObject, b: JsObject) => a ++ b)
    json
  }

}
