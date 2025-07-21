/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.ukPropertyBsas.retrieve.def2.model.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class SummaryCalculationIncome(
    totalRentsReceived: Option[BigDecimal],
    premiumsOfLeaseGrant: Option[BigDecimal],
    reversePremiums: Option[BigDecimal],
    otherPropertyIncome: Option[BigDecimal],
    rarRentReceived: Option[BigDecimal]
)

object SummaryCalculationIncome {

  val reads: Reads[SummaryCalculationIncome] = (
    (JsPath \ "totalRentsReceived").readNullable[BigDecimal] and
      (JsPath \ "premiumsOfLeaseGrant").readNullable[BigDecimal] and
      (JsPath \ "reversePremiums").readNullable[BigDecimal] and
      (JsPath \ "otherPropertyIncome").readNullable[BigDecimal] and
      (JsPath \ "rarRentReceived").readNullable[BigDecimal]
  )(SummaryCalculationIncome.apply)

  implicit val writes: OWrites[SummaryCalculationIncome] = Json.writes[SummaryCalculationIncome]
}
