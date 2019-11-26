/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.response

import config.AppConfig
import play.api.libs.json._
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.domain.TypeOfBusiness
import v1.models.hateoas.{HateoasData, Link}

case class TriggerBsasResponse(id: String)

object TriggerBsasResponse extends HateoasLinks {

  implicit val writes: OWrites[TriggerBsasResponse] = Json.writes[TriggerBsasResponse]

  implicit val reads: Reads[TriggerBsasResponse] =
    (JsPath \ "metadata" \ "calculationId").read[String].map(TriggerBsasResponse.apply)

  implicit object TriggerHateoasFactory extends HateoasLinksFactory[TriggerBsasResponse, TriggerBsasHateoasData] {
    override def links(appConfig: AppConfig, data: TriggerBsasHateoasData): Seq[Link] = {
      import TypeOfBusiness._
      import data._

      data.typeOfBusiness match {
        case `self-employment` =>
          Seq(
            getSelfEmploymentBsas(appConfig, nino, bsasId),
            adjustSelfEmploymentBsas(appConfig, nino, bsasId)
          )
        case `uk-property-fhl` | `uk-property-non-fhl` =>
          Seq(
            getPropertyBsas(appConfig, nino, bsasId),
            adjustPropertyBsas(appConfig, nino, bsasId)
          )
      }
    }
  }
}

case class TriggerBsasHateoasData(nino: String, typeOfBusiness: TypeOfBusiness, bsasId: String) extends HateoasData
