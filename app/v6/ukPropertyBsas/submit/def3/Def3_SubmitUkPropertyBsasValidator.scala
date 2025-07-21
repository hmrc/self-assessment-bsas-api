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

package v6.ukPropertyBsas.submit.def3

import cats.data.Validated
import cats.implicits.*
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.*
import shared.models.errors.MtdError
import v6.ukPropertyBsas.submit.def3.model.request.{Def3_SubmitUkPropertyBsasRequestBody, Def3_SubmitUkPropertyBsasRequestData}
import v6.ukPropertyBsas.submit.model.request.SubmitUkPropertyBsasRequestData

object Def3_SubmitUkPropertyBsasValidator extends ResolverSupport {

  private val resolveJson = ResolveNonEmptyJsonObject.resolver[Def3_SubmitUkPropertyBsasRequestBody]

  private val resolveTaxYear = ResolveTaxYear.resolver

}

class Def3_SubmitUkPropertyBsasValidator(nino: String, calculationId: String, taxYear: String, body: JsValue)
    extends Validator[SubmitUkPropertyBsasRequestData] {
  import Def3_SubmitUkPropertyBsasValidator.*

  def validate: Validated[Seq[MtdError], SubmitUkPropertyBsasRequestData] =
    (
      ResolveNino(nino),
      ResolveCalculationId(calculationId),
      resolveTaxYear(taxYear),
      resolveJson(body)
    ).mapN(Def3_SubmitUkPropertyBsasRequestData.apply) andThen Def3_SubmitUkPropertyBsasRulesValidator.validateBusinessRules

}
