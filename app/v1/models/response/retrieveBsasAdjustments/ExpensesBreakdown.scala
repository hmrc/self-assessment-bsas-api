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
import utils.NestedJsonReads

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

object ExpensesBreakdown extends NestedJsonReads {

  val empty = ExpensesBreakdown(None, None, None, None, None, None, None, None, None, None, None, None, None, None, None, None)

  implicit val reads: Reads[ExpensesBreakdown] = (
    (JsPath \ "adjustments" \ "expenses" \ "costOfGoodsAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "paymentsToSubContractorsAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "wagesAndStaffCostsAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "carVanTravelExpensesAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "premisesRunningCostsAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "maintenanceCostsAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "adminCostsAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "advertisingCostsAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "businessEntertainmentCostsAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "interestOnBankOtherLoansAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "financeChargesAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "irrecoverableDebtsAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "professionalFeesAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "depreciationAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "otherExpensesAllowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "expenses" \ "consolidatedExpenses").readNestedNullable[BigDecimal]
    ) (ExpensesBreakdown.apply _)

  implicit val writes: OWrites[ExpensesBreakdown] = Json.writes[ExpensesBreakdown]
}
