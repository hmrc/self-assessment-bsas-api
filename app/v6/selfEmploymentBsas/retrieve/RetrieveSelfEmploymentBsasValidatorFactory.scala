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

package v6.selfEmploymentBsas.retrieve

import cats.data.Validated.{Invalid, Valid}
import shared.controllers.validators.Validator
import v6.selfEmploymentBsas.retrieve.RetrieveSelfEmploymentBsasSchema.{Def1, Def2}
import v6.selfEmploymentBsas.retrieve.def1.Def1_RetrieveSelfEmploymentBsasValidator
import v6.selfEmploymentBsas.retrieve.def2.Def2_RetrieveSelfEmploymentBsasValidator
import v6.selfEmploymentBsas.retrieve.model.request.RetrieveSelfEmploymentBsasRequestData

import javax.inject.Singleton

@Singleton
class RetrieveSelfEmploymentBsasValidatorFactory {

  def validator(
      nino: String,
      calculationId: String,
      taxYear: String
  ): Validator[RetrieveSelfEmploymentBsasRequestData] = {

    val schema = RetrieveSelfEmploymentBsasSchema.schemaFor(taxYear)

    schema match {
      case Valid(Def1)     => new Def1_RetrieveSelfEmploymentBsasValidator(nino, calculationId, taxYear)
      case Valid(Def2)     => new Def2_RetrieveSelfEmploymentBsasValidator(nino, calculationId, taxYear)
      case Invalid(errors) => Validator.returningErrors(errors)
    }
  }

}
