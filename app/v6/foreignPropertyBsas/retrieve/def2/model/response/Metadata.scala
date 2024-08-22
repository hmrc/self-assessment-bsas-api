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

package v6.foreignPropertyBsas.retrieve.def2.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import shared.models.domain.TaxYear

case class Metadata(calculationId: String,
                    requestedDateTime: String,
                    adjustedDateTime: Option[String],
                    nino: String,
                    taxYear: String,
                    summaryStatus: String)

object Metadata {

  implicit val reads: Reads[Metadata] = ((JsPath \ "calculationId").read[String] and
    (JsPath \ "requestedDateTime").read[String] and
    (JsPath \ "adjustedDateTime").readNullable[String] and
    (JsPath \ "taxableEntityId").read[String] and
    (JsPath \ "taxYear").read[Int].map(TaxYear.fromDownstreamInt(_).asMtd) and
    (JsPath \ "status").read[String])(Metadata.apply _)

  implicit val writes: OWrites[Metadata] = Json.writes[Metadata]
}
