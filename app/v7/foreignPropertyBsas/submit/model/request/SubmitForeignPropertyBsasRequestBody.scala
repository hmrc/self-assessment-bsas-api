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

package v7.foreignPropertyBsas.submit.model.request

import play.api.libs.json.*
import shared.utils.JsonWritesUtil
import v7.foreignPropertyBsas.submit.def1.model.request.Def1_SubmitForeignPropertyBsasRequestBody
import v7.foreignPropertyBsas.submit.def2.model.request.Def2_SubmitForeignPropertyBsasRequestBody
import v7.foreignPropertyBsas.submit.def3.model.request.Def3_SubmitForeignPropertyBsasRequestBody
import v7.foreignPropertyBsas.submit.def4.model.request.Def4_SubmitForeignPropertyBsasRequestBody

trait SubmitForeignPropertyBsasRequestBody

object SubmitForeignPropertyBsasRequestBody extends JsonWritesUtil {

  implicit val writes: OWrites[SubmitForeignPropertyBsasRequestBody] = writesFrom {
    case def1: Def1_SubmitForeignPropertyBsasRequestBody =>
      implicitly[OWrites[Def1_SubmitForeignPropertyBsasRequestBody]].writes(def1)
    case def2: Def2_SubmitForeignPropertyBsasRequestBody =>
      implicitly[OWrites[Def2_SubmitForeignPropertyBsasRequestBody]].writes(def2)
    case def3: Def3_SubmitForeignPropertyBsasRequestBody =>
      implicitly[OWrites[Def3_SubmitForeignPropertyBsasRequestBody]].writes(def3)
    case def4: Def4_SubmitForeignPropertyBsasRequestBody =>
      implicitly[OWrites[Def4_SubmitForeignPropertyBsasRequestBody]].writes(def4)
  }

}
