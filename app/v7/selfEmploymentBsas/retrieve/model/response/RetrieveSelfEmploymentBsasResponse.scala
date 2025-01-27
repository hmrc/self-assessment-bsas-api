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

package v7.selfEmploymentBsas.retrieve.model.response

import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json._
import shared.utils.JsonWritesUtil
import v7.common.model.{HasIncomeSourceType, HasTaxYear}
import v7.selfEmploymentBsas.retrieve.def1.model.response.Def1_RetrieveSelfEmploymentBsasResponse
import v7.selfEmploymentBsas.retrieve.def2.model.response.Def2_RetrieveSelfEmploymentBsasResponse

trait RetrieveSelfEmploymentBsasResponse extends HasIncomeSourceType with HasTaxYear

object RetrieveSelfEmploymentBsasResponse extends JsonWritesUtil {

  implicit val writes: OWrites[RetrieveSelfEmploymentBsasResponse] = writesFrom {
    case def1: Def1_RetrieveSelfEmploymentBsasResponse =>
      implicitly[OWrites[Def1_RetrieveSelfEmploymentBsasResponse]].writes(def1)
    case def2: Def2_RetrieveSelfEmploymentBsasResponse =>
      implicitly[OWrites[Def2_RetrieveSelfEmploymentBsasResponse]].writes(def2)
  }

}
