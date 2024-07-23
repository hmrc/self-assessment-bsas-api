/*
 * Copyright 2024 HM Revenue & Customs
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

package v6.bsas.trigger.model

import play.api.libs.json.OWrites
import shared.utils.JsonWritesUtil
import v6.bsas.trigger.def1.model.response.Def1_TriggerBsasResponse
import v6.bsas.trigger.def2.model.response.Def2_TriggerBsasResponse

trait TriggerBsasResponse {
  val calculationId: String
}

object TriggerBsasResponse extends JsonWritesUtil {

  implicit val writes: OWrites[TriggerBsasResponse] = writesFrom {
    case def1: Def1_TriggerBsasResponse =>
    implicitly[OWrites[Def1_TriggerBsasResponse]].writes(def1)
    case def2: Def2_TriggerBsasResponse =>
      implicitly[OWrites[Def2_TriggerBsasResponse]].writes(def2)
  }

}
