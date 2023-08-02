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

package v3.models.response.retrieveBsas.foreignProperty

import api.hateoas.HateoasLinksFactory
import api.hateoas.{ HateoasData, Link }
import api.models.domain.TaxYear
import config.AppConfig
import play.api.libs.json._
import v3.models.domain.{HasTypeOfBusiness, TypeOfBusiness}
import v3.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse._

case class RetrieveForeignPropertyBsasResponse(metadata: Metadata,
                                               inputs: Inputs,
                                               adjustableSummaryCalculation: SummaryCalculation,
                                               adjustments: Option[Adjustments],
                                               adjustedSummaryCalculation: Option[SummaryCalculation])
  extends HasTypeOfBusiness {
  override def typeOfBusiness: TypeOfBusiness = inputs.typeOfBusiness
}

object RetrieveForeignPropertyBsasResponse {
  implicit val reads: Reads[RetrieveForeignPropertyBsasResponse] = (json: JsValue) =>
    for {
      metadata <- (json \ "metadata").validate[Metadata]
      inputs <- (json \ "inputs").validate[Inputs]

      isFhl = inputs.typeOfBusiness == TypeOfBusiness.`foreign-property-fhl-eea`

      adjustableSummaryCalculation <- (json \ "adjustableSummaryCalculation").validate[SummaryCalculation](
        if (isFhl) SummaryCalculation.readsFhl else SummaryCalculation.readsNonFhl
      )

      adjustments <- if (isFhl) fhlEeaAdjustmentsReads(json) else nonFhlAdjustmentsReads(json)

      adjustedSummaryCalculation <- (json \ "adjustedSummaryCalculation").validateOpt[SummaryCalculation](
        if (isFhl) SummaryCalculation.readsFhl else SummaryCalculation.readsNonFhl
      )
    } yield
      RetrieveForeignPropertyBsasResponse(
        metadata = metadata,
        inputs = inputs,
        adjustableSummaryCalculation = adjustableSummaryCalculation,
        adjustments = adjustments,
        adjustedSummaryCalculation = adjustedSummaryCalculation
      )

  private def fhlEeaAdjustmentsReads(json: JsValue): JsResult[Option[Adjustments]] =
    (json \ "adjustments").validateOpt[Adjustments](Adjustments.readsFhl)

  private def nonFhlAdjustmentsReads(json: JsValue): JsResult[Option[Adjustments]] =
    (json \ "adjustments")
      .validateOpt[Seq[Adjustments]](Adjustments.readsNonFhlSeq)
      .map(s => Some(Adjustments(s, None, None, None)))
      .orElse(JsSuccess(None)) // Not an array, e.g. typeOfBusiness is self-employment

  implicit val writes: OWrites[RetrieveForeignPropertyBsasResponse] = Json.writes[RetrieveForeignPropertyBsasResponse]

  implicit object RetrieveSelfAssessmentBsasHateoasFactory
    extends HateoasLinksFactory[RetrieveForeignPropertyBsasResponse, RetrieveForeignPropertyHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveForeignPropertyHateoasData): Seq[Link] = {
      import data._

      Seq(
        getForeignPropertyBsas(appConfig, nino, calculationId, taxYear),
        adjustForeignPropertyBsas(appConfig, nino, calculationId, taxYear)
      )
    }
  }
}

case class RetrieveForeignPropertyHateoasData(nino: String, calculationId: String, taxYear: Option[TaxYear]) extends HateoasData
