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

package v6.foreignPropertyBsas.retrieve.def1.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class AdjustmentsIncome(totalRentsReceived: Option[BigDecimal],
                             premiumsOfLeaseGrant: Option[BigDecimal],
                             otherPropertyIncome: Option[BigDecimal])

object AdjustmentsIncome {

  val readsFhl: Reads[AdjustmentsIncome] = (
    (JsPath \ "rent").readNullable[BigDecimal] and
      Reads.pure(None) and
      Reads.pure(None)
  )(AdjustmentsIncome.apply _)

  val readsNonFhl: Reads[AdjustmentsIncome] = (
    (JsPath \ "rentReceived").readNullable[BigDecimal] and
      (JsPath \ "premiumsOfLeaseGrant").readNullable[BigDecimal] and
      (JsPath \ "otherPropertyIncome").readNullable[BigDecimal]
  )(AdjustmentsIncome.apply _)

  implicit val writes: OWrites[AdjustmentsIncome] = Json.writes[AdjustmentsIncome]
}
