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

package v5.selfEmploymentBsas.retrieve

import shared.controllers.validators.Validator
import v5.selfEmploymentBsas.retrieve.RetrieveSelfEmploymentBsasSchema.Def1
import v5.selfEmploymentBsas.retrieve.def1.Def1_RetrieveSelfEmploymentBsasValidator
import v5.selfEmploymentBsas.retrieve.model.request.RetrieveSelfEmploymentBsasRequestData

import javax.inject.Singleton

@Singleton
class RetrieveSelfEmploymentBsasValidatorFactory {

  def validator(nino: String,
                calculationId: String,
                taxYear: Option[String],
                schema: RetrieveSelfEmploymentBsasSchema): Validator[RetrieveSelfEmploymentBsasRequestData] =
    schema match {
      case Def1 => new Def1_RetrieveSelfEmploymentBsasValidator(nino, calculationId, taxYear)
    }

}
