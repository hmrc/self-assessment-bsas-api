/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.request.submitBsas.selfEmployment

import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import play.api.libs.functional.syntax._

case class SeExpenses(costOfGoodsBought: Option[BigDecimal],
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

object SeExpenses {
  implicit val reads: Reads[SeExpenses] = Json.reads[SeExpenses]
  implicit val writes: OWrites[SeExpenses] = (
    (JsPath \ "costOfGoodsAllowable").writeNullable[BigDecimal] and
      (JsPath \ "cisPaymentsToSubcontractorsAllowable").writeNullable[BigDecimal] and
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
    )(unlift(SeExpenses.unapply))
}