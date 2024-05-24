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

package v5.selfEmploymentBsas.submit.def1.model.request.fixtures

import play.api.libs.json.{JsValue, Json}
import v5.selfEmploymentBsas.submit.def1.model.request.{Additions, queryMap}

object AdditionsFixture {

  val additionsModel: Additions =
    Additions(
      costOfGoodsDisallowable = Some(3000.1),
      paymentsToSubcontractorsDisallowable = Some(3000.2),
      wagesAndStaffCostsDisallowable = Some(3000.3),
      carVanTravelExpensesDisallowable = Some(3000.4),
      premisesRunningCostsDisallowable = Some(3000.5),
      maintenanceCostsDisallowable = Some(-3000.1),
      adminCostsDisallowable = Some(-3000.2),
      advertisingCostsDisallowable = Some(-3000.3),
      businessEntertainmentCostsDisallowable = Some(-3000.4),
      interestOnBankOtherLoansDisallowable = Some(-3000.5),
      financeChargesDisallowable = Some(3000.6),
      irrecoverableDebtsDisallowable = Some(-3000.6),
      professionalFeesDisallowable = Some(3000.7),
      depreciationDisallowable = Some(-3000.7),
      otherExpensesDisallowable = Some(3000.8)
    )

  def additionsToDesJson(model: Additions): JsValue = {
    import model._

    val desFields: Map[String, Option[BigDecimal]] =
      Map(
        "costOfGoodsDisallowable"                -> costOfGoodsDisallowable,
        "paymentsToSubcontractorsDisallowable"   -> paymentsToSubcontractorsDisallowable,
        "wagesAndStaffCostsDisallowable"         -> wagesAndStaffCostsDisallowable,
        "carVanTravelExpensesDisallowable"       -> carVanTravelExpensesDisallowable,
        "premisesRunningCostsDisallowable"       -> premisesRunningCostsDisallowable,
        "maintenanceCostsDisallowable"           -> maintenanceCostsDisallowable,
        "adminCostsDisallowable"                 -> adminCostsDisallowable,
        "advertisingCostsDisallowable"           -> advertisingCostsDisallowable,
        "businessEntertainmentCostsDisallowable" -> businessEntertainmentCostsDisallowable,
        "interestOnBankOtherLoansDisallowable"   -> interestOnBankOtherLoansDisallowable,
        "financeChargesDisallowable"             -> financeChargesDisallowable,
        "irrecoverableDebtsDisallowable"         -> irrecoverableDebtsDisallowable,
        "professionalFeesDisallowable"           -> professionalFeesDisallowable,
        "depreciationDisallowable"               -> depreciationDisallowable,
        "otherExpensesDisallowable"              -> otherExpensesDisallowable
      )

    Json.toJsObject(queryMap(desFields))
  }

  def additionsFromVendorJson(model: Additions): JsValue = {
    import model._

    val vendorSuppliedFields: Map[String, Option[BigDecimal]] =
      Map(
        "costOfGoodsDisallowable"                -> costOfGoodsDisallowable,
        "paymentsToSubcontractorsDisallowable"   -> paymentsToSubcontractorsDisallowable,
        "wagesAndStaffCostsDisallowable"         -> wagesAndStaffCostsDisallowable,
        "carVanTravelExpensesDisallowable"       -> carVanTravelExpensesDisallowable,
        "premisesRunningCostsDisallowable"       -> premisesRunningCostsDisallowable,
        "maintenanceCostsDisallowable"           -> maintenanceCostsDisallowable,
        "adminCostsDisallowable"                 -> adminCostsDisallowable,
        "advertisingCostsDisallowable"           -> advertisingCostsDisallowable,
        "businessEntertainmentCostsDisallowable" -> businessEntertainmentCostsDisallowable,
        "interestOnBankOtherLoansDisallowable"   -> interestOnBankOtherLoansDisallowable,
        "financeChargesDisallowable"             -> financeChargesDisallowable,
        "irrecoverableDebtsDisallowable"         -> irrecoverableDebtsDisallowable,
        "professionalFeesDisallowable"           -> professionalFeesDisallowable,
        "depreciationDisallowable"               -> depreciationDisallowable,
        "otherExpensesDisallowable"              -> otherExpensesDisallowable
      )

    Json.toJsObject(queryMap(vendorSuppliedFields))
  }

}
