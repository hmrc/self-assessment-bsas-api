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

package v1.models.response.listBsas

import cats.Functor
import config.AppConfig
import play.api.libs.json.{Json, OWrites, Reads, Writes}
import v1.hateoas.{HateoasLinks, HateoasListLinksFactory}
import v1.models.hateoas.Link
import v1.models.hateoas.Method.GET

case class ListBsasResponse[I](businessSourceSummaries: Seq[BusinessSourceSummary[I]])

object ListBsasResponse extends HateoasLinks {

  implicit def reads[I: Reads]: Reads[ListBsasResponse[I]] = implicitly[Reads[Seq[BusinessSourceSummary[I]]]].map(ListBsasResponse(_))

  implicit def writes[I: Writes]: OWrites[ListBsasResponse[I]] = Json.writes[ListBsasResponse[I]]

  implicit object LinksFactory extends HateoasListLinksFactory[ListBsasResponse, BsasEntries, ListBsasHateoasData] {
    override def links(appConfig: AppConfig, data: ListBsasHateoasData): Seq[Link] =
      Seq(Link("/foo", GET, ""))

    override def itemLinks(appConfig: AppConfig, data: ListBsasHateoasData, item: BsasEntries): Seq[Link] =
      Seq(Link("/bar", GET, ""))
  }

  implicit object ResponseFunctor extends Functor[ListBsasResponse] {
    override def map[A, B](fa: ListBsasResponse[A])(f: A => B): ListBsasResponse[B] =
      ListBsasResponse(fa.businessSourceSummaries.map {
        summary => summary.copy(bsasEntries = summary.bsasEntries.map(f))
      })
  }

}

case class ListBsasHateoasData(nino: String, taxYear: Option[String], selfEmploymentId: Option[String], incomeSourceType: Option[String])
