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

package v3.models.errors

import api.models.errors.MtdError
import play.api.http.Status.BAD_REQUEST

// Rule Errors

object RuleBothPropertiesSuppliedError
  extends MtdError("RULE_BOTH_PROPERTIES_SUPPLIED", "Both FHL and Non-FHL properties cannot be present at the same time", BAD_REQUEST)

object RuleBothExpensesError
  extends MtdError("RULE_BOTH_EXPENSES_SUPPLIED", "Both expenses and consolidated expenses cannot be present at the same time", BAD_REQUEST)

object RuleSelfEmploymentAdjustedError
  extends MtdError(
    "RULE_SELF_EMPLOYMENT_ADJUSTED",
    "A self-employment business type was adjusted. Re-trigger an adjustable summary for the self-employment to correct",
    BAD_REQUEST
  )

object RuleAccountingPeriodNotSupportedError
  extends MtdError(
    "RULE_ACCOUNTING_PERIOD_NOT_SUPPORTED",
    "The specified accounting period is not supported, that is, the accounting period specified falls before the minimum tax year value",
    BAD_REQUEST
  )

object RuleResultingValueNotPermitted
  extends MtdError("RULE_RESULTING_VALUE_NOT_PERMITTED",
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

object RuleSummaryStatusInvalid extends MtdError("RULE_SUMMARY_STATUS_INVALID", "Periodic data has changed. Request a new summary", BAD_REQUEST)

object RuleSummaryStatusSuperseded
  extends MtdError("RULE_SUMMARY_STATUS_SUPERSEDED", "A newer summary calculation exists for this accounting period", BAD_REQUEST)

object RuleAlreadyAdjusted extends MtdError("RULE_ALREADY_ADJUSTED", "A summary may only be adjusted once. Request a new summary", BAD_REQUEST)

object RuleOverConsolidatedExpensesThreshold
  extends MtdError("RULE_OVER_CONSOLIDATED_EXPENSES_THRESHOLD",
    "The cumulative turnover amount exceeds the consolidated expenses threshold",
    BAD_REQUEST)

object RuleTradingIncomeAllowanceClaimed
  extends MtdError("RULE_TRADING_INCOME_ALLOWANCE_CLAIMED", "A claim for trading income allowance was made. Cannot also have expenses", BAD_REQUEST)

object RulePropertyIncomeAllowanceClaimed
  extends MtdError("RULE_PROPERTY_INCOME_ALLOWANCE_CLAIMED",
    "A claim for property income allowance was made. Cannot also have expenses",
    BAD_REQUEST)

object RuleNoAdjustmentsMade extends MtdError("RULE_NO_ADJUSTMENTS_MADE", "An adjusted summary calculation does not exist", BAD_REQUEST)

object RuleNotSelfEmployment
  extends MtdError("RULE_NOT_SELF_EMPLOYMENT", "The adjustments requested are not for a self-employment business", BAD_REQUEST)
