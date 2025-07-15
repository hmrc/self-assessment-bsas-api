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

package v7.selfEmploymentBsas.retrieve.def2.model.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class AdjustmentsIncome(
    turnover: Option[BigDecimal],
    other: Option[BigDecimal]
)

object AdjustmentsIncome {

  implicit val reads: Reads[AdjustmentsIncome] = (
    (JsPath \ "turnover").readNullable[BigDecimal] and
      (JsPath \ "other").readNullable[BigDecimal]
  )(AdjustmentsIncome.apply _)

  implicit val writes: OWrites[AdjustmentsIncome] = Json.writes[AdjustmentsIncome]
}
