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

package v5.ukPropertyBsas.submit.def1

import cats.data.Validated
import cats.implicits.*
import common.errors.RuleBothPropertiesSuppliedError
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.*
import shared.models.domain.TaxYear
import shared.models.errors.{InvalidTaxYearParameterError, MtdError, RuleTaxYearNotSupportedError}
import v5.ukPropertyBsas.submit.def1.model.request.{Def1_SubmitUkPropertyBsasRequestBody, Def1_SubmitUkPropertyBsasRequestData}
import v5.ukPropertyBsas.submit.model.request.SubmitUkPropertyBsasRequestData

object Def1_SubmitUkPropertyBsasValidator extends ResolverSupport {

  private val resolveJson =
    new ResolveExclusiveJsonProperty(RuleBothPropertiesSuppliedError, "furnishedHolidayLet", "nonFurnishedHolidayLet").resolver thenResolve
      ResolveNonEmptyJsonObject.resolver[Def1_SubmitUkPropertyBsasRequestBody]

  private val minMaxTaxYears: (TaxYear, TaxYear) = (TaxYear.ending(2024), TaxYear.ending(2025))

  private val resolveTaxYear = ResolveTaxYearMinMax(
    minMaxTaxYears,
    minError = InvalidTaxYearParameterError,
    maxError = RuleTaxYearNotSupportedError
  ).resolver.resolveOptionally

}

class Def1_SubmitUkPropertyBsasValidator(nino: String, calculationId: String, taxYear: Option[String], body: JsValue)
    extends Validator[SubmitUkPropertyBsasRequestData] {
  import Def1_SubmitUkPropertyBsasValidator.*

  def validate: Validated[Seq[MtdError], SubmitUkPropertyBsasRequestData] =
    (
      ResolveNino(nino),
      ResolveCalculationId(calculationId),
      resolveTaxYear(taxYear),
      resolveJson(body)
    ).mapN(Def1_SubmitUkPropertyBsasRequestData) andThen Def1_SubmitUkPropertyBsasRulesValidator.validateBusinessRules

}
