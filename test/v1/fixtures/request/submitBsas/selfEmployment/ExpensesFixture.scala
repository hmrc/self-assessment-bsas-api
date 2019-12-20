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

package v1.fixtures.request.submitBsas.selfEmployment

import play.api.libs.json.{JsValue, Json}
import v1.models.request.submitBsas.selfEmployment.{Expenses, queryMap}

object ExpensesFixture {

  val expensesModel: Expenses =
    Expenses(
      costOfGoodsBought = Some(2000.25),
      cisPaymentsToSubcontractors = Some(2000.50),
      staffCosts = Some(2000.75),
      travelCosts = Some(-2000.25),
      premisesRunningCosts = Some(-2000.50),
      maintenanceCosts = Some(-2000.75),
      adminCosts = Some(2001.25),
      advertisingCosts = Some(2001.50),
      businessEntertainmentCosts = Some(2001.75),
      interest = Some(-2001.25),
      financialCharges = Some(-2001.50),
      badDebt = Some(-2001.75),
      professionalFees = Some(2002.25),
      depreciation = Some(2002.50),
      other = Some(2002.75),
      consolidatedExpenses = Some(-2002.25)
    )

  def expensesMtdJson(model: Expenses): JsValue = {
    import model._

    val fields: Map[String, Option[BigDecimal]] =
      Map(
        "costOfGoodsAllowable" -> costOfGoodsBought,
        "cisPaymentsToSubcontractorsAllowable" -> cisPaymentsToSubcontractors,
        "wagesAndStaffCostsAllowable" -> staffCosts,
        "carVanTravelExpensesAllowable" -> travelCosts,
        "premisesRunningCostsAllowable" -> premisesRunningCosts,
        "maintenanceCostsAllowable" -> maintenanceCosts,
        "adminCostsAllowable" -> adminCosts,
        "advertisingCostsAllowable" -> advertisingCosts,
        "businessEntertainmentCostsAllowable" -> businessEntertainmentCosts,
        "interestOnBankOtherLoansAllowable" -> interest,
        "financeChargesAllowable" -> financialCharges,
        "irrecoverableDebtsAllowable" -> badDebt,
        "professionalFeesAllowable" -> professionalFees,
        "depreciationAllowable" -> depreciation,
        "otherExpensesAllowable" -> other,
        "consolidatedExpenses" -> consolidatedExpenses
      )

    Json.toJsObject(queryMap(fields))
  }

  def expensesDesJson(model: Expenses): JsValue = {
    import model._

    val fields: Map[String, Option[BigDecimal]] =
      Map(
        "costOfGoodsBought" -> costOfGoodsBought,
        "cisPaymentsToSubcontractors" -> cisPaymentsToSubcontractors,
        "staffCosts" -> staffCosts,
        "travelCosts" -> travelCosts,
        "premisesRunningCosts" -> premisesRunningCosts,
        "maintenanceCosts" -> maintenanceCosts,
        "adminCosts" -> adminCosts,
        "advertisingCosts" -> advertisingCosts,
        "businessEntertainmentCosts" -> businessEntertainmentCosts,
        "interest" -> interest,
        "financialCharges" -> financialCharges,
        "badDebt" -> badDebt,
        "professionalFees" -> professionalFees,
        "depreciation" -> depreciation,
        "other" -> other,
        "consolidatedExpenses" -> consolidatedExpenses
      )

    Json.toJsObject(queryMap(fields))
  }
}
