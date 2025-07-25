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

package v5.bsas.list.def1

import cats.data.Validated
import cats.data.Validated.*
import cats.implicits.*
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino, ResolveTaxYearMinMax, ResolverSupport}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v5.bsas.list.def1.model.request.Def1_ListBsasRequestData
import v5.bsas.list.model.request.ListBsasRequestData
import v5.common.resolvers.ResolveTypeOfBusiness

object Def1_ListBsasValidator extends ResolverSupport {
  private val minMaxTaxYears: (TaxYear, TaxYear) = (TaxYear.fromMtd("2019-20"), TaxYear.ending(2025))
  private val resolveTaxYear                     = ResolveTaxYearMinMax(minMaxTaxYears).resolver.resolveOptionallyWithDefault(TaxYear.currentTaxYear)

  private val resolveBusinessId     = ResolveBusinessId.resolver.resolveOptionally
  private val resolveTypeOfBusiness = ResolveTypeOfBusiness.resolver.resolveOptionally
}

class Def1_ListBsasValidator(nino: String, taxYear: Option[String], typeOfBusiness: Option[String], businessId: Option[String])
    extends Validator[ListBsasRequestData] {
  import Def1_ListBsasValidator.*

  def validate: Validated[Seq[MtdError], ListBsasRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      resolveBusinessId(businessId),
      resolveTypeOfBusiness(typeOfBusiness).map(_.map(_.asDownstreamValue))
    ).mapN(Def1_ListBsasRequestData.apply)

}
