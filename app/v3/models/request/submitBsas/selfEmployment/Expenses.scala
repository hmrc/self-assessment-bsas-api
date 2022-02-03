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

case class Expenses(costOfGoodsAllowable: Option[BigDecimal],
                    paymentsToSubcontractorsAllowable: Option[BigDecimal],
                    wagesAndStaffCostsAllowable: Option[BigDecimal],
                    carVanTravelExpensesAllowable: Option[BigDecimal],
                    premisesRunningCostsAllowable: Option[BigDecimal],
                    maintenanceCostsAllowable: Option[BigDecimal],
                    adminCostsAllowable: Option[BigDecimal],
                    interestOnBankOtherLoansAllowable: Option[BigDecimal],
                    financeChargesAllowable: Option[BigDecimal],
                    irrecoverableDebtsAllowable: Option[BigDecimal],
                    professionalFeesAllowable: Option[BigDecimal],
                    depreciationAllowable: Option[BigDecimal],
                    otherExpensesAllowable: Option[BigDecimal],
                    advertisingCostsAllowable: Option[BigDecimal],
                    businessEntertainmentCostsAllowable: Option[BigDecimal],
                    consolidatedExpenses: Option[BigDecimal]) {

  //noinspection ScalaStyle
  def isNonConsolidatedExpensesEmpty: Boolean =  costOfGoodsAllowable.isEmpty &&
                        paymentsToSubcontractorsAllowable.isEmpty &&
                        wagesAndStaffCostsAllowable.isEmpty &&
                        carVanTravelExpensesAllowable.isEmpty &&
                        premisesRunningCostsAllowable.isEmpty &&
                        maintenanceCostsAllowable.isEmpty &&
                        adminCostsAllowable.isEmpty &&
                        interestOnBankOtherLoansAllowable.isEmpty &&
                        financeChargesAllowable.isEmpty &&
                        irrecoverableDebtsAllowable.isEmpty &&
                        professionalFeesAllowable.isEmpty &&
                        depreciationAllowable.isEmpty &&
                        otherExpensesAllowable.isEmpty &&
                        advertisingCostsAllowable.isEmpty &&
                        businessEntertainmentCostsAllowable.isEmpty

  def isConsolidatedExpensesEmpty: Boolean = consolidatedExpenses.isEmpty

  def isEmpty: Boolean = isNonConsolidatedExpensesEmpty && isConsolidatedExpensesEmpty

  def isBothSupplied: Boolean = !isNonConsolidatedExpensesEmpty && !isConsolidatedExpensesEmpty
}

object Expenses {
  implicit val reads: Reads[Expenses] = Json.reads[Expenses]
  implicit val writes: OWrites[Expenses] = Json.writes[Expenses]
}
