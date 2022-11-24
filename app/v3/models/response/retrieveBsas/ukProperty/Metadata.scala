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

package v3.models.response.retrieveBsas.ukProperty

import play.api.libs.functional.syntax._
import play.api.libs.json._
import v3.models.domain.{DownstreamTaxYear, Status}

case class Metadata(
    calculationId: String,
    requestedDateTime: String,
    adjustedDateTime: Option[String],
    nino: String,
    taxYear: String,
    summaryStatus: Status
)

object Metadata {
  implicit val reads: Reads[Metadata] = (
    (JsPath \ "calculationId").read[String] and
      (JsPath \ "requestedDateTime").read[String] and
      (JsPath \ "adjustedDateTime").readNullable[String] and
      (JsPath \ "taxableEntityId").read[String] and
      (JsPath \ "taxYear").read[BigInt].map(taxYear => DownstreamTaxYear.fromDownstream(taxYear.toString())) and
      (JsPath \ "status").read[Status]
  )(Metadata.apply _)

  implicit val writes: OWrites[Metadata] = Json.writes[Metadata]
}
