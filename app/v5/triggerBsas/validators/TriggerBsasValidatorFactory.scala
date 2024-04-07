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

package v5.triggerBsas.validators

import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import v5.triggerBsas.models.TriggerBsasRequestData
import v5.triggerBsas.schema.TriggerSchema
import v5.triggerBsas.schema.TriggerSchema._
import v5.triggerBsas.validators.def1.{Def1_TriggerBsasRulesValidator, Def1_TriggerBsasValidator}

import javax.inject.{Inject, Singleton}

@Singleton
class TriggerBsasValidatorFactory @Inject() (def1_TriggerBsasRulesValidator: Def1_TriggerBsasRulesValidator) {

  def validator(nino: String, body: JsValue, schema: TriggerSchema): Validator[TriggerBsasRequestData] =
    schema match {
      case Def1 => new Def1_TriggerBsasValidator(def1_TriggerBsasRulesValidator)(nino, body)
    }

}
