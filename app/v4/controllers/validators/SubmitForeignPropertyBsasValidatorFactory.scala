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
import shared.controllers.validators.resolvers._
import shared.models.domain.TaxYear
import shared.models.errors.{InvalidTaxYearParameterError, MtdError, RuleTaxYearNotSupportedError}
import v4.controllers.validators.SubmitForeignPropertyBsasRulesValidator.validateBusinessRules
import v4.controllers.validators.resolvers.ResolveOnlyOneJsonProperty
import v4.models.request.submitBsas.foreignProperty.{SubmitForeignPropertyBsasRequestBody, SubmitForeignPropertyBsasRequestData}

import javax.inject.Singleton

@Singleton
class SubmitForeignPropertyBsasValidatorFactory {

  private val resolveJson = new ResolveNonEmptyJsonObject[SubmitForeignPropertyBsasRequestBody]()

  private val resolveOnlyOneJsonProperty = new ResolveOnlyOneJsonProperty("foreignFhlEea", "nonFurnishedHolidayLet")

  private val minMaxTaxYears: (TaxYear, TaxYear) = (TaxYear.ending(2024), TaxYear.ending(2025))

  private val resolveTaxYear = ResolveTaxYearMinMax(
    minMaxTaxYears,
    minError = InvalidTaxYearParameterError,
    maxError = RuleTaxYearNotSupportedError
  ).resolver.resolveOptionally

  def validator(nino: String, calculationId: String, taxYear: Option[String], body: JsValue): Validator[SubmitForeignPropertyBsasRequestData] =
    new Validator[SubmitForeignPropertyBsasRequestData] {

      def validate: Validated[Seq[MtdError], SubmitForeignPropertyBsasRequestData] =
        resolveOnlyOneJsonProperty(body).productR(
          (
            ResolveNino(nino),
            ResolveCalculationId(calculationId),
            resolveTaxYear(taxYear),
            resolveJson(body)
          ).mapN(SubmitForeignPropertyBsasRequestData)
        ) andThen validateBusinessRules

    }

}
