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

package v6.bsas.list.def1

import cats.data.Validated
import cats.data.Validated._
import cats.implicits._
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino, ResolveTaxYear, ResolverSupport}
import shared.models.domain.TaxYear
import shared.models.errors.{MtdError, RuleTaxYearNotSupportedError}
import v6.bsas.list.def1.model.request.Def1_ListBsasRequestData
import v6.bsas.list.model.request.ListBsasRequestData
import v6.common.resolvers.ResolveTypeOfBusinessWithFHL

object Def1_ListBsasValidator extends ResolverSupport {
  private val listMinimumTaxYear = TaxYear.fromMtd("2019-20")

  private val resolveTaxYear = ResolveTaxYear.resolver thenValidate
    satisfiesMin(listMinimumTaxYear, RuleTaxYearNotSupportedError)

  private val resolveBusinessId            = ResolveBusinessId.resolver.resolveOptionally
  private val resolveTypeOfBusinessWithFHL = ResolveTypeOfBusinessWithFHL.resolver.resolveOptionally
}

class Def1_ListBsasValidator(nino: String, taxYear: String, typeOfBusiness: Option[String], businessId: Option[String])
    extends Validator[ListBsasRequestData] {
  import Def1_ListBsasValidator._

  def validate: Validated[Seq[MtdError], ListBsasRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      resolveBusinessId(businessId),
      resolveTypeOfBusinessWithFHL(typeOfBusiness).map(_.map(_.asDownstreamValue))
    ).mapN(Def1_ListBsasRequestData)

}
