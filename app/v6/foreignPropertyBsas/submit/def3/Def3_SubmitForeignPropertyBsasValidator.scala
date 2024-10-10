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

package v6.foreignPropertyBsas.submit.def3

import cats.data.Validated
import cats.implicits._
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers._
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v6.foreignPropertyBsas.submit.def3.model.request.{Def3_SubmitForeignPropertyBsasRequestBody, Def3_SubmitForeignPropertyBsasRequestData}
import v6.foreignPropertyBsas.submit.model.request.SubmitForeignPropertyBsasRequestData

object Def3_SubmitForeignPropertyBsasValidator extends ResolverSupport {

  private val resolveJson = ResolveNonEmptyJsonObject.resolver[Def3_SubmitForeignPropertyBsasRequestBody]

  private val resolveTaxYear = ResolveTaxYear.resolver.resolveOptionallyWithDefault(TaxYear.currentTaxYear)
}

class Def3_SubmitForeignPropertyBsasValidator(nino: String, calculationId: String, taxYear: String, body: JsValue)
    extends Validator[SubmitForeignPropertyBsasRequestData] {
  import Def3_SubmitForeignPropertyBsasValidator._

  def validate: Validated[Seq[MtdError], SubmitForeignPropertyBsasRequestData] =
    (
      ResolveNino(nino),
      ResolveCalculationId(calculationId),
      resolveTaxYear(Some(taxYear)),
      resolveJson(body)
    ).mapN(Def3_SubmitForeignPropertyBsasRequestData) andThen Def3_SubmitForeignPropertyBsasRulesValidator.validateBusinessRules

}
