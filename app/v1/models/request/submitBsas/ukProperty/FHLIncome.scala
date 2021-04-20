/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.models.request.submitBsas.ukProperty

import play.api.libs.json._

case class FHLIncome(rentIncome: Option[BigDecimal]) {

  val params: Map[String, BigDecimal] = Map(
    "rentReceived" -> rentIncome
  ).collect {case (k, Some(v)) => (k, v) }
}

object FHLIncome {
  implicit val reads: Reads[FHLIncome] = Json.reads[FHLIncome]
  implicit val writes: Writes[FHLIncome] = (o: FHLIncome) => Json.toJsObject(o.params)
}
