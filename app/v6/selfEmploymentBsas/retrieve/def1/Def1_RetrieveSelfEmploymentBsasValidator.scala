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

package v6.selfEmploymentBsas.retrieve.def1

import cats.data.Validated
import cats.data.Validated._
import cats.implicits._
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveCalculationId, ResolveNino, ResolveTysTaxYear, ResolverSupport}
import shared.models.errors.MtdError
import v6.selfEmploymentBsas.retrieve.def1.model.request.Def1_RetrieveSelfEmploymentBsasRequestData
import v6.selfEmploymentBsas.retrieve.model.request.RetrieveSelfEmploymentBsasRequestData

object Def1_RetrieveSelfEmploymentBsasValidator extends ResolverSupport {
  private val resolveTysTaxYear = ResolveTysTaxYear.resolver.resolveOptionally
}

class Def1_RetrieveSelfEmploymentBsasValidator(
    nino: String,
    calculationId: String,
    taxYear: Option[String]
) extends Validator[RetrieveSelfEmploymentBsasRequestData] {

  import Def1_RetrieveSelfEmploymentBsasValidator._

  def validate: Validated[Seq[MtdError], RetrieveSelfEmploymentBsasRequestData] =
    (
      ResolveNino(nino),
      ResolveCalculationId(calculationId),
      resolveTysTaxYear(taxYear)
    ).mapN(Def1_RetrieveSelfEmploymentBsasRequestData)

}
