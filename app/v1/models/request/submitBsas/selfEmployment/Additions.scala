/*
 * Copyright 2021 HM Revenue & Customs
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

case class Additions(costOfGoodsBoughtDisallowable: Option[BigDecimal],
                     cisPaymentsToSubcontractorsDisallowable: Option[BigDecimal],
                     staffCostsDisallowable: Option[BigDecimal],
                     travelCostsDisallowable: Option[BigDecimal],
                     premisesRunningCostsDisallowable: Option[BigDecimal],
                     maintenanceCostsDisallowable: Option[BigDecimal],
                     adminCostsDisallowable: Option[BigDecimal],
                     advertisingCostsDisallowable: Option[BigDecimal],
                     businessEntertainmentCostsDisallowable: Option[BigDecimal],
                     interestDisallowable: Option[BigDecimal],
                     financialChargesDisallowable: Option[BigDecimal],
                     badDebtDisallowable: Option[BigDecimal],
                     professionalFeesDisallowable: Option[BigDecimal],
                     depreciationDisallowable: Option[BigDecimal],
                     otherDisallowable: Option[BigDecimal]){

  def isEmpty: Boolean =  costOfGoodsBoughtDisallowable.isEmpty &&
                          cisPaymentsToSubcontractorsDisallowable.isEmpty &&
                          staffCostsDisallowable.isEmpty &&
                          travelCostsDisallowable.isEmpty &&
                          premisesRunningCostsDisallowable.isEmpty &&
                          maintenanceCostsDisallowable.isEmpty &&
                          adminCostsDisallowable.isEmpty &&
                          advertisingCostsDisallowable.isEmpty &&
                          businessEntertainmentCostsDisallowable.isEmpty &&
                          interestDisallowable.isEmpty &&
                          financialChargesDisallowable.isEmpty &&
                          badDebtDisallowable.isEmpty &&
                          professionalFeesDisallowable.isEmpty &&
                          depreciationDisallowable.isEmpty &&
                          otherDisallowable.isEmpty
}

object Additions{
 implicit val reads: Reads[Additions] = Json.reads[Additions]
 implicit val writes: OWrites[Additions] = (
   (JsPath \ "costOfGoodsDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "paymentsToSubcontractorsDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "wagesAndStaffCostsDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "carVanTravelExpensesDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "premisesRunningCostsDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "maintenanceCostsDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "adminCostsDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "advertisingCostsDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "businessEntertainmentCostsDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "interestOnBankOtherLoansDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "financeChargesDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "irrecoverableDebtsDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "professionalFeesDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "depreciationDisallowable").writeNullable[BigDecimal] and
     (JsPath \ "otherExpensesDisallowable").writeNullable[BigDecimal]
   )(unlift(Additions.unapply))
}
