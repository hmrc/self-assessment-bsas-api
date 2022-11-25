/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.controllers.requestParsers.validators

import v3.controllers.requestParsers.validators.validations.{CalculationIdValidation, NinoValidation, TaxYearTYSParameterValidation, TaxYearValidation}
import v3.models.errors.MtdError
import v3.models.request.retrieveBsas.selfEmployment.RetrieveSelfEmploymentBsasRawData

class RetrieveSelfEmploymentValidator extends Validator[RetrieveSelfEmploymentBsasRawData] {

  private val validationSet = List(parameterFormatValidation, parameterValidation)

  private def parameterFormatValidation: RetrieveSelfEmploymentBsasRawData => List[List[MtdError]] = (data: RetrieveSelfEmploymentBsasRawData) => {
    List(
      NinoValidation.validate(data.nino),
      CalculationIdValidation.validate(data.calculationId),
      TaxYearValidation.validate(data.taxYear)
    )
  }

  private def parameterValidation: RetrieveSelfEmploymentBsasRawData => List[List[MtdError]] = (data: RetrieveSelfEmploymentBsasRawData) => {
    List(
      data.taxYear.map(TaxYearTYSParameterValidation.validate).getOrElse(Nil)
    )
  }

  override def validate(data: RetrieveSelfEmploymentBsasRawData): List[MtdError] = run(validationSet, data).distinct
}
