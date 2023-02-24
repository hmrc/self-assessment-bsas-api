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

package v3.models.response

import api.hateoas.{HateoasData, HateoasLinksFactory, Link}
import api.models.domain.TaxYear
import config.AppConfig
import play.api.libs.json._
import v3.hateoas.HateoasLinks
import v3.models.domain.TypeOfBusiness

case class TriggerBsasResponse(calculationId: String)

object TriggerBsasResponse extends HateoasLinks {

  implicit val writes: OWrites[TriggerBsasResponse] = Json.writes[TriggerBsasResponse]

  implicit val reads: Reads[TriggerBsasResponse] =
    (JsPath \ "metadata" \ "calculationId").read[String].map(TriggerBsasResponse.apply)

  implicit object TriggerHateoasFactory extends HateoasLinksFactory[TriggerBsasResponse, TriggerBsasHateoasData] {
    override def links(appConfig: AppConfig, data: TriggerBsasHateoasData): Seq[Link] = {
      import data._
      import v3.models.domain.TypeOfBusiness._

      data.typeOfBusiness match {
        case `self-employment`                               => Seq(getSelfEmploymentBsas(appConfig, nino, bsasId, taxYear))
        case `uk-property-fhl` | `uk-property-non-fhl`       => Seq(getUkPropertyBsas(appConfig, nino, bsasId, taxYear))
        case `foreign-property` | `foreign-property-fhl-eea` => Seq(getForeignPropertyBsas(appConfig, nino, bsasId, taxYear))
      }
    }
  }
}

case class TriggerBsasHateoasData(nino: String, typeOfBusiness: TypeOfBusiness, bsasId: String, taxYear: Option[TaxYear]) extends HateoasData
