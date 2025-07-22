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

package v6.selfEmploymentBsas.submit.def1.model.request.fixtures

import play.api.libs.json.{JsValue, Json}
import v6.selfEmploymentBsas.submit.def1.model.request.{Expenses, queryMap}

object ExpensesFixture {

  val expenses: Expenses =
    Expenses(
      costOfGoodsAllowable = Some(2000.25),
      paymentsToSubcontractorsAllowable = Some(2000.50),
      wagesAndStaffCostsAllowable = Some(2000.75),
      carVanTravelExpensesAllowable = Some(-2000.25),
      premisesRunningCostsAllowable = Some(-2000.50),
      maintenanceCostsAllowable = Some(-2000.75),
      adminCostsAllowable = Some(2001.25),
      advertisingCostsAllowable = Some(2001.50),
      businessEntertainmentCostsAllowable = Some(2001.75),
      interestOnBankOtherLoansAllowable = Some(-2001.25),
      financeChargesAllowable = Some(-2001.50),
      irrecoverableDebtsAllowable = Some(-2001.75),
      professionalFeesAllowable = Some(2002.25),
      depreciationAllowable = Some(2002.50),
      otherExpensesAllowable = Some(2002.75),
      consolidatedExpenses = None
    )

  def expensesToDesJson(expenses: Expenses): JsValue = {
    import expenses.*

    val desFields: Map[String, Option[BigDecimal]] =
      Map(
        "costOfGoodsAllowable"                -> costOfGoodsAllowable,
        "paymentsToSubcontractorsAllowable"   -> paymentsToSubcontractorsAllowable,
        "wagesAndStaffCostsAllowable"         -> wagesAndStaffCostsAllowable,
        "carVanTravelExpensesAllowable"       -> carVanTravelExpensesAllowable,
        "premisesRunningCostsAllowable"       -> premisesRunningCostsAllowable,
        "maintenanceCostsAllowable"           -> maintenanceCostsAllowable,
        "adminCostsAllowable"                 -> adminCostsAllowable,
        "advertisingCostsAllowable"           -> advertisingCostsAllowable,
        "businessEntertainmentCostsAllowable" -> businessEntertainmentCostsAllowable,
        "interestOnBankOtherLoansAllowable"   -> interestOnBankOtherLoansAllowable,
        "financeChargesAllowable"             -> financeChargesAllowable,
        "irrecoverableDebtsAllowable"         -> irrecoverableDebtsAllowable,
        "professionalFeesAllowable"           -> professionalFeesAllowable,
        "depreciationAllowable"               -> depreciationAllowable,
        "otherExpensesAllowable"              -> otherExpensesAllowable
      )

    Json.toJsObject(queryMap(desFields))
  }

  def expensesFromMtdJson(expenses: Expenses): JsValue = {
    import expenses.*

    val vendorSuppliedFields: Map[String, Option[BigDecimal]] =
      Map(
        "costOfGoodsAllowable"                -> costOfGoodsAllowable,
        "paymentsToSubcontractorsAllowable"   -> paymentsToSubcontractorsAllowable,
        "wagesAndStaffCostsAllowable"         -> wagesAndStaffCostsAllowable,
        "carVanTravelExpensesAllowable"       -> carVanTravelExpensesAllowable,
        "premisesRunningCostsAllowable"       -> premisesRunningCostsAllowable,
        "maintenanceCostsAllowable"           -> maintenanceCostsAllowable,
        "adminCostsAllowable"                 -> adminCostsAllowable,
        "advertisingCostsAllowable"           -> advertisingCostsAllowable,
        "businessEntertainmentCostsAllowable" -> businessEntertainmentCostsAllowable,
        "interestOnBankOtherLoansAllowable"   -> interestOnBankOtherLoansAllowable,
        "financeChargesAllowable"             -> financeChargesAllowable,
        "irrecoverableDebtsAllowable"         -> irrecoverableDebtsAllowable,
        "professionalFeesAllowable"           -> professionalFeesAllowable,
        "depreciationAllowable"               -> depreciationAllowable,
        "otherExpensesAllowable"              -> otherExpensesAllowable
      )

    Json.toJsObject(queryMap(vendorSuppliedFields))
  }

}
