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

package v6.bsas.trigger.def2.model.response

import play.api.libs.json._
import v6.bsas.trigger.model.TriggerBsasResponse

case class Def2_TriggerBsasResponse(calculationId: String) extends TriggerBsasResponse

object Def2_TriggerBsasResponse {

  implicit val writes: OWrites[Def2_TriggerBsasResponse] = Json.writes[Def2_TriggerBsasResponse]

  implicit val reads: Reads[Def2_TriggerBsasResponse] =
    (JsPath \ "metadata" \ "calculationId").read[String].map(Def2_TriggerBsasResponse.apply)

}
