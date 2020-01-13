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

package v1.models.request.submitBsas

import play.api.libs.json._

case class FHLIncome(rentIncome: Option[BigDecimal]) {

  val params: Map[String, Option[BigDecimal]] = Map(
    "totalRentsReceived" -> rentIncome
  ).filterNot {case (_, v) => v.isEmpty }

  def queryMap[A](as: Map[String, A]): Map[String, BigDecimal] = as.map {
    case (k: String, Some(v: BigDecimal)) => (k, v)
  }

  val mappedPresentParams: Map[String, BigDecimal] = queryMap(params)
}

object FHLIncome {
  implicit val reads: Reads[FHLIncome] = Json.reads[FHLIncome]
  implicit val writes: Writes[FHLIncome] = new OWrites[FHLIncome] {
    override def writes(o: FHLIncome): JsObject = Json.toJsObject(o.mappedPresentParams)
  }
}
