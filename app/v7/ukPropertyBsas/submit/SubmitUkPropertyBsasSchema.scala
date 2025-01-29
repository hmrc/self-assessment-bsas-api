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

package v7.ukPropertyBsas.submit

import cats.data.Validated
import cats.data.Validated.Valid
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import shared.models.errors.MtdError

import scala.math.Ordered.orderingToOrdered

sealed trait SubmitUkPropertyBsasSchema

object SubmitUkPropertyBsasSchema {

  case object Def1 extends SubmitUkPropertyBsasSchema
  case object Def2 extends SubmitUkPropertyBsasSchema
  case object Def3 extends SubmitUkPropertyBsasSchema

  def schemaFor(taxYearString: String): Validated[Seq[MtdError], SubmitUkPropertyBsasSchema] = {
    ResolveTaxYear(taxYearString) andThen schemaFor
  }

  def schemaFor(taxYear: TaxYear): Validated[Seq[MtdError], SubmitUkPropertyBsasSchema] = {
    if (taxYear <= TaxYear.starting(2023)) Valid(Def1)
    else if (taxYear == TaxYear.starting(2024)) Valid(Def2)
    else Valid(Def3)
  }

}
