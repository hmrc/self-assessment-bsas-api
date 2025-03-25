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

import play.api.libs.json.{Json, OFormat}

case class Expenses(
    costOfGoodsAllowable: Option[BigDecimal],
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
  implicit val format: OFormat[Expenses] = Json.format[Expenses]
}
