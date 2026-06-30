/*
 * Copyright 2026 HM Revenue & Customs
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

package v7.ukPropertyBsas.retrieve.def3.model.response

import api.models.domain.TaxYear
import play.api.libs.json.*
import v7.ukPropertyBsas.retrieve.model.response.RetrieveUkPropertyBsasResponse

case class Def3_RetrieveUkPropertyBsasResponse(
    metadata: Metadata,
    inputs: Inputs,
    adjustableSummaryCalculation: AdjustableSummaryCalculation,
    adjustments: Option[Adjustments],
    adjustedSummaryCalculation: Option[AdjustedSummaryCalculation]
) extends RetrieveUkPropertyBsasResponse {
  override def incomeSourceType: String = inputs.incomeSourceType
  override def taxYear: TaxYear         = TaxYear.fromMtd(metadata.taxYear)
}

object Def3_RetrieveUkPropertyBsasResponse {

  implicit val reads: Reads[Def3_RetrieveUkPropertyBsasResponse] = (json: JsValue) =>
    for {
      metadata <- (json \ "metadata").validate[Metadata]
      inputs   <- (json \ "inputs").validate[Inputs]
      adjustableSummaryCalculation <- (json \ "adjustableSummaryCalculation")
        .validate[AdjustableSummaryCalculation](AdjustableSummaryCalculation.reads)
      adjustments <- (json \ "adjustments").validateOpt[Adjustments](Adjustments.reads)
      adjustedSummaryCalculation <- (json \ "adjustedSummaryCalculation")
        .validateOpt[AdjustedSummaryCalculation](AdjustedSummaryCalculation.reads)
    } yield {
      Def3_RetrieveUkPropertyBsasResponse(
        metadata = metadata,
        inputs = inputs,
        adjustableSummaryCalculation = adjustableSummaryCalculation,
        adjustments = adjustments,
        adjustedSummaryCalculation = adjustedSummaryCalculation
      )
    }

  implicit val writes: OWrites[Def3_RetrieveUkPropertyBsasResponse] = Json.writes
}
