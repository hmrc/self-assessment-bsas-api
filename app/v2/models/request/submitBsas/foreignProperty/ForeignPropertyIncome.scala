/*
 * Copyright 2022 HM Revenue & Customs
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

package v2.models.request.submitBsas.foreignProperty

import play.api.libs.json.{JsObject, Json, OWrites, Reads}

case class ForeignPropertyIncome(rentIncome: Option[BigDecimal],
                                 premiumsOfLeaseGrant: Option[BigDecimal],
                                 otherPropertyIncome: Option[BigDecimal]) {

  def isEmpty: Boolean =
    ForeignPropertyIncome.unapply(this).forall {
      case (None, None, None) => true
      case _                  => false
    }
}

object ForeignPropertyIncome {
  implicit val reads: Reads[ForeignPropertyIncome] = Json.reads[ForeignPropertyIncome]
  implicit val writes: OWrites[ForeignPropertyIncome] = new OWrites[ForeignPropertyIncome] {
    override def writes(o: ForeignPropertyIncome): JsObject =
      if (o.isEmpty) JsObject.empty
      else Json.obj(
        "rent" -> o.rentIncome,
        "premiumsOfLeaseGrant" -> o.premiumsOfLeaseGrant,
        "otherPropertyIncome" -> o.otherPropertyIncome)
  }
}
