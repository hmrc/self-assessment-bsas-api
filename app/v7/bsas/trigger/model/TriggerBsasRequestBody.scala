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

package v7.bsas.trigger.model

import play.api.libs.json.OWrites
import shared.utils.JsonWritesUtil
import v7.bsas.trigger.def1.model.request.Def1_TriggerBsasRequestBody
import v7.bsas.trigger.def2.model.request.Def2_TriggerBsasRequestBody

trait TriggerBsasRequestBody {
  val typeOfBusiness: String
}

object TriggerBsasRequestBody extends JsonWritesUtil {

  implicit val writes: OWrites[TriggerBsasRequestBody] = writesFrom {
    case def1: Def1_TriggerBsasRequestBody => implicitly[OWrites[Def1_TriggerBsasRequestBody]].writes(def1)
    case def2: Def2_TriggerBsasRequestBody => implicitly[OWrites[Def2_TriggerBsasRequestBody]].writes(def2)
  }

}
