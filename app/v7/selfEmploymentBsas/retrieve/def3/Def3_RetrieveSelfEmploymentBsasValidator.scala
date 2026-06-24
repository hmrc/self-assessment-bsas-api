/*
 * Copyright 2026 HM Revenue & Customs
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

package v7.selfEmploymentBsas.retrieve.def3

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ResolveCalculationId, ResolveNino, ResolveTaxYear, ResolverSupport}
import api.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.*
import cats.implicits.*
import v7.selfEmploymentBsas.retrieve.def3.model.request.Def3_RetrieveSelfEmploymentBsasRequestData
import v7.selfEmploymentBsas.retrieve.model.request.RetrieveSelfEmploymentBsasRequestData

object Def3_RetrieveSelfEmploymentBsasValidator extends ResolverSupport {
  private val resolveTaxYear = ResolveTaxYear.resolver
}

class Def3_RetrieveSelfEmploymentBsasValidator(
    nino: String,
    calculationId: String,
    taxYear: String
) extends Validator[RetrieveSelfEmploymentBsasRequestData] {

  import Def3_RetrieveSelfEmploymentBsasValidator.*

  def validate: Validated[Seq[MtdError], RetrieveSelfEmploymentBsasRequestData] =
    (
      ResolveNino(nino),
      ResolveCalculationId(calculationId),
      resolveTaxYear(taxYear)
    ).mapN(Def3_RetrieveSelfEmploymentBsasRequestData.apply)

}
