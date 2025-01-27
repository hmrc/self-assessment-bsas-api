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

package v7.bsas.trigger.def2

import cats.data.Validated
import cats.implicits._
import config.BsasConfig
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveNonEmptyJsonObject, ResolverSupport}
import shared.models.errors.MtdError
import v7.bsas.trigger.def2.model.request.{Def2_TriggerBsasRequestBody, Def2_TriggerBsasRequestData}
import v7.bsas.trigger.model.TriggerBsasRequestData

object Def2_TriggerBsasValidator extends ResolverSupport {
  private val resolveJson = ResolveNonEmptyJsonObject.resolver[Def2_TriggerBsasRequestBody]
}

class Def2_TriggerBsasValidator(
    nino: String,
    body: JsValue
)(implicit bsasConfig: BsasConfig)
    extends Validator[TriggerBsasRequestData] {

  import Def2_TriggerBsasValidator._

  lazy private val rulesValidator = new Def2_TriggerBsasRulesValidator

  def validate: Validated[Seq[MtdError], TriggerBsasRequestData] = {
    (
      ResolveNino(nino),
      resolveJson(body)
    ).mapN(Def2_TriggerBsasRequestData) andThen rulesValidator.validateBusinessRules
  }

}
