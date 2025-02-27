/*
 * Copyright 2023 HM Revenue & Customs
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

package v6.ukPropertyBsas.retrieve

import cats.data.Validated.{Invalid, Valid}
import shared.controllers.validators.Validator
import v6.ukPropertyBsas.retrieve.RetrieveUkPropertyBsasSchema.{Def1, Def2}
import v6.ukPropertyBsas.retrieve.def1.Def1_RetrieveUkPropertyBsasValidator
import v6.ukPropertyBsas.retrieve.def2.Def2_RetrieveUkPropertyBsasValidator
import v6.ukPropertyBsas.retrieve.model.request.RetrieveUkPropertyBsasRequestData

import javax.inject.Singleton

@Singleton
class RetrieveUkPropertyBsasValidatorFactory {

  def validator(
      nino: String,
      calculationId: String,
      taxYear: String
  ): Validator[RetrieveUkPropertyBsasRequestData] = {

    RetrieveUkPropertyBsasSchema.schemaFor(taxYear) match {
      case Valid(Def1)     => new Def1_RetrieveUkPropertyBsasValidator(nino, calculationId, taxYear)
      case Valid(Def2)     => new Def2_RetrieveUkPropertyBsasValidator(nino, calculationId, taxYear)
      case Invalid(errors) => Validator.returningErrors(errors)
    }
  }

}
