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

package v7.ukPropertyBsas.retrieve.def3

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ResolveCalculationId, ResolveNino, ResolveTaxYear, ResolverSupport}
import api.models.errors.MtdError
import cats.data.Validated
import cats.implicits.*
import v7.ukPropertyBsas.retrieve.def3.model.request.Def3_RetrieveUkPropertyBsasRequestData
import v7.ukPropertyBsas.retrieve.model.request.RetrieveUkPropertyBsasRequestData

object Def3_RetrieveUkPropertyBsasValidator extends ResolverSupport {
  private val resolveTaxYear = ResolveTaxYear.resolver
}

class Def3_RetrieveUkPropertyBsasValidator(nino: String, calculationId: String, taxYear: String)
    extends Validator[RetrieveUkPropertyBsasRequestData] {
  import Def3_RetrieveUkPropertyBsasValidator.*

  def validate: Validated[Seq[MtdError], RetrieveUkPropertyBsasRequestData] =
    (
      ResolveNino(nino),
      ResolveCalculationId(calculationId),
      resolveTaxYear(taxYear)
    ).mapN(Def3_RetrieveUkPropertyBsasRequestData.apply)

}
