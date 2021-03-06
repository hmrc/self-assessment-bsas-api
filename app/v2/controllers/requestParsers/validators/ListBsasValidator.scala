/*
 * Copyright 2021 HM Revenue & Customs
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

import config.FixedConfig
import v2.controllers.requestParsers.validators.validations._
import v2.models.errors.{MtdError, RuleTaxYearNotSupportedError}
import v2.models.request.ListBsasRawData


class ListBsasValidator extends Validator[ListBsasRawData] with FixedConfig {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation)

  private def parameterFormatValidation: ListBsasRawData => List[List[MtdError]] = (data: ListBsasRawData) => List(
    NinoValidation.validate(data.nino),
    data.taxYear.map(TaxYearValidation.validate).getOrElse(Nil),
    data.typeOfBusiness.map(TypeOfBusinessValidation.validate).getOrElse(Nil),
    data.businessId.map(BusinessIdValidation.validate).getOrElse(Nil)
  )


  private def parameterRuleValidation: ListBsasRawData => List[List[MtdError]] = (data: ListBsasRawData) => List(
    data.taxYear.map(MtdTaxYearValidation.validate(_, RuleTaxYearNotSupportedError, listMinimumTaxYear)).getOrElse(Nil)
  )

  override def validate(data: ListBsasRawData): List[MtdError] = run(validationSet, data).distinct
}
