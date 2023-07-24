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

package v2.models.response.listBsas

import api.hateoas.HateoasListLinksFactory
import api.models.hateoas.{HateoasData, Link}
import cats.Functor
import config.AppConfig
import play.api.libs.json.{Json, OWrites, Reads, Writes}
import v2.hateoas.HateoasLinks
import v2.models.domain.TypeOfBusiness

case class ListBsasResponse[I](businessSourceSummaries: Seq[BusinessSourceSummary[I]])

object ListBsasResponse extends HateoasLinks {

  implicit def reads[I: Reads]: Reads[ListBsasResponse[I]] = implicitly[Reads[Seq[BusinessSourceSummary[I]]]].map(ListBsasResponse(_))

  implicit def writes[I: Writes]: OWrites[ListBsasResponse[I]] = Json.writes[ListBsasResponse[I]]

  implicit object LinksFactory extends HateoasListLinksFactory[ListBsasResponse, BsasEntries, ListBsasHateoasData] {
    override def links(appConfig: AppConfig, data: ListBsasHateoasData): Seq[Link] =
      Seq(triggerBsas(appConfig, data.nino), listBsas(appConfig, data.nino))

    override def itemLinks(appConfig: AppConfig, data: ListBsasHateoasData, item: BsasEntries): Seq[Link] = {
      val filteredSummary: Seq[BusinessSourceSummary[BsasEntries]] =
        data.listBsasResponse.businessSourceSummaries.filter(_.bsasEntries.contains(item))

      filteredSummary.flatMap(
        summary =>
          summary.bsasEntries
            .filter(_ == item)
            .flatMap(_ =>
              summary.typeOfBusiness match {
                case TypeOfBusiness.`self-employment` => Seq(getSelfEmploymentBsas(appConfig, data.nino, item.bsasId))
                case TypeOfBusiness.`uk-property-fhl` | TypeOfBusiness.`uk-property-non-fhl` =>
                  Seq(getUkPropertyBsas(appConfig, data.nino, item.bsasId))
                case TypeOfBusiness.`foreign-property` | TypeOfBusiness.`foreign-property-fhl-eea` =>
                  Seq(getForeignPropertyBsas(appConfig, data.nino, item.bsasId))
            }))
    }
  }

  implicit object ResponseFunctor extends Functor[ListBsasResponse] {
    override def map[A, B](fa: ListBsasResponse[A])(f: A => B): ListBsasResponse[B] =
      ListBsasResponse(fa.businessSourceSummaries.map { summary =>
        summary.copy(bsasEntries = summary.bsasEntries.map(f))
      })
  }

}

case class ListBsasHateoasData(nino: String, listBsasResponse: ListBsasResponse[BsasEntries]) extends HateoasData
