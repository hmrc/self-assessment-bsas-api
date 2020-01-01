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

package v1.models.response.listBsas

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v1.models.domain.Status

case class BsasEntries(bsasId: String, requestedDateTime: String, summaryStatus: Status, adjustedSummary: Boolean)

object BsasEntries {

  implicit val reads: Reads[BsasEntries] = (
    (JsPath \ "calculationId").read[String] and
      (JsPath \ "requestedDateTime").read[String] and
      (JsPath \ "status").read[Status] and
      (JsPath \ "adjusted").read[Boolean]
  )(BsasEntries.apply _)

  implicit val writes: OWrites[BsasEntries] = Json.writes[BsasEntries]
}
