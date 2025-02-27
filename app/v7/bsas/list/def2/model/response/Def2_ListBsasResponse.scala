/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.bsas.list.def2.model.response

import play.api.libs.json.{Json, OWrites, Reads}
import v7.bsas.list.model.response.ListBsasResponse

case class Def2_ListBsasResponse(businessSources: Seq[BusinessSource]) extends ListBsasResponse

object Def2_ListBsasResponse {

  implicit val reads: Reads[Def2_ListBsasResponse] =
    implicitly[Reads[Seq[BusinessSource]]].map(Def2_ListBsasResponse(_))

  implicit val writes: OWrites[Def2_ListBsasResponse] = Json.writes[Def2_ListBsasResponse]
}
