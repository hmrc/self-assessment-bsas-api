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
import v2.hateoas.{HateoasLinks, HateoasLinksFactory}
import v2.models.hateoas.{HateoasData, Link}
import play.api.libs.functional.syntax._
import v2.models.domain.{IncomeSourceType, TypeOfBusiness}

case class SubmitSelfEmploymentBsasResponse(id: String, typeOfBusiness: TypeOfBusiness)

object SubmitSelfEmploymentBsasResponse extends HateoasLinks {

  implicit val reads: Reads[SubmitSelfEmploymentBsasResponse] = (
    (JsPath \ "metadata" \ "calculationId").read[String] and
      (JsPath \ "inputs" \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness)
    )(SubmitSelfEmploymentBsasResponse.apply _)

  implicit val writes: OWrites[SubmitSelfEmploymentBsasResponse] = new OWrites[SubmitSelfEmploymentBsasResponse] {
    def writes(response: SubmitSelfEmploymentBsasResponse): JsObject =
      Json.obj(
        "id" -> response.id
      )
  }

  implicit object SubmitSelfEmploymentAdjustmentHateoasFactory
    extends HateoasLinksFactory[SubmitSelfEmploymentBsasResponse, SubmitSelfEmploymentBsasHateoasData] {
    override def links(appConfig: AppConfig, data: SubmitSelfEmploymentBsasHateoasData): Seq[Link] = {
      import data._

      Seq(
        getSelfEmploymentBsasAdjustments(appConfig, nino, bsasId),
        getAdjustedSelfEmploymentBsas(appConfig, nino, bsasId)
      )
    }
  }

}

case class SubmitSelfEmploymentBsasHateoasData(nino: String, bsasId: String) extends HateoasData