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

package v7.selfEmploymentBsas.submit.def2

import cats.data.Validated
import cats.implicits._
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers._
import shared.models.errors.MtdError
import v7.selfEmploymentBsas.submit.def2.model.request.{Def2_SubmitSelfEmploymentBsasRequestBody, Def2_SubmitSelfEmploymentBsasRequestData}
import v7.selfEmploymentBsas.submit.model.request.SubmitSelfEmploymentBsasRequestData

object Def2_SubmitSelfEmploymentBsasValidator extends ResolverSupport {
  private val resolveJson = ResolveNonEmptyJsonObject.resolver[Def2_SubmitSelfEmploymentBsasRequestBody]

  private val resolveTaxYear = ResolveTaxYear.resolver
}

class Def2_SubmitSelfEmploymentBsasValidator(
    nino: String,
    calculationId: String,
    taxYear: String,
    body: JsValue
) extends Validator[SubmitSelfEmploymentBsasRequestData] {

  import Def2_SubmitSelfEmploymentBsasValidator._

  def validate: Validated[Seq[MtdError], SubmitSelfEmploymentBsasRequestData] =
    (
      ResolveNino(nino),
      ResolveCalculationId(calculationId),
      resolveTaxYear(taxYear),
      resolveJson(body)
    ).mapN(Def2_SubmitSelfEmploymentBsasRequestData) andThen Def2_SubmitSelfEmploymentBsasRulesValidator.validateBusinessRules

}
