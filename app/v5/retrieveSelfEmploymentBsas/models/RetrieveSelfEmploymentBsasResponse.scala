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

package v5.retrieveSelfEmploymentBsas.models

import play.api.libs.json._
import shared.config.AppConfig
import shared.hateoas.{HateoasData, HateoasLinksFactory, Link}
import shared.models.domain.TaxYear
import v5.hateoas.HateoasLinks
import v5.models.domain.HasTypeOfBusiness
import v5.retrieveSelfEmploymentBsas.models.def1.Def1_RetrieveSelfEmploymentBsasResponse

trait RetrieveSelfEmploymentBsasResponse extends HasTypeOfBusiness

object RetrieveSelfEmploymentBsasResponse extends HateoasLinks {

  implicit val writes: OWrites[RetrieveSelfEmploymentBsasResponse] = OWrites.apply[RetrieveSelfEmploymentBsasResponse] {
    case a: Def1_RetrieveSelfEmploymentBsasResponse =>
      implicitly[OWrites[Def1_RetrieveSelfEmploymentBsasResponse]].writes(a)

    case a: RetrieveSelfEmploymentBsasResponse => throw new RuntimeException(s"No writes defined for type ${a.getClass.getName}")
  }

  implicit object RetrieveSelfAssessmentBsasHateoasFactory
      extends HateoasLinksFactory[RetrieveSelfEmploymentBsasResponse, RetrieveSelfEmploymentBsasHateoasData] {

    override def links(appConfig: AppConfig, data: RetrieveSelfEmploymentBsasHateoasData): Seq[Link] = {
      import data._

      Seq(
        getSelfEmploymentBsas(appConfig, nino, bsasId, taxYear),
        adjustSelfEmploymentBsas(appConfig, nino, bsasId, taxYear)
      )
    }

  }

}

case class RetrieveSelfEmploymentBsasHateoasData(nino: String, bsasId: String, taxYear: Option[TaxYear]) extends HateoasData
