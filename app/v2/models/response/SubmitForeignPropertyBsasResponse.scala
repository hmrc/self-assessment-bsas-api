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

package v2.models.response

import config.AppConfig
import play.api.libs.json.{JsObject, JsPath, Json, OWrites, Reads}
import v2.models.domain.{IncomeSourceType, TypeOfBusiness}
import play.api.libs.functional.syntax._
import v2.hateoas.{HateoasLinks, HateoasLinksFactory}
import v2.models.hateoas.{HateoasData, Link}

case class SubmitForeignPropertyBsasResponse(id: String, typeOfBusiness: TypeOfBusiness)

object SubmitForeignPropertyBsasResponse extends HateoasLinks {

  implicit val writes: OWrites[SubmitForeignPropertyBsasResponse] = new OWrites[SubmitForeignPropertyBsasResponse] {
    def writes(response: SubmitForeignPropertyBsasResponse): JsObject =
      Json.obj(
        "id" -> response.id
      )
  }

  implicit val reads: Reads[SubmitForeignPropertyBsasResponse] = (
    (JsPath \ "metadata" \ "calculationId").read[String] and
      (JsPath \ "inputs" \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness)
    )(SubmitForeignPropertyBsasResponse.apply _)

  implicit object SubmitForeignPropertyAdjustmentHateoasFactory
    extends HateoasLinksFactory[SubmitForeignPropertyBsasResponse, SubmitForeignPropertyBsasHateoasData] {
    override def links(appConfig: AppConfig, data: SubmitForeignPropertyBsasHateoasData): Seq[Link] = {
      import data._

      Seq(
        getForeignPropertyBsasAdjustments(appConfig, nino, bsasId),
        getAdjustedForeignPropertyBsas(appConfig, nino, bsasId)
      )
    }
  }
}

case class SubmitForeignPropertyBsasHateoasData(nino: String, bsasId: String) extends HateoasData
