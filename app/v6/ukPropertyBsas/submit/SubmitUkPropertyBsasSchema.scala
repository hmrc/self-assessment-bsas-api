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

package v6.ukPropertyBsas.submit

import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear

import scala.math.Ordered.orderingToOrdered

sealed trait SubmitUkPropertyBsasSchema

object SubmitUkPropertyBsasSchema {

  case object Def1 extends SubmitUkPropertyBsasSchema
  case object Def2 extends SubmitUkPropertyBsasSchema

  private val defaultSchema = Def1

  def schemaFor(maybeTaxYear: Option[String]): SubmitUkPropertyBsasSchema = {
    maybeTaxYear
      .map(ResolveTaxYear.resolver)
      .flatMap(_.toOption.map(schemaFor))
      .getOrElse(defaultSchema)
  }

  def schemaFor(taxYear: TaxYear): SubmitUkPropertyBsasSchema = {
    if (taxYear <= TaxYear.starting(2023)) Def1
    else Def2
  }

}
