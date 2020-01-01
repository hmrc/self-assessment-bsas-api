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

package v1.models.response

import config.AppConfig
import play.api.libs.json._
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.domain.{IncomeSourceType, TypeOfBusiness}
import v1.models.hateoas.{HateoasData, Link}
import play.api.libs.functional.syntax._

case class SubmitUkPropertyBsasResponse(id: String, typeOfBusiness: TypeOfBusiness)

object SubmitUkPropertyBsasResponse extends HateoasLinks {

  implicit val writes: OWrites[SubmitUkPropertyBsasResponse] = new OWrites[SubmitUkPropertyBsasResponse] {
    def writes(response: SubmitUkPropertyBsasResponse): JsObject =
      Json.obj(
        "id" -> response.id
      )
  }


  implicit val reads: Reads[SubmitUkPropertyBsasResponse] = (
    (JsPath \ "metadata" \ "calculationId").read[String] and
      (JsPath \ "inputs" \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness)
    )(SubmitUkPropertyBsasResponse.apply _)

  implicit object SubmitPropertyAdjustmentHateoasFactory
    extends HateoasLinksFactory[SubmitUkPropertyBsasResponse, SubmitUkPropertyBsasHateoasData] {
    override def links(appConfig: AppConfig, data: SubmitUkPropertyBsasHateoasData): Seq[Link] = {
      import data._

      Seq(
        getPropertyBsasAdjustments(appConfig, nino, bsasId),
        getAdjustedPropertyBsas(appConfig, nino, bsasId)
      )
    }
  }

}

case class SubmitUkPropertyBsasHateoasData(nino: String, bsasId: String) extends HateoasData
