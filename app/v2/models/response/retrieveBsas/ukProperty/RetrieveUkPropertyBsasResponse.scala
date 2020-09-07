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

package v2.models.response.retrieveBsas.ukProperty

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json._
import v2.hateoas.{HateoasLinks, HateoasLinksFactory}
import v2.models.domain.{IncomeSourceType, TypeOfBusiness}
import v2.models.hateoas.{HateoasData, Link}

case class RetrieveUkPropertyBsasResponse(metadata: Metadata,
                                          bsas: Option[BsasDetail])

object RetrieveUkPropertyBsasResponse extends HateoasLinks {

  implicit val reads: Reads[RetrieveUkPropertyBsasResponse] = (
    JsPath.read[Metadata] and
      (JsPath \ "inputs" \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness).flatMap {
        case TypeOfBusiness.`uk-property-fhl` => fhlBsasDetailReads
        case TypeOfBusiness.`uk-property-non-fhl` => nonFhlBsasDetailReads
        case _ => fhlBsasDetailReads // Reading as normal property, we are handling the error in the service layer.
      }
    )(RetrieveUkPropertyBsasResponse.apply _)

  private val fhlBsasDetailReads = (JsPath \ "adjustedSummaryCalculation").readNullable[JsObject].flatMap{
    case Some(_) => (JsPath \ "adjustedSummaryCalculation").readNullable[BsasDetail](BsasDetail.fhlReads)
    case _ => (JsPath \ "adjustableSummaryCalculation").readNullable[BsasDetail](BsasDetail.fhlReads)
  }

  private val nonFhlBsasDetailReads = (JsPath \ "adjustedSummaryCalculation").readNullable[JsObject].flatMap{
    case Some(_) => (JsPath \ "adjustedSummaryCalculation").readNullable[BsasDetail](BsasDetail.nonFhlReads)
    case _ => (JsPath \ "adjustableSummaryCalculation").readNullable[BsasDetail](BsasDetail.nonFhlReads)
  }

  implicit val writes: OWrites[RetrieveUkPropertyBsasResponse] = Json.writes[RetrieveUkPropertyBsasResponse]

  implicit object RetrieveUkPropertyBsasHateoasFactory extends HateoasLinksFactory[RetrieveUkPropertyBsasResponse, RetrieveUkPropertyHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveUkPropertyHateoasData): Seq[Link] = {
      Seq(
        getUkPropertyBsas(appConfig, data.nino, data.bsasId), adjustPropertyBsas(appConfig, data.nino, data.bsasId)
      )
    }
  }
}

case class RetrieveUkPropertyHateoasData(nino: String, bsasId: String) extends HateoasData
