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

package v5.selfEmploymentBsas.submit.def1

import cats.data.Validated
import cats.implicits._
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers._
import shared.models.errors.MtdError
import v5.selfEmploymentBsas.submit.def1.model.request.{Def1_SubmitSelfEmploymentBsasRequestBody, Def1_SubmitSelfEmploymentBsasRequestData}
import v5.selfEmploymentBsas.submit.model.request.SubmitSelfEmploymentBsasRequestData

object Def1_SubmitSelfEmploymentBsasValidator extends ResolverSupport {
  private val resolveJson = ResolveNonEmptyJsonObject.resolver[Def1_SubmitSelfEmploymentBsasRequestBody]

  private val resolveTysTaxYear = ResolveTysTaxYear.resolver.resolveOptionally
}

class Def1_SubmitSelfEmploymentBsasValidator(
    nino: String,
    calculationId: String,
    taxYear: Option[String],
    body: JsValue
) extends Validator[SubmitSelfEmploymentBsasRequestData] {

  import Def1_SubmitSelfEmploymentBsasValidator._

  def validate: Validated[Seq[MtdError], SubmitSelfEmploymentBsasRequestData] =
    (
      ResolveNino(nino),
      ResolveCalculationId(calculationId),
      resolveTysTaxYear(taxYear),
      resolveJson(body)
    ).mapN(Def1_SubmitSelfEmploymentBsasRequestData) andThen Def1_SubmitSelfEmploymentBsasRulesValidator.validateBusinessRules

}
