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

package v6.ukPropertyBsas.retrieve.model.response

import play.api.libs.json.*
import shared.utils.JsonWritesUtil
import v6.common.model.{HasIncomeSourceType, HasTaxYear}
import v6.ukPropertyBsas.retrieve.def1.model.response.Def1_RetrieveUkPropertyBsasResponse
import v6.ukPropertyBsas.retrieve.def2.model.response.Def2_RetrieveUkPropertyBsasResponse

trait RetrieveUkPropertyBsasResponse extends HasIncomeSourceType with HasTaxYear

object RetrieveUkPropertyBsasResponse extends JsonWritesUtil {

  implicit val writes: OWrites[RetrieveUkPropertyBsasResponse] = writesFrom {
    case def1: Def1_RetrieveUkPropertyBsasResponse =>
      implicitly[OWrites[Def1_RetrieveUkPropertyBsasResponse]].writes(def1)
    case def2: Def2_RetrieveUkPropertyBsasResponse =>
      implicitly[OWrites[Def2_RetrieveUkPropertyBsasResponse]].writes(def2)
  }

}
