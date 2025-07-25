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

package v5.foreignPropertyBsas.submit

import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import v5.foreignPropertyBsas.submit.SubmitForeignPropertyBsasSchema.*
import v5.foreignPropertyBsas.submit.def1.Def1_SubmitForeignPropertyBsasValidator
import v5.foreignPropertyBsas.submit.def2.Def2_SubmitForeignPropertyBsasValidator
import v5.foreignPropertyBsas.submit.model.request.SubmitForeignPropertyBsasRequestData

import javax.inject.Singleton

@Singleton
class SubmitForeignPropertyBsasValidatorFactory {

  def validator(
      nino: String,
      calculationId: String,
      taxYear: Option[String],
      body: JsValue
  ): Validator[SubmitForeignPropertyBsasRequestData] = {

    val schema = SubmitForeignPropertyBsasSchema.schemaFor(taxYear)

    schema match {
      case Def1 => new Def1_SubmitForeignPropertyBsasValidator(nino, calculationId, taxYear, body)
      case Def2 => new Def2_SubmitForeignPropertyBsasValidator(nino, calculationId, taxYear, body)
    }

  }

}
