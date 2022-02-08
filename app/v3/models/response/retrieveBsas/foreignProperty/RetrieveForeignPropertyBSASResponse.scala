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

package v3.models.response.retrieveBsas.foreignProperty

import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import play.api.libs.functional.syntax._

case class RetrieveForeignPropertyBSASResponse(metadata: Metadata,
                                               inputs: Inputs,
                                               adjustableSummaryCalculation: AdjustableSummaryCalculation,
                                               adjustments: Option[Adjustments],
                                               adjustedSummaryCalculation: AdjustableSummaryCalculation)

object RetrieveForeignPropertyBSASResponse {
  implicit val reads: Reads[RetrieveForeignPropertyBSASResponse] = (
    (JsPath \ "metadata").read[Metadata] and
      (JsPath \ "inputs").read[Inputs] and
      (JsPath \ "adjustableSummaryCalculation").read[AdjustableSummaryCalculation] and
      (JsPath \ "adjustments").readNullable[Adjustments] and
      (JsPath \ "adjustedSummaryCalculation").read[AdjustableSummaryCalculation]
    ) (RetrieveForeignPropertyBSASResponse.apply _)

  implicit val writes: OWrites[RetrieveForeignPropertyBSASResponse] = Json.writes[RetrieveForeignPropertyBSASResponse]
}