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

package v5.retrieveForeignPropertyBsas.models

import play.api.libs.json._
import shared.config.AppConfig
import shared.hateoas.{HateoasData, HateoasLinksFactory, Link}
import shared.models.domain.TaxYear
import v5.models.domain.HasTypeOfBusiness
import v5.retrieveForeignPropertyBsas.models.def1.Def1_RetrieveForeignPropertyBsasResponse
import v5.retrieveUkPropertyBsas.models.RetrieveUkPropertyBsasResponse._

trait RetrieveForeignPropertyBsasResponse extends HasTypeOfBusiness

object RetrieveForeignPropertyBsasResponse {

  implicit val writes: OWrites[RetrieveForeignPropertyBsasResponse] = OWrites.apply[RetrieveForeignPropertyBsasResponse] {
    case a: Def1_RetrieveForeignPropertyBsasResponse =>
      implicitly[OWrites[Def1_RetrieveForeignPropertyBsasResponse]].writes(a)

    case a: RetrieveForeignPropertyBsasResponse => throw new RuntimeException(s"No writes defined for type ${a.getClass.getName}")
  }

  implicit object RetrieveSelfAssessmentBsasHateoasFactory
    extends HateoasLinksFactory[RetrieveForeignPropertyBsasResponse, RetrieveForeignPropertyHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveForeignPropertyHateoasData): Seq[Link] = {
      import data._

      Seq(
        getForeignPropertyBsas(appConfig, nino, calculationId, taxYear),
        adjustForeignPropertyBsas(appConfig, nino, calculationId, taxYear)
      )
    }
  }
}

case class RetrieveForeignPropertyHateoasData(nino: String, calculationId: String, taxYear: Option[TaxYear]) extends HateoasData
