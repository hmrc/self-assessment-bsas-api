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

package v7.ukPropertyBsas.submit.def2

import cats.data.Validated
import cats.implicits.*
import common.errors.RuleBothPropertiesSuppliedError
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.*
import shared.models.errors.MtdError
import v7.ukPropertyBsas.submit.def2.model.request.{Def2_SubmitUkPropertyBsasRequestBody, Def2_SubmitUkPropertyBsasRequestData}
import v7.ukPropertyBsas.submit.model.request.SubmitUkPropertyBsasRequestData

object Def2_SubmitUkPropertyBsasValidator extends ResolverSupport {

  private val resolveJson =
    new ResolveExclusiveJsonProperty(RuleBothPropertiesSuppliedError, "furnishedHolidayLet", "ukProperty").resolver.thenResolve(
      ResolveNonEmptyJsonObject.resolver[Def2_SubmitUkPropertyBsasRequestBody]
    )

  private val resolveTaxYear = ResolveTaxYear.resolver

}

class Def2_SubmitUkPropertyBsasValidator(nino: String, calculationId: String, taxYear: String, body: JsValue)
    extends Validator[SubmitUkPropertyBsasRequestData] {
  import Def2_SubmitUkPropertyBsasValidator.*

  def validate: Validated[Seq[MtdError], SubmitUkPropertyBsasRequestData] =
    (
      ResolveNino(nino),
      ResolveCalculationId(calculationId),
      resolveTaxYear(taxYear),
      resolveJson(body)
    ).mapN(Def2_SubmitUkPropertyBsasRequestData.apply) andThen Def2_SubmitUkPropertyBsasRulesValidator.validateBusinessRules

}
