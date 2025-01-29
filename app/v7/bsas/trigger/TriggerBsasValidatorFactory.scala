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

package v7.bsas.trigger

import cats.data.Validated.{Invalid, Valid}
import config.BsasConfig
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import v7.bsas.trigger.TriggerSchema.{Def1, Def2}
import v7.bsas.trigger.def1.Def1_TriggerBsasValidator
import v7.bsas.trigger.def2.Def2_TriggerBsasValidator
import v7.bsas.trigger.model.TriggerBsasRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class TriggerBsasValidatorFactory @Inject() (implicit bsasConfig: BsasConfig) {

  def validator(nino: String, body: JsValue): Validator[TriggerBsasRequestData] = {

    TriggerSchema.schemaFor(body) match {
      case Valid(Def1)     => new Def1_TriggerBsasValidator(nino, body)
      case Valid(Def2)     => new Def2_TriggerBsasValidator(nino, body)
      case Invalid(errors) => Validator.returningErrors(errors)
    }
  }

}
