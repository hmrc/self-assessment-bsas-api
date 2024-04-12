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

package v5.triggerBsas.models

import play.api.libs.json.OWrites
import v5.triggerBsas.models.def1.Def1_TriggerBsasRequestBody

trait TriggerBsasRequestBody

object TriggerBsasRequestBody {

  implicit val writes: OWrites[TriggerBsasRequestBody] = OWrites{
    case a: Def1_TriggerBsasRequestBody => implicitly[OWrites[Def1_TriggerBsasRequestBody]].writes(a)
    case a: TriggerBsasRequestBody => throw new RuntimeException(s"No writes defined for type ${a.getClass.getName}")
  }

}