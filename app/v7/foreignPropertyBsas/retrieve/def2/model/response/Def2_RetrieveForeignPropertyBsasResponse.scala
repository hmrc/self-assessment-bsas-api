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

package v7.foreignPropertyBsas.retrieve.def2.model.response

import play.api.libs.json.*
import shared.models.domain.TaxYear
import v7.foreignPropertyBsas.retrieve.model.response.RetrieveForeignPropertyBsasResponse

case class Def2_RetrieveForeignPropertyBsasResponse(
    metadata: Metadata,
    inputs: Inputs,
    adjustableSummaryCalculation: SummaryCalculation,
    adjustments: Option[Adjustments],
    adjustedSummaryCalculation: Option[SummaryCalculation]
) extends RetrieveForeignPropertyBsasResponse {

  override def incomeSourceType: String = inputs.incomeSourceType
  override def taxYear: TaxYear         = TaxYear.fromMtd(metadata.taxYear)
}

object Def2_RetrieveForeignPropertyBsasResponse {

  implicit val reads: Reads[Def2_RetrieveForeignPropertyBsasResponse] = (json: JsValue) =>
    for {
      metadata <- (json \ "metadata").validate[Metadata]
      inputs   <- (json \ "inputs").validate[Inputs]

      adjustableSummaryCalculation <- (json \ "adjustableSummaryCalculation").validate[SummaryCalculation]

      adjustments <- adjustmentsReads(json)

      adjustedSummaryCalculation <- (json \ "adjustedSummaryCalculation").validateOpt[SummaryCalculation]

    } yield Def2_RetrieveForeignPropertyBsasResponse(
      metadata = metadata,
      inputs = inputs,
      adjustableSummaryCalculation = adjustableSummaryCalculation,
      adjustments = adjustments,
      adjustedSummaryCalculation = adjustedSummaryCalculation
    )

  private def adjustmentsReads(json: JsValue): JsResult[Option[Adjustments]] =
    (json \ "adjustments")
      .validateOpt[Seq[Adjustments]]
      .map(s => Some(Adjustments(s, None, None, None, None)))
      .orElse((json \ "adjustments").validateOpt[Adjustments](Adjustments.readsZeroAdjustments))
      .orElse(JsSuccess(None)) // Not an array, e.g. typeOfBusiness is self-employment

  implicit val writes: OWrites[Def2_RetrieveForeignPropertyBsasResponse] = Json.writes[Def2_RetrieveForeignPropertyBsasResponse]
}
