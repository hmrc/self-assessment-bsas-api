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

package v7.selfEmploymentBsas.submit

import cats.data.Validated
import cats.data.Validated.Valid
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.errors.MtdError

sealed trait SubmitSelfEmploymentBsasSchema

object SubmitSelfEmploymentBsasSchema {

  case object Def1 extends SubmitSelfEmploymentBsasSchema

  def schemaFor(taxYearString: String): Validated[Seq[MtdError], SubmitSelfEmploymentBsasSchema] = {
    ResolveTaxYear(taxYearString) andThen (_ => schemaFor())
  }

  def schemaFor(): Validated[Seq[MtdError], SubmitSelfEmploymentBsasSchema] = {
    Valid(Def1)
  }

}
