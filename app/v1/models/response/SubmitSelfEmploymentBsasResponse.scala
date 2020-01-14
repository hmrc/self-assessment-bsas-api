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
import play.api.libs.functional.syntax._
import play.api.libs.json._
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.domain.{IncomeSourceType, TypeOfBusiness}
import v1.models.hateoas.{HateoasData, Link}

case class SubmitSelfEmploymentBsasResponse(id: String, typeOfBusiness: TypeOfBusiness)

object SubmitSelfEmploymentBsasResponse extends HateoasLinks {

  implicit val reads: Reads[SubmitSelfEmploymentBsasResponse] = (
    (JsPath \ "metadata" \ "calculationId").read[String] and
      (JsPath \ "inputs" \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness)
    ) (SubmitSelfEmploymentBsasResponse.apply _)

  implicit val writes: OWrites[SubmitSelfEmploymentBsasResponse] = (response: SubmitSelfEmploymentBsasResponse) =>
    Json.obj(
      "id" -> response.id
    )

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