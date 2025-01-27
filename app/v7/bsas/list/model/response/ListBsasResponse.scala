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

package v7.bsas.list.model.response

import play.api.libs.json.OWrites
import shared.utils.JsonWritesUtil
import v7.bsas.list.def1.model.response.Def1_ListBsasResponse
import v7.bsas.list.def2.model.response.Def2_ListBsasResponse

trait ListBsasResponse

object ListBsasResponse extends JsonWritesUtil {

  implicit val writes: OWrites[ListBsasResponse] = writesFrom {
    case def1: Def1_ListBsasResponse =>
      implicitly[OWrites[Def1_ListBsasResponse]].writes(def1)
    case def2: Def2_ListBsasResponse =>
      implicitly[OWrites[Def2_ListBsasResponse]].writes(def2)
  }

}
