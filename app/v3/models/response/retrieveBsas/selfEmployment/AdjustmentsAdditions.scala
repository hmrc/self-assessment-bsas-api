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

package v3.models.response.retrieveBsas.selfEmployment

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class AdjustmentsAdditions(
    costOfGoodsDisallowable: Option[BigDecimal],
    paymentsToSubcontractorsDisallowable: Option[BigDecimal],
    wagesAndStaffCostsDisallowable: Option[BigDecimal],
    carVanTravelExpensesDisallowable: Option[BigDecimal],
    premisesRunningCostsDisallowable: Option[BigDecimal],
    maintenanceCostsDisallowable: Option[BigDecimal],
    adminCostsDisallowable: Option[BigDecimal],
    interestOnBankOtherLoansDisallowable: Option[BigDecimal],
    financeChargesDisallowable: Option[BigDecimal],
    irrecoverableDebtsDisallowable: Option[BigDecimal],
    professionalFeesDisallowable: Option[BigDecimal],
    depreciationDisallowable: Option[BigDecimal],
    otherExpensesDisallowable: Option[BigDecimal],
    advertisingCostsDisallowable: Option[BigDecimal],
    businessEntertainmentCostsDisallowable: Option[BigDecimal],
)

object AdjustmentsAdditions {
  implicit val reads: Reads[AdjustmentsAdditions] = (
    (JsPath \ "costOfGoodsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "paymentsToSubcontractorsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "wagesAndStaffCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "carVanTravelExpensesDisallowable").readNullable[BigDecimal] and
      (JsPath \ "premisesRunningCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "maintenanceCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adminCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "interestOnBankOtherLoansDisallowable").readNullable[BigDecimal] and
      (JsPath \ "financeChargesDisallowable").readNullable[BigDecimal] and
      (JsPath \ "irrecoverableDebtsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "professionalFeesDisallowable").readNullable[BigDecimal] and
      (JsPath \ "depreciationDisallowable").readNullable[BigDecimal] and
      (JsPath \ "otherExpensesDisallowable").readNullable[BigDecimal] and
      (JsPath \ "advertisingCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "businessEntertainmentCostsDisallowable").readNullable[BigDecimal]
  )(AdjustmentsAdditions.apply _)

  implicit val writes: OWrites[AdjustmentsAdditions] = Json.writes[AdjustmentsAdditions]
}
