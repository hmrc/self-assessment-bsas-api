/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.models.response.retrieveBsasAdjustments.ukProperty

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v2.hateoas.{HateoasLinks, HateoasLinksFactory}
import v2.models.domain.{IncomeSourceType, TypeOfBusiness}
import v2.models.hateoas.{HateoasData, Link}

case class RetrieveUkPropertyAdjustmentsResponse(metadata: Metadata, adjustments: BsasDetail)

object RetrieveUkPropertyAdjustmentsResponse extends HateoasLinks {

  implicit val reads: Reads[RetrieveUkPropertyAdjustmentsResponse] = (
    JsPath.read[Metadata] and
      (JsPath \ "inputs" \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness).flatMap {
        case TypeOfBusiness.`uk-property-fhl` => BsasDetail.fhlReads
        case TypeOfBusiness.`uk-property-non-fhl` => BsasDetail.nonFhlReads
        case _ => BsasDetail.fhlReads // Reading as normal property, we are handling the error in the service layer.
      }
    )(RetrieveUkPropertyAdjustmentsResponse.apply _)

  implicit val writes: OWrites[RetrieveUkPropertyAdjustmentsResponse] = Json.writes[RetrieveUkPropertyAdjustmentsResponse]

  implicit object RetrieveUkPropertyBsasHateoasFactory
    extends HateoasLinksFactory[RetrieveUkPropertyAdjustmentsResponse, RetrieveUkPropertyAdjustmentsHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveUkPropertyAdjustmentsHateoasData): Seq[Link] = {
      import data._

      Seq(
        getAdjustedPropertyBsas(appConfig, nino, bsasId),
        getPropertyBsasAdjustments(appConfig, nino, bsasId)
      )
    }
  }
}

case class RetrieveUkPropertyAdjustmentsHateoasData(nino: String, bsasId: String) extends HateoasData
