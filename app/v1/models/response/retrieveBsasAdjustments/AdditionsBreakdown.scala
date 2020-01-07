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

case class AdditionsBreakdown(costOfGoodsBoughtDisallowable : Option[BigDecimal],
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
                              otherDisallowable: Option[BigDecimal])


object AdditionsBreakdown {

  implicit val reads: Reads[AdditionsBreakdown] = (
    (JsPath \ "adjustments" \ "additions" \ "costOfGoodsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "paymentsToSubContractorsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "wagesAndStaffCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "carVanTravelExpensesDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "premisesRunningCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "maintenanceCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "adminCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "advertisingCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "businessEntertainmentCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "interestOnBankOtherLoansDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "financeChargesDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "irrecoverableDebtsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "professionalFeesDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "depreciationDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "otherExpensesDisallowable").readNullable[BigDecimal]
    )(AdditionsBreakdown.apply _)

  implicit val writes: OWrites[AdditionsBreakdown] = Json.writes[AdditionsBreakdown]

}