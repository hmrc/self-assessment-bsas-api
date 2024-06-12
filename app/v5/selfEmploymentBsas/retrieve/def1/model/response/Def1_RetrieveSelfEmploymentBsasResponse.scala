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

package v5.selfEmploymentBsas.retrieve.def1.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json._
import v5.common.model.TypeOfBusiness
import v5.selfEmploymentBsas.retrieve.model.response.RetrieveSelfEmploymentBsasResponse

case class Def1_RetrieveSelfEmploymentBsasResponse(
    metadata: Metadata,
    inputs: Inputs,
    adjustableSummaryCalculation: AdjustableSummaryCalculation,
    adjustments: Option[Adjustments],
    adjustedSummaryCalculation: Option[AdjustedSummaryCalculation]
) extends RetrieveSelfEmploymentBsasResponse {

  override def typeOfBusiness: TypeOfBusiness = inputs.typeOfBusiness

}

object Def1_RetrieveSelfEmploymentBsasResponse {

  implicit val reads: Reads[Def1_RetrieveSelfEmploymentBsasResponse] = (
    (JsPath \ "metadata").read[Metadata] and
      (JsPath \ "inputs").read[Inputs] and
      (JsPath \ "adjustableSummaryCalculation").read[AdjustableSummaryCalculation] and
      (JsPath \ "adjustments").readNullable[Adjustments] and
      (JsPath \ "adjustedSummaryCalculation").readNullable[AdjustedSummaryCalculation]
  )(Def1_RetrieveSelfEmploymentBsasResponse.apply _)

  implicit val writes: OWrites[Def1_RetrieveSelfEmploymentBsasResponse] = Json.writes
}
