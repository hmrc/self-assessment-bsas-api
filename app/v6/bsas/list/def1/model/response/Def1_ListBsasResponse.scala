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

package v6.bsas.list.def1.model.response

import play.api.libs.json.{Json, OWrites, Reads, Writes}
import v6.bsas.list.model.response.ListBsasResponse
import v6.common.model.TypeOfBusinessWithFHL

case class Def1_ListBsasResponse[I](businessSources: Seq[BusinessSource[I]]) extends ListBsasResponse[I] {

  override def typeOfBusinessFor[A >: I](item: A): Option[TypeOfBusinessWithFHL] =
    businessSources.find(_.summaries.contains(item)).map(_.typeOfBusiness)

  override def mapItems[B](f: I => B): ListBsasResponse[B] = {
    Def1_ListBsasResponse(businessSources.map { businessSource =>
      businessSource.copy(summaries = businessSource.summaries.map(f))
    })
  }

}

object Def1_ListBsasResponse {

  implicit def reads[I: Reads]: Reads[Def1_ListBsasResponse[I]] =
    implicitly[Reads[Seq[BusinessSource[I]]]].map(Def1_ListBsasResponse(_))

  implicit def writes[I: Writes]: OWrites[Def1_ListBsasResponse[I]] = Json.writes[Def1_ListBsasResponse[I]]
}
