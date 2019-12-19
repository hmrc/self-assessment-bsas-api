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
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, Link}

case class SubmitSeBsasResponse(id: String)

object SubmitSeBsasResponse extends HateoasLinks {

  implicit val writes: OWrites[SubmitSeBsasResponse] = Json.writes[SubmitSeBsasResponse]

  implicit val reads: Reads[SubmitSeBsasResponse] =
    (JsPath \ "metadata" \ "calculationId").read[String].map(SubmitSeBsasResponse.apply)

  implicit object SubmitPropertyAdjustmentHateoasFactory
    extends HateoasLinksFactory[SubmitSeBsasResponse, SubmitSeBsasHateoasData] {
    override def links(appConfig: AppConfig, data: SubmitSeBsasHateoasData): Seq[Link] = {
      import data._

      Seq(
        getSelfEmploymentBsasAdjustments(appConfig, nino, bsasId),
        getAdjustedSelfEmploymentBsas(appConfig, nino, bsasId)
      )
    }
  }

}

case class SubmitSeBsasHateoasData(nino: String, bsasId: String) extends HateoasData