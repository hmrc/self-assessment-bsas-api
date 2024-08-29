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

package v6.selfEmploymentBsas.retrieve.model.response

import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json._
import shared.utils.JsonWritesUtil
import v6.common.model.HasIncomeSourceType
import v6.selfEmploymentBsas.retrieve.def1.model.response.Def1_RetrieveSelfEmploymentBsasResponse

trait RetrieveSelfEmploymentBsasResponse extends HasIncomeSourceType

object RetrieveSelfEmploymentBsasResponse extends JsonWritesUtil {

  implicit val writes: OWrites[RetrieveSelfEmploymentBsasResponse] = writesFrom { case a: Def1_RetrieveSelfEmploymentBsasResponse =>
    implicitly[OWrites[Def1_RetrieveSelfEmploymentBsasResponse]].writes(a)
  }

}
