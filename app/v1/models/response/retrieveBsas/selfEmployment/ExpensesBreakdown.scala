/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.response.retrieveBsas.selfEmployment

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class ExpensesBreakdown(costOfGoodsBought: Option[BigDecimal],
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
                             consolidatedExpenses: Option[BigDecimal])

object ExpensesBreakdown {
  implicit val writes: OWrites[ExpensesBreakdown] = Json.writes[ExpensesBreakdown]

  implicit val reads: Reads[ExpensesBreakdown] = (
    (JsPath \ "costOfGoodsAllowable").readNullable[BigDecimal] and
      (JsPath \ "paymentsToSubcontractorsAllowable").readNullable[BigDecimal] and
      (JsPath \ "wagesAndStaffCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "carVanTravelExpensesAllowable").readNullable[BigDecimal] and
      (JsPath \ "premisesRunningCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "maintenanceCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "adminCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "advertisingCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "businessEntertainmentCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "interestOnBankOtherLoansAllowable").readNullable[BigDecimal] and
      (JsPath \ "financeChargesAllowable").readNullable[BigDecimal] and
      (JsPath \ "irrecoverableDebtsAllowable").readNullable[BigDecimal] and
      (JsPath \ "professionalFeesAllowable").readNullable[BigDecimal] and
      (JsPath \ "depreciationAllowable").readNullable[BigDecimal] and
      (JsPath \ "otherExpensesAllowable").readNullable[BigDecimal] and
      (JsPath \ "consolidatedExpenses").readNullable[BigDecimal]
  )(ExpensesBreakdown.apply _)
}
