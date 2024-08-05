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

package v4.controllers.validators

import cats.data.Validated
import cats.implicits._
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.ResolverSupport._
import shared.controllers.validators.resolvers.{ResolveCalculationId, ResolveNino, ResolveNonEmptyJsonObject, ResolveTysTaxYear}
import shared.models.errors.MtdError
import v4.controllers.validators.SubmitUkPropertyBsasRulesValidator.validateBusinessRules
import v4.controllers.validators.resolvers.ResolveOnlyOneJsonProperty
import v4.models.request.submitBsas.ukProperty.{SubmitUKPropertyBsasRequestBody, SubmitUkPropertyBsasRequestData}

import javax.inject.Singleton

@Singleton
class SubmitUkPropertyBsasValidatorFactory {

  private val resolveJson = new ResolveNonEmptyJsonObject[SubmitUKPropertyBsasRequestBody]()

  private val resolveOnlyOneJsonProperty = new ResolveOnlyOneJsonProperty("furnishedHolidayLet", "nonFurnishedHolidayLet")

  private val resolveTysTaxYear = ResolveTysTaxYear.resolver.resolveOptionally

  def validator(nino: String, calculationId: String, taxYear: Option[String], body: JsValue): Validator[SubmitUkPropertyBsasRequestData] =
    new Validator[SubmitUkPropertyBsasRequestData] {

      def validate: Validated[Seq[MtdError], SubmitUkPropertyBsasRequestData] =
        resolveOnlyOneJsonProperty(body).productR(
          (
            ResolveNino(nino),
            ResolveCalculationId(calculationId),
            resolveTysTaxYear(taxYear),
            resolveJson(body)
          ).mapN(SubmitUkPropertyBsasRequestData)
        ) andThen validateBusinessRules

    }

}
