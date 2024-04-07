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

package v5.retrieveUkPropertyBsas.validators

import shared.controllers.validators.Validator
import v5.retrieveUkPropertyBsas.models.RetrieveUkPropertyBsasRequestData
import v5.retrieveUkPropertyBsas.schema.RetrieveUkPropertyBsasSchema
import v5.retrieveUkPropertyBsas.schema.RetrieveUkPropertyBsasSchema._
import v5.retrieveUkPropertyBsas.validators.def1.Def1_RetrieveUkPropertyBsasValidator

import javax.inject.Singleton

@Singleton
class RetrieveUkPropertyBsasValidatorFactory {

  def validator(nino: String,
                calculationId: String,
                taxYear: Option[String],
                schema: RetrieveUkPropertyBsasSchema): Validator[RetrieveUkPropertyBsasRequestData] =
    schema match {
      case Def1 => new Def1_RetrieveUkPropertyBsasValidator(nino, calculationId, taxYear)
    }

}
