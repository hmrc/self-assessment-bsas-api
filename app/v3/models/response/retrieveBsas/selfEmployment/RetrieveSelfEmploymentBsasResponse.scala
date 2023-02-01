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

package v3.models.response.retrieveBsas.selfEmployment

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json._
import v3.hateoas.{ HateoasLinks, HateoasLinksFactory }
import v3.models.domain.{ HasTypeOfBusiness, TaxYear, TypeOfBusiness }
import v3.models.hateoas.{ HateoasData, Link }

case class RetrieveSelfEmploymentBsasResponse(
    metadata: Metadata,
    inputs: Inputs,
    adjustableSummaryCalculation: AdjustableSummaryCalculation,
    adjustments: Option[Adjustments],
    adjustedSummaryCalculation: Option[AdjustedSummaryCalculation]
) extends HasTypeOfBusiness {
  override def typeOfBusiness: TypeOfBusiness = inputs.typeOfBusiness
}

object RetrieveSelfEmploymentBsasResponse extends HateoasLinks {

  implicit val reads: Reads[RetrieveSelfEmploymentBsasResponse] = (
    (JsPath \ "metadata").read[Metadata] and
      (JsPath \ "inputs").read[Inputs] and
      (JsPath \ "adjustableSummaryCalculation").read[AdjustableSummaryCalculation] and
      (JsPath \ "adjustments").readNullable[Adjustments] and
      (JsPath \ "adjustedSummaryCalculation").readNullable[AdjustedSummaryCalculation]
  )(RetrieveSelfEmploymentBsasResponse.apply _)

  implicit val writes: OWrites[RetrieveSelfEmploymentBsasResponse] = Json.writes[RetrieveSelfEmploymentBsasResponse]

  implicit object RetrieveSelfAssessmentBsasHateoasFactory
      extends HateoasLinksFactory[RetrieveSelfEmploymentBsasResponse, RetrieveSelfAssessmentBsasHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveSelfAssessmentBsasHateoasData): Seq[Link] = {
      import data._

      Seq(
        getSelfEmploymentBsas(appConfig, nino, bsasId, taxYear),
        adjustSelfEmploymentBsas(appConfig, nino, bsasId, taxYear)
      )
    }
  }
}

case class RetrieveSelfAssessmentBsasHateoasData(nino: String, bsasId: String, taxYear: Option[TaxYear]) extends HateoasData
