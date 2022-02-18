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

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v3.hateoas.HateoasLinksFactory
import v3.models.domain.{HasTypeOfBusiness, TypeOfBusiness}
import v3.models.hateoas.{HateoasData, Link}
import v3.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse._

case class RetrieveForeignPropertyBsasResponse(metadata: Metadata,
                                               inputs: Inputs,
                                               adjustableSummaryCalculation: AdjustableSummaryCalculation,
                                               adjustments: Option[Adjustments],
                                               adjustedSummaryCalculation: Option[AdjustableSummaryCalculation])
  extends HasTypeOfBusiness {
  override def typeOfBusiness: TypeOfBusiness = inputs.typeOfBusiness
}

object RetrieveForeignPropertyBsasResponse {
  implicit val reads: Reads[RetrieveForeignPropertyBsasResponse] = (
    (JsPath \ "metadata").read[Metadata] and
      (JsPath \ "inputs").read[Inputs] and
      (JsPath \ "adjustableSummaryCalculation").read[AdjustableSummaryCalculation] and
      (JsPath \ "adjustments")
        .read[Seq[CountryLevelDetail]]
        .map(s => Adjustments(Some(s), None, None))
        .map(Option(_))
        .orElse(
          (JsPath \ "adjustments").readNullable[Adjustments]
        ) and
      (JsPath \ "adjustedSummaryCalculation").readNullable[AdjustableSummaryCalculation]
    ) (RetrieveForeignPropertyBsasResponse.apply _)

  implicit val writes: OWrites[RetrieveForeignPropertyBsasResponse] = Json.writes[RetrieveForeignPropertyBsasResponse]

  implicit object RetrieveSelfAssessmentBsasHateoasFactory
    extends HateoasLinksFactory[RetrieveForeignPropertyBsasResponse, RetrieveForeignPropertyHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveForeignPropertyHateoasData): Seq[Link] = {
      import data._

      Seq(
        getForeignPropertyBsas(appConfig, nino, calculationId),
        adjustForeignPropertyBsas(appConfig, nino, calculationId)
      )
    }
  }
}


case class RetrieveForeignPropertyHateoasData(nino: String, calculationId: String) extends HateoasData