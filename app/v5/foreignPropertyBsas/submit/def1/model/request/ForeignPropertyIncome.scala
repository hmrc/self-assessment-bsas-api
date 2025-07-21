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

package v5.foreignPropertyBsas.submit.def1.model.request

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class ForeignPropertyIncome(totalRentsReceived: Option[BigDecimal],
                                 premiumsOfLeaseGrant: Option[BigDecimal],
                                 otherPropertyIncome: Option[BigDecimal])

object ForeignPropertyIncome {
  implicit val reads: Reads[ForeignPropertyIncome] = Json.reads[ForeignPropertyIncome]

  implicit val writes: OWrites[ForeignPropertyIncome] = (
    (JsPath \ "rent").writeNullable[BigDecimal] and
      (JsPath \ "premiumsOfLeaseGrant").writeNullable[BigDecimal] and
      (JsPath \ "otherPropertyIncome").writeNullable[BigDecimal]
  )(w => Tuple.fromProductTyped(w))

}
