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

package v3.models.response.retrieveBsas.ukProperty

import config.AppConfig
import play.api.libs.json._
import v3.hateoas.{ HateoasLinks, HateoasLinksFactory }
import v3.models.domain.{ HasTypeOfBusiness, TaxYear, TypeOfBusiness }
import v3.models.hateoas.{ HateoasData, Link }

case class RetrieveUkPropertyBsasResponse(
    metadata: Metadata,
    inputs: Inputs,
    adjustableSummaryCalculation: AdjustableSummaryCalculation,
    adjustments: Option[Adjustments],
    adjustedSummaryCalculation: Option[AdjustedSummaryCalculation]
) extends HasTypeOfBusiness {
  override def typeOfBusiness: TypeOfBusiness = inputs.typeOfBusiness
}

object RetrieveUkPropertyBsasResponse extends HateoasLinks {

  implicit val reads: Reads[RetrieveUkPropertyBsasResponse] = (json: JsValue) =>
    for {
      metadata <- (json \ "metadata").validate[Metadata]
      inputs   <- (json \ "inputs").validate[Inputs]
      isFhl = inputs.typeOfBusiness == TypeOfBusiness.`uk-property-fhl`
      adjustableSummaryCalculation <- (json \ "adjustableSummaryCalculation")
        .validate[AdjustableSummaryCalculation](if (isFhl) AdjustableSummaryCalculation.readsFhl else AdjustableSummaryCalculation.readsNonFhl)
      adjustments <- (json \ "adjustments").validateOpt[Adjustments](if (isFhl) Adjustments.readsFhl else Adjustments.readsNonFhl)
      adjustedSummaryCalculation <- (json \ "adjustedSummaryCalculation")
        .validateOpt[AdjustedSummaryCalculation](if (isFhl) AdjustedSummaryCalculation.readsFhl else AdjustedSummaryCalculation.readsNonFhl)
    } yield {
      RetrieveUkPropertyBsasResponse(
        metadata = metadata,
        inputs = inputs,
        adjustableSummaryCalculation = adjustableSummaryCalculation,
        adjustments = adjustments,
        adjustedSummaryCalculation = adjustedSummaryCalculation
      )
  }

  implicit val writes: OWrites[RetrieveUkPropertyBsasResponse] = Json.writes[RetrieveUkPropertyBsasResponse]

  implicit object RetrieveSelfAssessmentBsasHateoasFactory
      extends HateoasLinksFactory[RetrieveUkPropertyBsasResponse, RetrieveUkPropertyHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveUkPropertyHateoasData): Seq[Link] = {
      import data._

      Seq(
        getUkPropertyBsas(appConfig, nino, calculationId, taxYear),
        adjustUkPropertyBsas(appConfig, nino, calculationId, taxYear)
      )
    }
  }
}

case class RetrieveUkPropertyHateoasData(nino: String, calculationId: String, taxYear: Option[TaxYear]) extends HateoasData
