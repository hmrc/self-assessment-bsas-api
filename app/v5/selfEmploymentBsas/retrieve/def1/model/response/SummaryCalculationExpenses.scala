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

package v5.selfEmploymentBsas.retrieve.def1.model.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class SummaryCalculationExpenses(
    consolidatedExpenses: Option[BigDecimal],
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
    businessEntertainmentCostsAllowable: Option[BigDecimal]
)

object SummaryCalculationExpenses {

  implicit val reads: Reads[SummaryCalculationExpenses] = (
    (JsPath \ "consolidatedExpenses").readNullable[BigDecimal] and
      (JsPath \ "costOfGoodsAllowable").readNullable[BigDecimal] and
      (JsPath \ "paymentsToSubcontractorsAllowable").readNullable[BigDecimal] and
      (JsPath \ "wagesAndStaffCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "carVanTravelExpensesAllowable").readNullable[BigDecimal] and
      (JsPath \ "premisesRunningCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "maintenanceCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "adminCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "interestOnBankOtherLoansAllowable").readNullable[BigDecimal] and
      (JsPath \ "financeChargesAllowable").readNullable[BigDecimal] and
      (JsPath \ "irrecoverableDebtsAllowable").readNullable[BigDecimal] and
      (JsPath \ "professionalFeesAllowable").readNullable[BigDecimal] and
      (JsPath \ "depreciationAllowable").readNullable[BigDecimal] and
      (JsPath \ "otherExpensesAllowable").readNullable[BigDecimal] and
      (JsPath \ "advertisingCostsAllowable").readNullable[BigDecimal] and
      (JsPath \ "businessEntertainmentCostsAllowable").readNullable[BigDecimal]
  )(SummaryCalculationExpenses.apply _)

  implicit val writes: OWrites[SummaryCalculationExpenses] = Json.writes[SummaryCalculationExpenses]
}
