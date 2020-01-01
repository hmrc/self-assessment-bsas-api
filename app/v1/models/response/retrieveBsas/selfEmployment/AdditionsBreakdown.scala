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

case class AdditionsBreakdown(costOfGoodsBoughtDisallowable: Option[BigDecimal],
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
  implicit val writes: OWrites[AdditionsBreakdown] = Json.writes[AdditionsBreakdown]

  implicit val reads: Reads[AdditionsBreakdown] = (
    (JsPath \ "costOfGoodsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "paymentsToSubContractorsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "wagesAndStaffCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "carVanTravelExpensesDisallowable").readNullable[BigDecimal] and
      (JsPath \ "premisesRunningCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "maintenanceCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "adminCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "advertisingCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "businessEntertainmentCostsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "interestOnBankOtherLoansDisallowable").readNullable[BigDecimal] and
      (JsPath \ "financeChargesDisallowable").readNullable[BigDecimal] and
      (JsPath \ "irrecoverableDebtsDisallowable").readNullable[BigDecimal] and
      (JsPath \ "professionalFeesDisallowable").readNullable[BigDecimal] and
      (JsPath \ "depreciationDisallowable").readNullable[BigDecimal] and
      (JsPath \ "otherExpensesDisallowable").readNullable[BigDecimal]
  )(AdditionsBreakdown.apply _)
}
