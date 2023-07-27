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
import api.controllers.validators.resolvers.{ ResolveBusinessId, ResolveNino }
import api.models.domain.TaxYear
import api.models.errors.MtdError
import v3.controllers.validators.resolvers.{ ResolveListMinimumTaxYear, ResolveTypeOfBusiness }
import v3.models.request.ListBsasRequestData

import javax.inject.Singleton

@Singleton
class ListBsasValidatorFactory {

  def validator(nino: String, taxYear: Option[String], typeOfBusiness: Option[String], businessId: Option[String]): Validator[ListBsasRequestData] =
    new Validator[ListBsasRequestData] {

      def validate: Either[Seq[MtdError], ListBsasRequestData] = {
        val resolvedNino           = ResolveNino(nino)
        val resolvedTaxYear        = ResolveListMinimumTaxYear(taxYear)
        val resolvedTypeOfBusiness = ResolveTypeOfBusiness(typeOfBusiness)
        val resolvedBusinessId     = ResolveBusinessId(businessId)

        val result: Either[Seq[MtdError], ListBsasRequestData] = for {
          nino                  <- resolvedNino
          maybeTaxYear          <- resolvedTaxYear
          maybeIncomeSourceType <- resolvedTypeOfBusiness
          maybeIncomeSourceId   <- resolvedBusinessId
        } yield {
          val taxYear                         = maybeTaxYear.getOrElse(TaxYear.currentTaxYear())
          val maybeIncomeSourceTypeIdentifier = maybeIncomeSourceType.map(_.toIdentifierValue)

          ListBsasRequestData(nino, taxYear, maybeIncomeSourceId, maybeIncomeSourceTypeIdentifier)
        }

        mapResult(result, possibleErrors = resolvedNino, resolvedTaxYear, resolvedTypeOfBusiness, resolvedBusinessId)
      }
    }

}
