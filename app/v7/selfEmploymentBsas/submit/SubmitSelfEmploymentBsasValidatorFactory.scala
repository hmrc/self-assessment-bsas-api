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

import cats.data.Validated.{Invalid, Valid}
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import v7.selfEmploymentBsas.submit.SubmitSelfEmploymentBsasSchema.{Def1, Def2}
import v7.selfEmploymentBsas.submit.def1.Def1_SubmitSelfEmploymentBsasValidator
import v7.selfEmploymentBsas.submit.def2.Def2_SubmitSelfEmploymentBsasValidator
import v7.selfEmploymentBsas.submit.model.request.SubmitSelfEmploymentBsasRequestData

import javax.inject.Singleton

@Singleton
class SubmitSelfEmploymentBsasValidatorFactory {

  def validator(nino: String, calculationId: String, taxYear: String, body: JsValue): Validator[SubmitSelfEmploymentBsasRequestData] = {

    val schema = SubmitSelfEmploymentBsasSchema.schemaFor(taxYear)

    schema match {
      case Valid(Def1)     => new Def1_SubmitSelfEmploymentBsasValidator(nino, calculationId, taxYear, body)
      case Valid(Def2)     => new Def2_SubmitSelfEmploymentBsasValidator(nino, calculationId, taxYear, body)
      case Invalid(errors) => Validator.returningErrors(errors)
    }
  }

}
