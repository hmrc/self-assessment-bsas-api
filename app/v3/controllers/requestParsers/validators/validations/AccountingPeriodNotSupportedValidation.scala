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

package v3.controllers.requestParsers.validators.validations

import config.AppConfig

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import v3.models.domain.TypeOfBusiness
import v3.models.errors.{MtdError, RuleAccountingPeriodNotSupportedError}

object AccountingPeriodNotSupportedValidation {
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def validate(typeOfBusiness: TypeOfBusiness, endDate: String, config: AppConfig): List[MtdError] = {
    // self-employment and uk property: end date earliest 2019-20
    // foreign property: end date earliest 2021-22
    lazy val foreignPropertyEarliestEndDate: LocalDate = LocalDate.parse(
      s"${config.v3TriggerForeignBsasMinimumTaxYear.dropRight(3)}-04-06",
      DateTimeFormatter.ISO_LOCAL_DATE
    )
    lazy val selfEmploymentAndUkPropertyEarliestEndDate: LocalDate = LocalDate.parse(
      s"${config.v3TriggerNonForeignBsasMinimumTaxYear.dropRight(3)}-04-06",
      DateTimeFormatter.ISO_LOCAL_DATE
    )


    val earliestDate: LocalDate = typeOfBusiness match {
      case TypeOfBusiness.`self-employment` | TypeOfBusiness.`uk-property-fhl` | TypeOfBusiness.`uk-property-non-fhl` =>
        selfEmploymentAndUkPropertyEarliestEndDate
      case TypeOfBusiness.`foreign-property-fhl-eea` | TypeOfBusiness.`foreign-property` =>
        foreignPropertyEarliestEndDate
    }

    val localDateEndDate   = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)

    if (localDateEndDate.isBefore(earliestDate)) {
      List(RuleAccountingPeriodNotSupportedError)
    }
    else {
      NoValidationErrors
    }

  }
}
