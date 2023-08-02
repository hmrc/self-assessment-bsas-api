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

package v3.controllers.validators

import api.controllers.validators.RulesValidator
import api.controllers.validators.resolvers.{ResolveBusinessId, ResolveDateRange}
import api.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits._
import config.AppConfig
import v3.controllers.validators.resolvers.ResolveTypeOfBusiness
import v3.models.domain.TypeOfBusiness
import v3.models.domain.TypeOfBusiness.{`foreign-property-fhl-eea`, `foreign-property`, `self-employment`, `uk-property-fhl`, `uk-property-non-fhl`}
import v3.models.errors.RuleAccountingPeriodNotSupportedError
import v3.models.request.triggerBsas.TriggerBsasRequestData

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}

@Singleton
class TriggerBsasRulesValidator @Inject() (appConfig: AppConfig) extends RulesValidator[TriggerBsasRequestData] {

  private lazy val foreignPropertyEarliestEndDate: LocalDate = LocalDate.parse(
    s"${appConfig.v3TriggerForeignBsasMinimumTaxYear.dropRight(3)}-04-06",
    DateTimeFormatter.ISO_LOCAL_DATE
  )

  private lazy val selfEmploymentAndUkPropertyEarliestEndDate: LocalDate = LocalDate.parse(
    s"${appConfig.v3TriggerNonForeignBsasMinimumTaxYear.dropRight(3)}-04-06",
    DateTimeFormatter.ISO_LOCAL_DATE
  )

  def validateBusinessRules(parsed: TriggerBsasRequestData): Validated[Seq[MtdError], TriggerBsasRequestData] = {
    import parsed.body
    import parsed.body.accountingPeriod._

    val (validatedBusinessId, validatedDateRange, validatedTypeOfBusiness) = (
      ResolveBusinessId(body.businessId),
      ResolveDateRange(startDate -> endDate),
      ResolveTypeOfBusiness(body.typeOfBusiness)
    )

    (
      validatedBusinessId,
      validatedDateRange,
      validatedTypeOfBusiness
    ).mapN((_, _, _))
      .andThen { case (_, dateRange, typeOfBusiness) => validateAccountingPeriodNotSupported(dateRange.endDate, typeOfBusiness) }
      .onSuccess(parsed)
  }

  private def validateAccountingPeriodNotSupported(endDate: LocalDate, typeOfBusiness: TypeOfBusiness): Validated[Seq[MtdError], Unit] = {

    val earliestDate: LocalDate = typeOfBusiness match {
      case `self-employment` | `uk-property-fhl` | `uk-property-non-fhl` =>
        selfEmploymentAndUkPropertyEarliestEndDate
      case `foreign-property-fhl-eea` | `foreign-property` =>
        foreignPropertyEarliestEndDate
    }

    if (endDate.isBefore(earliestDate)) {
      Invalid(List(RuleAccountingPeriodNotSupportedError))
    } else {
      valid
    }
  }

}
