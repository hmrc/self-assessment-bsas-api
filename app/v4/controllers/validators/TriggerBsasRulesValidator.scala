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

package v4.controllers.validators

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits._
import config.BsasConfig
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveDateRange}
import shared.models.errors.MtdError
import v4.controllers.validators.resolvers.ResolveTypeOfBusiness
import v4.models.domain.TypeOfBusiness
import v4.models.domain.TypeOfBusiness.{`foreign-property-fhl-eea`, `foreign-property`, `self-employment`, `uk-property-fhl`, `uk-property-non-fhl`}
import v4.models.errors.RuleAccountingPeriodNotSupportedError
import v4.models.request.triggerBsas.TriggerBsasRequestData

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}

@Singleton
class TriggerBsasRulesValidator @Inject() (bsasConfig: BsasConfig) extends RulesValidator[TriggerBsasRequestData] {

  private val minYear: Int = 1900
  private val maxYear: Int = 2099

  private val resolveDateRange = ResolveDateRange().withYearsLimitedTo(minYear, maxYear)

  private lazy val foreignPropertyEarliestEndDate: LocalDate = LocalDate.parse(
    s"${bsasConfig.v3TriggerForeignBsasMinimumTaxYear.dropRight(3)}-04-06",
    DateTimeFormatter.ISO_LOCAL_DATE
  )

  private lazy val selfEmploymentAndUkPropertyEarliestEndDate: LocalDate = LocalDate.parse(
    s"${bsasConfig.v3TriggerNonForeignBsasMinimumTaxYear.dropRight(3)}-04-06",
    DateTimeFormatter.ISO_LOCAL_DATE
  )

  def validateBusinessRules(parsed: TriggerBsasRequestData): Validated[Seq[MtdError], TriggerBsasRequestData] = {
    import parsed.body
    import parsed.body.accountingPeriod._

    val (validatedBusinessId, validatedDateRange, validatedTypeOfBusiness) = (
      ResolveBusinessId(body.businessId),
      resolveDateRange(startDate -> endDate),
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

    if (endDate.isBefore(earliestDate))
      Invalid(List(RuleAccountingPeriodNotSupportedError))
    else
      valid
  }

}
