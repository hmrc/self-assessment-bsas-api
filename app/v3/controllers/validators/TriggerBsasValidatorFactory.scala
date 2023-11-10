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

package v3.controllers.validators

import cats.data.Validated
import cats.implicits._
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers._
import shared.models.errors.MtdError
import v3.models.request.triggerBsas.{TriggerBsasRequestBody, TriggerBsasRequestData}

import javax.inject.{Inject, Singleton}

@Singleton
class TriggerBsasValidatorFactory @Inject() (rulesValidator: TriggerBsasRulesValidator) {

  private val resolveJson = new ResolveNonEmptyJsonObject[TriggerBsasRequestBody]()

  def validator(nino: String, body: JsValue): Validator[TriggerBsasRequestData] =
    new Validator[TriggerBsasRequestData] {

      def validate: Validated[Seq[MtdError], TriggerBsasRequestData] = {
        (
          ResolveNino(nino),
          resolveJson(body)
        ).mapN(TriggerBsasRequestData) andThen rulesValidator.validateBusinessRules
      }

    }

}
