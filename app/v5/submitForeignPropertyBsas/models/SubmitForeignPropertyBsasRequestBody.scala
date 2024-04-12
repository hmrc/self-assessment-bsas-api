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

package v5.submitForeignPropertyBsas.models

import play.api.libs.json._
import v5.submitForeignPropertyBsas.models.def1.Def1_SubmitForeignPropertyBsasRequestBody

trait SubmitForeignPropertyBsasRequestBody

object SubmitForeignPropertyBsasRequestBody {

  implicit val writes: OWrites[SubmitForeignPropertyBsasRequestBody] = OWrites.apply[SubmitForeignPropertyBsasRequestBody] {
    case a: Def1_SubmitForeignPropertyBsasRequestBody => implicitly[OWrites[Def1_SubmitForeignPropertyBsasRequestBody]].writes(a)

    case a: SubmitForeignPropertyBsasRequestBody => throw new RuntimeException(s"No writes defined for type ${a.getClass.getName}")
  }

}