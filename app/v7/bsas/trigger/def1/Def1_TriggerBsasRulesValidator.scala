/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.bsas.trigger.def1

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.*
import common.errors.RuleAccountingPeriodNotSupportedError
import config.BsasConfig
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveDateRange}
import shared.models.errors.MtdError
import v7.bsas.trigger.def1.model.request.Def1_TriggerBsasRequestData
import v7.common.model.TypeOfBusinessWithFHL
import v7.common.model.TypeOfBusinessWithFHL.*
import v7.common.resolvers.ResolveTypeOfBusinessWithFHL

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Def1_TriggerBsasRulesValidator(implicit bsasConfig: BsasConfig) extends RulesValidator[Def1_TriggerBsasRequestData] {

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

  def validateBusinessRules(parsed: Def1_TriggerBsasRequestData): Validated[Seq[MtdError], Def1_TriggerBsasRequestData] = {
    import parsed.body
    import parsed.body.accountingPeriod.*

    val (validatedBusinessId, validatedDateRange, validatedTypeOfBusiness) = (
      ResolveBusinessId(body.businessId),
      resolveDateRange(startDate -> endDate),
      ResolveTypeOfBusinessWithFHL(body.typeOfBusiness)
    )

    (
      validatedBusinessId,
      validatedDateRange,
      validatedTypeOfBusiness
    ).mapN((_, _, _))
      .andThen { case (_, dateRange, typeOfBusiness) => validateAccountingPeriodNotSupported(dateRange.endDate, typeOfBusiness) }
      .onSuccess(parsed)
  }

  private def validateAccountingPeriodNotSupported(endDate: LocalDate, typeOfBusiness: TypeOfBusinessWithFHL): Validated[Seq[MtdError], Unit] = {

    val earliestDate: LocalDate = typeOfBusiness match {
      case `self-employment` | `uk-property-fhl` | `uk-property` =>
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
