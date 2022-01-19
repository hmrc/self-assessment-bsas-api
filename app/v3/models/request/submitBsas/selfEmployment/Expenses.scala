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

package v3.models.request.submitBsas.selfEmployment

import play.api.libs.functional.syntax._
import play.api.libs.json.{ JsPath, Json, OWrites, Reads }

case class Expenses(costOfGoodsBought: Option[BigDecimal],
                    cisPaymentsToSubcontractors: Option[BigDecimal],
                    staffCosts: Option[BigDecimal],
                    travelCosts: Option[BigDecimal],
                    premisesRunningCosts: Option[BigDecimal],
                    maintenanceCosts: Option[BigDecimal],
                    adminCosts: Option[BigDecimal],
                    advertisingCosts: Option[BigDecimal],
                    businessEntertainmentCosts: Option[BigDecimal],
                    interest: Option[BigDecimal],
                    financialCharges: Option[BigDecimal],
                    badDebt: Option[BigDecimal],
                    professionalFees: Option[BigDecimal],
                    depreciation: Option[BigDecimal],
                    other: Option[BigDecimal],
                    consolidatedExpenses: Option[BigDecimal]) {

  //noinspection ScalaStyle
  def isNonConsolidatedExpensesEmpty: Boolean =  costOfGoodsBought.isEmpty &&
                        cisPaymentsToSubcontractors.isEmpty &&
                        staffCosts.isEmpty &&
                        travelCosts.isEmpty &&
                        premisesRunningCosts.isEmpty &&
                        maintenanceCosts.isEmpty &&
                        adminCosts.isEmpty &&
                        advertisingCosts.isEmpty &&
                        businessEntertainmentCosts.isEmpty &&
                        interest.isEmpty &&
                        financialCharges.isEmpty &&
                        badDebt.isEmpty &&
                        professionalFees.isEmpty &&
                        depreciation.isEmpty &&
                        other.isEmpty

  def isConsolidatedExpensesEmpty: Boolean = consolidatedExpenses.isEmpty

  def isEmpty: Boolean = isNonConsolidatedExpensesEmpty && isConsolidatedExpensesEmpty

  def isBothSupplied: Boolean = !isNonConsolidatedExpensesEmpty && !isConsolidatedExpensesEmpty
}

object Expenses {
  implicit val reads: Reads[Expenses] = Json.reads[Expenses]
  implicit val writes: OWrites[Expenses] = (
    (JsPath \ "costOfGoodsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "paymentsToSubcontractorsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "wagesAndStaffCostsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "carVanTravelExpensesAllowable").writeNullable[BigDecimal] and
      (JsPath \ "premisesRunningCostsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "maintenanceCostsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "adminCostsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "advertisingCostsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "businessEntertainmentCostsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "interestOnBankOtherLoansAllowable").writeNullable[BigDecimal] and
      (JsPath \ "financeChargesAllowable").writeNullable[BigDecimal] and
      (JsPath \ "irrecoverableDebtsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "professionalFeesAllowable").writeNullable[BigDecimal] and
      (JsPath \ "depreciationAllowable").writeNullable[BigDecimal] and
      (JsPath \ "otherExpensesAllowable").writeNullable[BigDecimal] and
      (JsPath \ "consolidatedExpenses").writeNullable[BigDecimal]
  )(unlift(Expenses.unapply))
}
