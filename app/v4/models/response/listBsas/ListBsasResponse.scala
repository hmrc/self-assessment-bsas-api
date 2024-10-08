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

package v4.models.response.listBsas

import shared.hateoas.{HateoasData, HateoasListLinksFactory, Link}
import shared.models.domain.TaxYear
import cats.Functor
import play.api.libs.json.{Json, OWrites, Reads, Writes}
import shared.config.SharedAppConfig
import v4.hateoas.HateoasLinks
import v4.models.domain.TypeOfBusiness._

case class ListBsasResponse[I](businessSources: Seq[BusinessSourceSummary[I]])

object ListBsasResponse extends HateoasLinks {

  implicit def reads[I: Reads]: Reads[ListBsasResponse[I]] = implicitly[Reads[Seq[BusinessSourceSummary[I]]]].map(ListBsasResponse(_))

  implicit def writes[I: Writes]: OWrites[ListBsasResponse[I]] = Json.writes[ListBsasResponse[I]]

  implicit object LinksFactory extends HateoasListLinksFactory[ListBsasResponse, BsasSummary, ListBsasHateoasData] {

    override def links(appConfig: SharedAppConfig, data: ListBsasHateoasData): Seq[Link] = Seq(
      triggerBsas(appConfig, data.nino),
      listBsas(appConfig, data.nino, data.taxYear)
    )

    override def itemLinks(appConfig: SharedAppConfig, data: ListBsasHateoasData, item: BsasSummary): Seq[Link] = {
      val filteredSummary: Seq[BusinessSourceSummary[BsasSummary]] = data.listBsasResponse.businessSources.filter(_.summaries.contains(item))

      filteredSummary.flatMap(summary =>
        summary.summaries
          .filter(_ == item)
          .map(_ =>
            summary.typeOfBusiness match {
              case `self-employment`                               => getSelfEmploymentBsas(appConfig, data.nino, item.calculationId, data.taxYear)
              case `uk-property-fhl` | `uk-property-non-fhl`       => getUkPropertyBsas(appConfig, data.nino, item.calculationId, data.taxYear)
              case `foreign-property` | `foreign-property-fhl-eea` => getForeignPropertyBsas(appConfig, data.nino, item.calculationId, data.taxYear)
            }))
    }

  }

  implicit object ResponseFunctor extends Functor[ListBsasResponse] {

    override def map[A, B](fa: ListBsasResponse[A])(f: A => B): ListBsasResponse[B] =
      ListBsasResponse(fa.businessSources.map { summary =>
        summary.copy(summaries = summary.summaries.map(f))
      })

  }

}

case class ListBsasHateoasData(nino: String, listBsasResponse: ListBsasResponse[BsasSummary], taxYear: Option[TaxYear]) extends HateoasData
