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

package common.errors

import play.api.http.Status.{BAD_REQUEST, NOT_FOUND}
import shared.models.errors.MtdError

object TypeOfBusinessFormatError extends MtdError("FORMAT_TYPE_OF_BUSINESS", "The provided type of business is invalid", BAD_REQUEST)

object TriggerNotFoundError
    extends MtdError(
      "MATCHING_RESOURCE_NOT_FOUND",
      "A matching incomeSourceId record was not found, or the incomeSourceType provided does not relate to the incomeSourceId",
      NOT_FOUND)

// Rule Errors

object RuleBothPropertiesSuppliedError
    extends MtdError("RULE_BOTH_PROPERTIES_SUPPLIED", "Both FHL and Non-FHL properties cannot be present at the same time", BAD_REQUEST)

object RuleBothExpensesError
    extends MtdError("RULE_BOTH_EXPENSES_SUPPLIED", "Both expenses and consolidated expenses cannot be present at the same time", BAD_REQUEST)

object RuleAccountingPeriodNotSupportedError
    extends MtdError(
      "RULE_ACCOUNTING_PERIOD_NOT_SUPPORTED",
      "The specified accounting period is not supported, that is, the accounting period specified falls before the minimum tax year value",
      BAD_REQUEST
    )

object RuleResultingValueNotPermitted
    extends MtdError(
      "RULE_RESULTING_VALUE_NOT_PERMITTED",
      "The adjustments provided would produce an unacceptable negative monetary value",
      BAD_REQUEST)

object RuleDuplicateCountryCodeError
    extends MtdError("RULE_DUPLICATE_COUNTRY_CODE", "The same countryCode cannot be supplied multiple times", BAD_REQUEST) {

  def forDuplicatedCodesAndPaths(code: String, paths: Seq[String]): MtdError =
    RuleDuplicateCountryCodeError.copy(message = s"The country code '$code' is supplied multiple times", paths = Some(paths))

}

object RuleAccountingPeriodNotEndedError
    extends MtdError("RULE_ACCOUNTING_PERIOD_NOT_ENDED", "The supplied accounting period has not ended", BAD_REQUEST)

object RuleNoAccountingPeriodError extends MtdError("RULE_NO_ACCOUNTING_PERIOD", "The supplied accounting period does not exist", BAD_REQUEST)

object RulePeriodicDataIncompleteError
    extends MtdError("RULE_PERIODIC_DATA_INCOMPLETE", "One or more periodic updates missing for this accounting period", BAD_REQUEST)

object RuleTypeOfBusinessIncorrectError
    extends MtdError("RULE_TYPE_OF_BUSINESS_INCORRECT", "The calculation ID supplied relates to a different type of business", BAD_REQUEST)

object RuleOutsideAmendmentWindowError extends MtdError("RULE_OUTSIDE_AMENDMENT_WINDOW", "You are outside the amendment window", BAD_REQUEST)

object RuleSummaryStatusInvalid extends MtdError("RULE_SUMMARY_STATUS_INVALID", "Periodic data has changed. Request a new summary", BAD_REQUEST)

object RuleSummaryStatusSuperseded
    extends MtdError("RULE_SUMMARY_STATUS_SUPERSEDED", "A newer summary calculation exists for this accounting period", BAD_REQUEST)

object RuleAlreadyAdjusted extends MtdError("RULE_ALREADY_ADJUSTED", "A summary may only be adjusted once. Request a new summary", BAD_REQUEST)

object RuleOverConsolidatedExpensesThreshold
    extends MtdError(
      "RULE_OVER_CONSOLIDATED_EXPENSES_THRESHOLD",
      "The cumulative turnover amount exceeds the consolidated expenses threshold",
      BAD_REQUEST)

object RuleTradingIncomeAllowanceClaimed
    extends MtdError("RULE_TRADING_INCOME_ALLOWANCE_CLAIMED", "A claim for trading income allowance was made. Cannot also have expenses", BAD_REQUEST)

object RulePropertyIncomeAllowanceClaimed
    extends MtdError(
      "RULE_PROPERTY_INCOME_ALLOWANCE_CLAIMED",
      "A claim for property income allowance was made. Cannot also have expenses",
      BAD_REQUEST)

object RuleObligationsNotMet extends MtdError("RULE_OBLIGATIONS_NOT_MET", "The obligations for the business have not been met", BAD_REQUEST)

object RuleBothAdjustmentsSuppliedError
    extends MtdError("RULE_BOTH_ADJUSTMENTS_SUPPLIED", "Both adjustments and zero adjustments must not be present at the same time", BAD_REQUEST)

object RuleZeroAdjustmentsInvalidError extends MtdError("RULE_ZERO_ADJUSTMENTS_INVALID", "Zero adjustments can only be set to true", BAD_REQUEST)
