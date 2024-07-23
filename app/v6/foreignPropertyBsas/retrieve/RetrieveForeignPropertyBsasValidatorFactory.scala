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

package v6.foreignPropertyBsas.retrieve

import shared.controllers.validators.Validator
import v6.foreignPropertyBsas.retrieve.RetrieveForeignPropertyBsasSchema._
import v6.foreignPropertyBsas.retrieve.def1.Def1_RetrieveForeignPropertyBsasValidator
import v6.foreignPropertyBsas.retrieve.model.request.RetrieveForeignPropertyBsasRequestData

import javax.inject.Singleton

@Singleton
class RetrieveForeignPropertyBsasValidatorFactory {

  def validator(
      nino: String,
      calculationId: String,
      taxYear: Option[String]
  ): Validator[RetrieveForeignPropertyBsasRequestData] = {

    val schema = RetrieveForeignPropertyBsasSchema.schemaFor(taxYear)

    schema match {
      case Def1 => new Def1_RetrieveForeignPropertyBsasValidator(nino, calculationId, taxYear)
    }

  }

}
