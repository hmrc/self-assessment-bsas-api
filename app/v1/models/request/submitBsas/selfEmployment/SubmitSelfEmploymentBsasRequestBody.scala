/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.request.submitBsas.selfEmployment

import play.api.libs.json.{Json, OWrites, Reads}

case class SubmitSelfEmploymentBsasRequestBody(income: Option[Income], additions: Option[Additions], expenses: Option[Expenses])

object SubmitSelfEmploymentBsasRequestBody {
  implicit val reads: Reads[SubmitSelfEmploymentBsasRequestBody] = Json.reads[SubmitSelfEmploymentBsasRequestBody]
  implicit val writes: OWrites[SubmitSelfEmploymentBsasRequestBody] = Json.writes[SubmitSelfEmploymentBsasRequestBody]
}