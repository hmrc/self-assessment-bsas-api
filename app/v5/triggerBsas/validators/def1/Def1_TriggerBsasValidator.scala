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

package v5.triggerBsas.validators.def1

import cats.data.Validated
import cats.implicits._
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveNonEmptyJsonObject}
import shared.models.errors.MtdError
import v5.triggerBsas.models.TriggerBsasRequestData
import v5.triggerBsas.models.def1.{Def1_TriggerBsasRequestBody, Def1_TriggerBsasRequestData}

class Def1_TriggerBsasValidator(rulesValidator: Def1_TriggerBsasRulesValidator)(nino: String, body: JsValue)
    extends Validator[TriggerBsasRequestData] {
  private val resolveJson = new ResolveNonEmptyJsonObject[Def1_TriggerBsasRequestBody]()

  def validate: Validated[Seq[MtdError], TriggerBsasRequestData] = {
    (
      ResolveNino(nino),
      resolveJson(body)
    ).mapN(Def1_TriggerBsasRequestData) andThen rulesValidator.validateBusinessRules
  }

}