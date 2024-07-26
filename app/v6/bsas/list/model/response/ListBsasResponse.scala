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

package v6.bsas.list.model.response

import cats.Functor
import play.api.libs.json.{OWrites, Writes}
import shared.utils.JsonWritesUtil
import v6.bsas.list.def1.model.response.{Def1_BsasSummary, Def1_ListBsasResponse}
import v6.common.model.TypeOfBusiness

trait BsasSummary {
  def calculationId: String
}

object BsasSummary extends JsonWritesUtil {

  implicit val writes: OWrites[BsasSummary] = writesFrom { case a: Def1_BsasSummary =>
    implicitly[OWrites[Def1_BsasSummary]].writes(a)
  }

}

trait ListBsasResponse[+I] {
  def typeOfBusinessFor[A >: I](item: A): Option[TypeOfBusiness]

  def mapItems[B](f: I => B): ListBsasResponse[B]
}

object ListBsasResponse extends JsonWritesUtil {

  implicit def writes[I: Writes]: OWrites[ListBsasResponse[I]] = writesFrom { case a: Def1_ListBsasResponse[I] =>
    implicitly[OWrites[Def1_ListBsasResponse[I]]].writes(a)
  }

  implicit object ResponseFunctor extends Functor[ListBsasResponse] {
    override def map[A, B](fa: ListBsasResponse[A])(f: A => B): ListBsasResponse[B] = fa.mapItems(f)
  }

}
