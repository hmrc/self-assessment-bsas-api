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

import cats.data.Validated
import cats.implicits._
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.ResolverSupport._
import shared.controllers.validators.resolvers.{ResolveCalculationId, ResolveNino, ResolveTysTaxYear}
import shared.models.errors.MtdError
import v3.models.request.retrieveBsas.RetrieveUkPropertyBsasRequestData

import javax.inject.Singleton

@Singleton
class RetrieveUkPropertyBsasValidatorFactory {

  private val resolveTysTaxYear = ResolveTysTaxYear.resolver.resolveOptionally

  def validator(nino: String, calculationId: String, taxYear: Option[String]): Validator[RetrieveUkPropertyBsasRequestData] =
    new Validator[RetrieveUkPropertyBsasRequestData] {

      def validate: Validated[Seq[MtdError], RetrieveUkPropertyBsasRequestData] =
        (
          ResolveNino(nino),
          ResolveCalculationId(calculationId),
          resolveTysTaxYear(taxYear)
        ).mapN(RetrieveUkPropertyBsasRequestData)

    }

}
