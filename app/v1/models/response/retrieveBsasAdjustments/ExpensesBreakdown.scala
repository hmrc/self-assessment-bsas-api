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

package v1.models.response.retrieveBsasAdjustments

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class ExpensesBreakdown(
                              costOfGoodsBought: Option[BigDecimal],
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
                              consolidatedExpenses: Option[BigDecimal]
                            )

object ExpensesBreakdown {

  implicit val reads: Reads[ExpensesBreakdown] = (
    (JsPath \ "adjustments" \ "expenses" \ "costOfGoodsAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "paymentsToSubContractorsAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "wagesAndStaffCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "carVanTravelExpensesAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "premisesRunningCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "maintenanceCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "adminCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "advertisingCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "businessEntertainmentCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "interestOnBankOtherLoansAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "financeChargesAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "irrecoverableDebtsAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "professionalFeesAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "depreciationAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "otherExpensesAllowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "consolidatedExpenses").readNullable[BigDecimal]
    ) (ExpensesBreakdown.apply _)

  implicit val writes: OWrites[ExpensesBreakdown] = Json.writes[ExpensesBreakdown]
}
