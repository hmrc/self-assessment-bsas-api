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

import play.api.libs.json.{Json, OWrites, Reads}
import v6.bsas.list.model.response.ListBsasResponse

case class Def1_ListBsasResponse(businessSources: Seq[BusinessSource]) extends ListBsasResponse

object Def1_ListBsasResponse {

  implicit val reads: Reads[Def1_ListBsasResponse] =
    implicitly[Reads[Seq[BusinessSource]]].map(Def1_ListBsasResponse(_))

  implicit val writes: OWrites[Def1_ListBsasResponse] = Json.writes[Def1_ListBsasResponse]
}
