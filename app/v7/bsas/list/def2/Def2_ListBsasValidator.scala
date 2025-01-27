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

package v7.bsas.list.def2

import cats.data.Validated
import cats.data.Validated._
import cats.implicits._
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino, ResolveTaxYear, ResolverSupport}
import shared.models.domain.TaxYear
import shared.models.errors.{MtdError, RuleTaxYearNotSupportedError}
import v7.bsas.list.def2.model.request.Def2_ListBsasRequestData
import v7.bsas.list.model.request.ListBsasRequestData
import v7.common.resolvers.ResolveTypeOfBusiness

object Def2_ListBsasValidator extends ResolverSupport {
  private val listMinimumTaxYear = TaxYear.fromMtd("2025-26")

  private val resolveTaxYear = ResolveTaxYear.resolver thenValidate
    satisfiesMin(listMinimumTaxYear, RuleTaxYearNotSupportedError)

  private val resolveBusinessId     = ResolveBusinessId.resolver.resolveOptionally
  private val resolveTypeOfBusiness = ResolveTypeOfBusiness.resolver.resolveOptionally
}

class Def2_ListBsasValidator(nino: String, taxYear: String, typeOfBusiness: Option[String], businessId: Option[String])
    extends Validator[ListBsasRequestData] {
  import Def2_ListBsasValidator._

  def validate: Validated[Seq[MtdError], ListBsasRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      resolveBusinessId(businessId),
      resolveTypeOfBusiness(typeOfBusiness).map(_.map(_.asDownstreamValue))
    ).mapN(Def2_ListBsasRequestData)

}
