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

import play.api.libs.json.{JsValue, Json}
import v7.selfEmploymentBsas.submit.def2.model.request.{Expenses, queryMap}

object ExpensesFixture {

  val expenses: Expenses =
    Expenses(
      costOfGoods = Some(2000.25),
      paymentsToSubcontractors = Some(2000.50),
      wagesAndStaffCosts = Some(2000.75),
      carVanTravelExpenses = Some(-2000.25),
      premisesRunningCosts = Some(-2000.50),
      maintenanceCosts = Some(-2000.75),
      adminCosts = Some(2001.25),
      advertisingCosts = Some(2001.50),
      businessEntertainmentCosts = Some(2001.75),
      interestOnBankOtherLoans = Some(-2001.25),
      financeCharges = Some(-2001.50),
      irrecoverableDebts = Some(-2001.75),
      professionalFees = Some(2002.25),
      depreciation = Some(2002.50),
      otherExpenses = Some(2002.75),
      consolidatedExpenses = None
    )

  def expensesToDesJson(expenses: Expenses): JsValue = {
    import expenses.*

    val desFields: Map[String, Option[BigDecimal]] =
      Map(
        "costOfGoodsAllowable"                -> costOfGoods,
        "paymentsToSubcontractorsAllowable"   -> paymentsToSubcontractors,
        "wagesAndStaffCostsAllowable"         -> wagesAndStaffCosts,
        "carVanTravelExpensesAllowable"       -> carVanTravelExpenses,
        "premisesRunningCostsAllowable"       -> premisesRunningCosts,
        "maintenanceCostsAllowable"           -> maintenanceCosts,
        "adminCostsAllowable"                 -> adminCosts,
        "advertisingCostsAllowable"           -> advertisingCosts,
        "businessEntertainmentCostsAllowable" -> businessEntertainmentCosts,
        "interestOnBankOtherLoansAllowable"   -> interestOnBankOtherLoans,
        "financeChargesAllowable"             -> financeCharges,
        "irrecoverableDebtsAllowable"         -> irrecoverableDebts,
        "professionalFeesAllowable"           -> professionalFees,
        "depreciationAllowable"               -> depreciation,
        "otherExpensesAllowable"              -> otherExpenses
      )

    Json.toJsObject(queryMap(desFields))
  }

  def expensesFromMtdJson(expenses: Expenses): JsValue = {
    import expenses.*

    val vendorSuppliedFields: Map[String, Option[BigDecimal]] =
      Map(
        "costOfGoods"                -> costOfGoods,
        "paymentsToSubcontractors"   -> paymentsToSubcontractors,
        "wagesAndStaffCosts"         -> wagesAndStaffCosts,
        "carVanTravelExpenses"       -> carVanTravelExpenses,
        "premisesRunningCosts"       -> premisesRunningCosts,
        "maintenanceCosts"           -> maintenanceCosts,
        "adminCosts"                 -> adminCosts,
        "advertisingCosts"           -> advertisingCosts,
        "businessEntertainmentCosts" -> businessEntertainmentCosts,
        "interestOnBankOtherLoans"   -> interestOnBankOtherLoans,
        "financeCharges"             -> financeCharges,
        "irrecoverableDebts"         -> irrecoverableDebts,
        "professionalFees"           -> professionalFees,
        "depreciation"               -> depreciation,
        "otherExpenses"              -> otherExpenses
      )

    Json.toJsObject(queryMap(vendorSuppliedFields))
  }

}
