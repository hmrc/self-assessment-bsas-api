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

package v3.models.request.submitBsas.selfEmployment

import play.api.libs.json.{Json, OFormat}

case class Additions(costOfGoodsDisallowable: Option[BigDecimal],
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
                     businessEntertainmentCostsDisallowable: Option[BigDecimal]) {

  val nonEmpty: Boolean = this match {
    case Additions(None, None, None, None, None, None, None, None, None, None, None, None, None, None, None) => false
    case _ => true
  }
}

object Additions {
  implicit val format: OFormat[Additions] = Json.format[Additions]
}
