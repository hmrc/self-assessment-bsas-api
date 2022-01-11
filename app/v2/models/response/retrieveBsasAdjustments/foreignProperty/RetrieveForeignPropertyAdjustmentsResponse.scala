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

package v2.models.response.retrieveBsasAdjustments.foreignProperty

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v2.hateoas.{HateoasLinks, HateoasLinksFactory}
import v2.models.domain.{IncomeSourceType, TypeOfBusiness}
import v2.models.hateoas.{HateoasData, Link}

case class RetrieveForeignPropertyAdjustmentsResponse(metadata: Metadata, adjustments: Seq[BsasDetail])

object RetrieveForeignPropertyAdjustmentsResponse extends HateoasLinks {

  implicit val reads: Reads[RetrieveForeignPropertyAdjustmentsResponse] = (
    JsPath.read[Metadata] and
      (JsPath \ "inputs" \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness).flatMap {
        case TypeOfBusiness.`foreign-property-fhl-eea` => (JsPath \ "adjustments").read[BsasDetail](BsasDetail.fhlReads).fmap(Seq(_))
        case TypeOfBusiness.`foreign-property`         => (JsPath \ "adjustments").read[Seq[BsasDetail]](BsasDetail.nonFhlSeqReads)
        case _                                         => (JsPath \ "adjustments").read[BsasDetail](BsasDetail.fhlReads).fmap(Seq(_))
        // Reading as normal property, we are handling the error in the service layer.
      }
  )(RetrieveForeignPropertyAdjustmentsResponse.apply _)

  implicit val writes: OWrites[RetrieveForeignPropertyAdjustmentsResponse] =
    (o: RetrieveForeignPropertyAdjustmentsResponse) =>
      o.metadata.typeOfBusiness match {
        case TypeOfBusiness.`foreign-property` =>
          Json.obj(
            "metadata"    -> o.metadata,
            "adjustments" -> o.adjustments
          )
        case TypeOfBusiness.`foreign-property-fhl-eea` =>
          Json.obj(
            "metadata"    -> o.metadata,
            "adjustments" -> o.adjustments
          )
      }

  implicit object RetrieveForeignPropertyAdjustmentsHateoasFactory
      extends HateoasLinksFactory[RetrieveForeignPropertyAdjustmentsResponse, RetrieveForeignPropertyAdjustmentsHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveForeignPropertyAdjustmentsHateoasData): Seq[Link] = {
      import data._

      Seq(
        getAdjustedForeignPropertyBsasNoStat(appConfig, nino, bsasId),
        getForeignPropertyBsasAdjustments(appConfig, nino, bsasId)
      )
    }
  }
}

case class RetrieveForeignPropertyAdjustmentsHateoasData(nino: String, bsasId: String) extends HateoasData
