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

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ ResolveCalculationId, ResolveNino, ResolveTysTaxYear }
import api.models.errors.MtdError
import v3.models.request.retrieveBsas.RetrieveSelfEmploymentBsasRequestData

import javax.inject.Singleton

@Singleton
class RetrieveSelfEmploymentBsasValidatorFactory {

  def validator(nino: String, calculationId: String, taxYear: Option[String]): Validator[RetrieveSelfEmploymentBsasRequestData] =
    new Validator[RetrieveSelfEmploymentBsasRequestData] {

      def validate: Either[Seq[MtdError], RetrieveSelfEmploymentBsasRequestData] = {
        val resolvedNino          = ResolveNino(nino)
        val resolvedCalculationId = ResolveCalculationId(calculationId)
        val resolvedTaxYear       = ResolveTysTaxYear(taxYear)

        val result: Either[Seq[MtdError], RetrieveSelfEmploymentBsasRequestData] = for {
          nino          <- resolvedNino
          calculationId <- resolvedCalculationId
          maybeTaxYear  <- resolvedTaxYear
        } yield {
          RetrieveSelfEmploymentBsasRequestData(nino, calculationId, maybeTaxYear)
        }

        mapResult(result, possibleErrors = resolvedNino, resolvedCalculationId, resolvedTaxYear)
      }
    }
}
