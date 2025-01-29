/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.ukPropertyBsas.retrieve.def1.model.response

import play.api.libs.json._
import shared.models.domain.TaxYear
import v7.common.model.TypeOfBusinessWithFHL
import v7.ukPropertyBsas.retrieve.model.response.RetrieveUkPropertyBsasResponse

case class Def1_RetrieveUkPropertyBsasResponse(
    metadata: Metadata,
    inputs: Inputs,
    adjustableSummaryCalculation: AdjustableSummaryCalculation,
    adjustments: Option[Adjustments],
    adjustedSummaryCalculation: Option[AdjustedSummaryCalculation]
) extends RetrieveUkPropertyBsasResponse {

  override def incomeSourceType: String = inputs.incomeSourceType
  override def taxYear: TaxYear         = TaxYear.fromMtd(metadata.taxYear)
}

object Def1_RetrieveUkPropertyBsasResponse {

  implicit val reads: Reads[Def1_RetrieveUkPropertyBsasResponse] = (json: JsValue) =>
    for {
      metadata <- (json \ "metadata").validate[Metadata]
      inputs   <- (json \ "inputs").validate[Inputs]
      isFhl = inputs.incomeSourceType == TypeOfBusinessWithFHL.`uk-property-fhl`.asDownstreamValue
      adjustableSummaryCalculation <- (json \ "adjustableSummaryCalculation")
        .validate[AdjustableSummaryCalculation](if (isFhl) AdjustableSummaryCalculation.readsFhl else AdjustableSummaryCalculation.readsUkProperty)
      adjustments <- (json \ "adjustments").validateOpt[Adjustments](if (isFhl) Adjustments.readsFhl else Adjustments.readsUkProperty)
      adjustedSummaryCalculation <- (json \ "adjustedSummaryCalculation")
        .validateOpt[AdjustedSummaryCalculation](if (isFhl) AdjustedSummaryCalculation.readsFhl else AdjustedSummaryCalculation.readsUkProperty)
    } yield {
      Def1_RetrieveUkPropertyBsasResponse(
        metadata = metadata,
        inputs = inputs,
        adjustableSummaryCalculation = adjustableSummaryCalculation,
        adjustments = adjustments,
        adjustedSummaryCalculation = adjustedSummaryCalculation
      )
    }

  implicit val writes: OWrites[Def1_RetrieveUkPropertyBsasResponse] = Json.writes
}
