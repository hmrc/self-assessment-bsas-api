/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.response.retrieveBsasAdjustments

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json._
import v1.hateoas.{HateoasLinksFactory, HateoasLinks}
import v1.models.hateoas.{HateoasData, Link}

case class RetrieveSelfEmploymentAdjustmentsResponse(metadata: Metadata,
                                                     adjustments: BsasDetail)

object RetrieveSelfEmploymentAdjustmentsResponse extends HateoasLinks {

  implicit val reads: Reads[RetrieveSelfEmploymentAdjustmentsResponse] = (
    JsPath.read[Metadata] and
      JsPath.read[BsasDetail]
    ) (RetrieveSelfEmploymentAdjustmentsResponse.apply _)

  implicit val writes: OWrites[RetrieveSelfEmploymentAdjustmentsResponse] = Json.writes[RetrieveSelfEmploymentAdjustmentsResponse]

  implicit object RetrieveSelfAssessmentAdjustmentsHateoasFactory
    extends HateoasLinksFactory[RetrieveSelfEmploymentAdjustmentsResponse, RetrieveSelfAssessmentAdjustmentsHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveSelfAssessmentAdjustmentsHateoasData): Seq[Link] = {
      import data._

      Seq(
        getAdjustedSelfEmploymentBsas(appConfig, nino, bsasId),
        getSelfEmploymentBsasAdjustments(appConfig, nino, bsasId)
      )
    }
  }
}

case class RetrieveSelfAssessmentAdjustmentsHateoasData(nino: String, bsasId: String) extends HateoasData