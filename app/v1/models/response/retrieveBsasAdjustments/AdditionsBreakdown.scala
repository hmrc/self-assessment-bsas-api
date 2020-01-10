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


object AdditionsBreakdown extends NestedJsonReads {

  val empty = AdditionsBreakdown(None, None, None, None, None, None, None, None, None, None, None, None, None, None, None)

  implicit val reads: Reads[AdditionsBreakdown] = (
    (JsPath \ "adjustments" \ "additions" \ "costOfGoodsDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "paymentsToSubContractorsDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "wagesAndStaffCostsDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "carVanTravelExpensesDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "premisesRunningCostsDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "maintenanceCostsDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "adminCostsDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "advertisingCostsDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "businessEntertainmentCostsDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "interestOnBankOtherLoansDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "financeChargesDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "irrecoverableDebtsDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "professionalFeesDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "depreciationDisallowable").readNestedNullable[BigDecimal] and
      (JsPath \ "adjustments" \ "additions" \ "otherExpensesDisallowable").readNestedNullable[BigDecimal]
    )(AdditionsBreakdown.apply _)

  implicit val writes: OWrites[AdditionsBreakdown] = Json.writes[AdditionsBreakdown]

}
