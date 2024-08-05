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
import cats.data.Validated._
import cats.implicits._
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.ResolverSupport._
import shared.controllers.validators.resolvers.{ResolveCalculationId, ResolveNino, ResolveTaxYearMinMax}
import shared.models.domain.TaxYear
import shared.models.errors.{InvalidTaxYearParameterError, MtdError}
import v4.models.request.retrieveBsas.RetrieveSelfEmploymentBsasRequestData

import javax.inject.Singleton

@Singleton
class RetrieveSelfEmploymentBsasValidatorFactory {

  private val minMaxTaxYears: (TaxYear, TaxYear) = (TaxYear.ending(2024), TaxYear.ending(2025))
  private val resolveTaxYear = ResolveTaxYearMinMax(minMaxTaxYears, minError = InvalidTaxYearParameterError).resolver.resolveOptionally

  def validator(nino: String, calculationId: String, taxYear: Option[String]): Validator[RetrieveSelfEmploymentBsasRequestData] =
    new Validator[RetrieveSelfEmploymentBsasRequestData] {

      def validate: Validated[Seq[MtdError], RetrieveSelfEmploymentBsasRequestData] =
        (
          ResolveNino(nino),
          ResolveCalculationId(calculationId),
          resolveTaxYear(taxYear)
        ).mapN(RetrieveSelfEmploymentBsasRequestData)

    }

}
