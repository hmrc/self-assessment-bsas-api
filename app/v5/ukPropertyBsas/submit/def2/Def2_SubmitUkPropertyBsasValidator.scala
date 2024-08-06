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

package v5.ukPropertyBsas.submit.def2

import cats.data.Validated
import cats.implicits.catsSyntaxTuple4Semigroupal
import common.errors.RuleBothPropertiesSuppliedError
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers._
import shared.models.domain.TaxYear
import shared.models.errors.{InvalidTaxYearParameterError, MtdError}
import v5.ukPropertyBsas.submit.def2.model.request.{Def2_SubmitUkPropertyBsasRequestBody, Def2_SubmitUkPropertyBsasRequestData}
import v5.ukPropertyBsas.submit.model.request.SubmitUkPropertyBsasRequestData

object Def2_SubmitUkPropertyBsasValidator extends ResolverSupport {

  private val resolveJson =
    new ResolveExclusiveJsonProperty(RuleBothPropertiesSuppliedError, "furnishedHolidayLet", "nonFurnishedHolidayLet").resolver thenResolve
      ResolveNonEmptyJsonObject.resolver[Def2_SubmitUkPropertyBsasRequestBody]

  private val minMaxTaxYears: (TaxYear, TaxYear) = (TaxYear.ending(2024), TaxYear.ending(2025))
  private val resolveTaxYear = ResolveTaxYearMinMax(minMaxTaxYears, minError = InvalidTaxYearParameterError).resolver.resolveOptionally

}

class Def2_SubmitUkPropertyBsasValidator(nino: String, calculationId: String, taxYear: Option[String], body: JsValue)
    extends Validator[SubmitUkPropertyBsasRequestData] {
  import Def2_SubmitUkPropertyBsasValidator._

  def validate: Validated[Seq[MtdError], SubmitUkPropertyBsasRequestData] =
    (
      ResolveNino(nino),
      ResolveCalculationId(calculationId),
      resolveTaxYear(taxYear),
      resolveJson(body)
    ).mapN(Def2_SubmitUkPropertyBsasRequestData) andThen Def2_SubmitUkPropertyBsasRulesValidator.validateBusinessRules

}
