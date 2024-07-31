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

package v6.bsas.list.def2.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import shared.models.domain.Status

case class Def2_BsasSummary(
    calculationId: String,
    requestedDateTime: String,
    summaryStatus: Status,
    adjustedSummary: Boolean,
    adjustedDateTime: Option[String]
)

object Def2_BsasSummary {

  implicit val reads: Reads[Def2_BsasSummary] = (
    (JsPath \ "calculationId").read[String] and
      (JsPath \ "requestedDateTime").read[String] and
      (JsPath \ "status").read[Status] and
      (JsPath \ "adjusted").read[Boolean] and
      (JsPath \ "adjustedDateTime").readNullable[String]
  )(Def2_BsasSummary.apply _)

  implicit val writes: OWrites[Def2_BsasSummary] = Json.writes[Def2_BsasSummary]
}
