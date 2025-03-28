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

package v7.selfEmploymentBsas.submit.def2.model.request

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsPath, Json, Reads, Writes}

case class Expenses(
    costOfGoods: Option[BigDecimal],
    paymentsToSubcontractors: Option[BigDecimal],
    wagesAndStaffCosts: Option[BigDecimal],
    carVanTravelExpenses: Option[BigDecimal],
    premisesRunningCosts: Option[BigDecimal],
    maintenanceCosts: Option[BigDecimal],
    adminCosts: Option[BigDecimal],
    interestOnBankOtherLoans: Option[BigDecimal],
    financeCharges: Option[BigDecimal],
    irrecoverableDebts: Option[BigDecimal],
    professionalFees: Option[BigDecimal],
    depreciation: Option[BigDecimal],
    otherExpenses: Option[BigDecimal],
    advertisingCosts: Option[BigDecimal],
    businessEntertainmentCosts: Option[BigDecimal],
    consolidatedExpenses: Option[BigDecimal]
) {

  val hasOnlyConsolidatedExpenses: Boolean =
    this match {
      case Expenses(None, None, None, None, None, None, None, None, None, None, None, None, None, None, None, Some(_)) =>
        true
      case _ =>
        false
    }

}

object Expenses {

  implicit val writes: Writes[Expenses] = (
    (JsPath \ "costOfGoodsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "paymentsToSubcontractorsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "wagesAndStaffCostsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "carVanTravelExpensesAllowable").writeNullable[BigDecimal] and
      (JsPath \ "premisesRunningCostsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "maintenanceCostsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "adminCostsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "interestOnBankOtherLoansAllowable").writeNullable[BigDecimal] and
      (JsPath \ "financeChargesAllowable").writeNullable[BigDecimal] and
      (JsPath \ "irrecoverableDebtsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "professionalFeesAllowable").writeNullable[BigDecimal] and
      (JsPath \ "depreciationAllowable").writeNullable[BigDecimal] and
      (JsPath \ "otherExpensesAllowable").writeNullable[BigDecimal] and
      (JsPath \ "advertisingCostsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "businessEntertainmentCostsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "consolidatedExpenses").writeNullable[BigDecimal]
  )(unlift(Expenses.unapply))

  implicit val reads: Reads[Expenses] = Json.reads[Expenses]
}
