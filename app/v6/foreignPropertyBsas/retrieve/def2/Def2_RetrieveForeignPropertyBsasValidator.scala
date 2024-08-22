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

package v6.foreignPropertyBsas.retrieve.def2

import cats.data.Validated
import cats.data.Validated._
import cats.implicits._
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveCalculationId, ResolveNino, ResolveTysTaxYear, ResolverSupport}
import shared.models.errors.MtdError
import v6.foreignPropertyBsas.retrieve.def2.model.request.Def2_RetrieveForeignPropertyBsasRequestData
import v6.foreignPropertyBsas.retrieve.model.request.RetrieveForeignPropertyBsasRequestData

object Def2_RetrieveForeignPropertyBsasValidator extends ResolverSupport {
  private val resolveTysTaxYear = ResolveTysTaxYear.resolver.resolveOptionally
}

class Def2_RetrieveForeignPropertyBsasValidator(nino: String, calculationId: String, taxYear: Option[String])
    extends Validator[RetrieveForeignPropertyBsasRequestData] {
  import Def2_RetrieveForeignPropertyBsasValidator._

  def validate: Validated[Seq[MtdError], RetrieveForeignPropertyBsasRequestData] =
    (
      ResolveNino(nino),
      ResolveCalculationId(calculationId),
      resolveTysTaxYear(taxYear)
    ).mapN(Def2_RetrieveForeignPropertyBsasRequestData)

}
