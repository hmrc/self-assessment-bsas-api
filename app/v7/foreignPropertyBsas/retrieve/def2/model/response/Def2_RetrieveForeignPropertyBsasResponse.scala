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

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import shared.models.domain.TaxYear
import v7.foreignPropertyBsas.retrieve.model.response.RetrieveForeignPropertyBsasResponse

case class Def2_RetrieveForeignPropertyBsasResponse(
    metadata: Metadata,
    inputs: Inputs,
    adjustableSummaryCalculation: SummaryCalculation,
    adjustments: Option[Seq[Adjustments]],
    adjustedSummaryCalculation: Option[SummaryCalculation]
) extends RetrieveForeignPropertyBsasResponse {

  override def incomeSourceType: String = inputs.incomeSourceType
  override def taxYear: TaxYear         = TaxYear.fromMtd(metadata.taxYear)
}

object Def2_RetrieveForeignPropertyBsasResponse {

  implicit val reads: Reads[Def2_RetrieveForeignPropertyBsasResponse] = (
    (JsPath \ "metadata").read[Metadata] and
      (JsPath \ "inputs").read[Inputs] and
      (JsPath \ "adjustableSummaryCalculation").read[SummaryCalculation] and
      // Handles cases where "adjustments" is not always an array, e.g., typeOfBusiness is self-employment
      (JsPath \ "adjustments").readNullable[Seq[Adjustments]].orElse(Reads.pure(None)) and
      (JsPath \ "adjustedSummaryCalculation").readNullable[SummaryCalculation]
  )(Def2_RetrieveForeignPropertyBsasResponse.apply _)

  implicit val writes: OWrites[Def2_RetrieveForeignPropertyBsasResponse] = Json.writes[Def2_RetrieveForeignPropertyBsasResponse]
}
