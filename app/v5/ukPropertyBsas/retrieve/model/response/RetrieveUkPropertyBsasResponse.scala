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

package v5.ukPropertyBsas.retrieve.model.response

import play.api.libs.json._
import shared.config.AppConfig
import shared.hateoas.{HateoasData, HateoasLinksFactory, Link}
import shared.models.domain.TaxYear
import shared.utils.JsonWritesUtil
import v5.hateoas.HateoasLinks
import v5.models.domain.HasTypeOfBusiness
import v5.ukPropertyBsas.retrieve.def1.model.response.Def1_RetrieveUkPropertyBsasResponse

trait RetrieveUkPropertyBsasResponse extends HasTypeOfBusiness

object RetrieveUkPropertyBsasResponse extends HateoasLinks with JsonWritesUtil {

  implicit val writes: OWrites[RetrieveUkPropertyBsasResponse] = writesFrom { case a: Def1_RetrieveUkPropertyBsasResponse =>
    implicitly[OWrites[Def1_RetrieveUkPropertyBsasResponse]].writes(a)
  }

  implicit object RetrieveSelfAssessmentBsasHateoasFactory
      extends HateoasLinksFactory[RetrieveUkPropertyBsasResponse, RetrieveUkPropertyHateoasData] {

    override def links(appConfig: AppConfig, data: RetrieveUkPropertyHateoasData): Seq[Link] = {
      import data._

      Seq(
        getUkPropertyBsas(appConfig, nino, calculationId, taxYear),
        adjustUkPropertyBsas(appConfig, nino, calculationId, taxYear)
      )
    }

  }

}

case class RetrieveUkPropertyHateoasData(nino: String, calculationId: String, taxYear: Option[TaxYear]) extends HateoasData
