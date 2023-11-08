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

import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ ResolveCalculationId, ResolveNino, ResolveNonEmptyJsonObject, ResolveTysTaxYear }
import shared.models.errors.MtdError
import cats.data.Validated
import cats.implicits._
import play.api.libs.json.JsValue
import v3.controllers.validators.SubmitSelfEmploymentBsasRulesValidator.validateBusinessRules
import v3.models.request.submitBsas.selfEmployment.{ SubmitSelfEmploymentBsasRequestBody, SubmitSelfEmploymentBsasRequestData }

import javax.inject.Singleton
import scala.annotation.nowarn
import shared.controllers.validators.resolvers.ResolverSupport._

@Singleton
class SubmitSelfEmploymentBsasValidatorFactory {

  @nowarn("cat=lint-byname-implicit")
  private val resolveJson = new ResolveNonEmptyJsonObject[SubmitSelfEmploymentBsasRequestBody]()

  private val resolveTysTaxYear = ResolveTysTaxYear.resolver.resolveOptionally

  def validator(nino: String, calculationId: String, taxYear: Option[String], body: JsValue): Validator[SubmitSelfEmploymentBsasRequestData] =
    new Validator[SubmitSelfEmploymentBsasRequestData] {

      def validate: Validated[Seq[MtdError], SubmitSelfEmploymentBsasRequestData] =
        (
          ResolveNino(nino),
          ResolveCalculationId(calculationId),
          resolveTysTaxYear(taxYear),
          resolveJson(body)
        ).mapN(SubmitSelfEmploymentBsasRequestData) andThen validateBusinessRules
    }
}
