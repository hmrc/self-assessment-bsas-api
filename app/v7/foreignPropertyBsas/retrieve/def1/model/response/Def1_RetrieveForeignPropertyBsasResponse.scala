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

package v7.foreignPropertyBsas.retrieve.def1.model.response

import play.api.libs.json.*
import shared.models.domain.TaxYear
import v7.common.model.TypeOfBusinessWithFHL
import v7.foreignPropertyBsas.retrieve.model.response.RetrieveForeignPropertyBsasResponse

case class Def1_RetrieveForeignPropertyBsasResponse(
    metadata: Metadata,
    inputs: Inputs,
    adjustableSummaryCalculation: SummaryCalculation,
    adjustments: Option[Adjustments],
    adjustedSummaryCalculation: Option[SummaryCalculation]
) extends RetrieveForeignPropertyBsasResponse {

  override def incomeSourceType: String = inputs.incomeSourceType
  override def taxYear: TaxYear         = TaxYear.fromMtd(metadata.taxYear)
}

object Def1_RetrieveForeignPropertyBsasResponse {

  implicit val reads: Reads[Def1_RetrieveForeignPropertyBsasResponse] = (json: JsValue) =>
    for {
      metadata <- (json \ "metadata").validate[Metadata]
      inputs   <- (json \ "inputs").validate[Inputs]

      isFhl = inputs.typeOfBusiness == TypeOfBusinessWithFHL.`foreign-property-fhl-eea`

      adjustableSummaryCalculation <- (json \ "adjustableSummaryCalculation").validate[SummaryCalculation](
        if (isFhl) SummaryCalculation.readsFhl else SummaryCalculation.reads
      )

      adjustments <- if (isFhl) adjustmentsReadsFhlEea(json) else adjustmentsReadsNonFhl(json)

      adjustedSummaryCalculation <- (json \ "adjustedSummaryCalculation").validateOpt[SummaryCalculation](
        if (isFhl) SummaryCalculation.readsFhl else SummaryCalculation.reads
      )
    } yield Def1_RetrieveForeignPropertyBsasResponse(
      metadata = metadata,
      inputs = inputs,
      adjustableSummaryCalculation = adjustableSummaryCalculation,
      adjustments = adjustments,
      adjustedSummaryCalculation = adjustedSummaryCalculation
    )

  private def adjustmentsReadsFhlEea(json: JsValue): JsResult[Option[Adjustments]] = {
    (json \ "adjustments")
      .validateOpt[Adjustments](Adjustments.readsFhl)
      .filter(_.exists(adj => adj.income.isDefined || adj.expenses.isDefined))
      .orElse((json \ "adjustments").validateOpt[Adjustments](Adjustments.readsZeroAdjustments))
  }

  private def adjustmentsReadsNonFhl(json: JsValue): JsResult[Option[Adjustments]] =
    (json \ "adjustments")
      .validateOpt[Seq[Adjustments]](Adjustments.readsSeq)
      .map(s => Some(Adjustments(s, None, None, None, None)))
      .orElse((json \ "adjustments").validateOpt[Adjustments](Adjustments.readsZeroAdjustments))
      .orElse(JsSuccess(None)) // Not an array, e.g. typeOfBusiness is self-employment

  implicit val writes: OWrites[Def1_RetrieveForeignPropertyBsasResponse] = Json.writes[Def1_RetrieveForeignPropertyBsasResponse]

}
