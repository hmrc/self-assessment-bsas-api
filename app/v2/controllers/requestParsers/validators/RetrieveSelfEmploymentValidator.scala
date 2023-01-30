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

package v2.controllers.requestParsers.validators

import v2.controllers.requestParsers.validators.validations.{AdjustedStatusValidation, BsasIdValidation, NinoValidation}
import v2.models.errors.MtdError
import v2.models.request.RetrieveSelfEmploymentBsasRawData

class RetrieveSelfEmploymentValidator extends Validator[RetrieveSelfEmploymentBsasRawData] {

  private val validationSet = List(parameterFormatValidation)

  private def parameterFormatValidation: RetrieveSelfEmploymentBsasRawData => List[List[MtdError]] = (data: RetrieveSelfEmploymentBsasRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BsasIdValidation.validate(data.bsasId),
      data.adjustedStatus.map(AdjustedStatusValidation.validate).getOrElse(Nil)
    )
  }

  override def validate(data: RetrieveSelfEmploymentBsasRawData): List[MtdError] = run(validationSet, data).distinct
}
