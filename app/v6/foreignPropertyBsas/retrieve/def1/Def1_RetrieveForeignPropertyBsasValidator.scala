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

package v6.foreignPropertyBsas.retrieve.def1

import cats.data.Validated
import cats.data.Validated.*
import cats.implicits.*
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveCalculationId, ResolveNino, ResolveTaxYear, ResolverSupport}
import shared.models.errors.MtdError
import v6.foreignPropertyBsas.retrieve.def1.model.request.Def1_RetrieveForeignPropertyBsasRequestData
import v6.foreignPropertyBsas.retrieve.model.request.RetrieveForeignPropertyBsasRequestData

object Def1_RetrieveForeignPropertyBsasValidator extends ResolverSupport {
  private val resolveTaxYear = ResolveTaxYear.resolver
}

class Def1_RetrieveForeignPropertyBsasValidator(nino: String, calculationId: String, taxYear: String)
    extends Validator[RetrieveForeignPropertyBsasRequestData] {
  import Def1_RetrieveForeignPropertyBsasValidator.*

  def validate: Validated[Seq[MtdError], RetrieveForeignPropertyBsasRequestData] =
    (
      ResolveNino(nino),
      ResolveCalculationId(calculationId),
      resolveTaxYear(taxYear)
    ).mapN(Def1_RetrieveForeignPropertyBsasRequestData.apply)

}
