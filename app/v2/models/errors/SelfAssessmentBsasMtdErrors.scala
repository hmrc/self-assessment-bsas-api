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

package v2.models.errors

import api.models.errors.MtdError
import play.api.http.Status.{BAD_REQUEST, FORBIDDEN}

object RuleTypeOfBusinessError extends MtdError("RULE_TYPE_OF_BUSINESS_INCORRECT", "The submission is for a different type of business", FORBIDDEN)

object RuleNotUkProperty extends MtdError("RULE_NOT_UK_PROPERTY", "The summary calculation requested is not for a UK property business", FORBIDDEN)

object RuleNotForeignProperty
    extends MtdError("RULE_NOT_FOREIGN_PROPERTY", "The summary calculation requested is not for a foreign property business", FORBIDDEN)

object RuleAdjustmentRangeInvalid extends MtdError("RULE_RANGE_INVALID", "Adjustment value falls outside accepted range", BAD_REQUEST)

object RuleBsasAlreadyAdjusted extends MtdError("RULE_BSAS_ALREADY_ADJUSTED", "A summary may only be adjusted once. Request a new summary", FORBIDDEN)

object RuleNoAdjustmentsMade extends MtdError("RULE_NO_ADJUSTMENTS_MADE", "An adjusted summary calculation does not exist", FORBIDDEN)

object RuleCountryCodeError extends MtdError("RULE_COUNTRY_CODE", "The country code is not a valid ISO 3166-1 alpha-3 country code", BAD_REQUEST)

object RuleBothExpensesError
    extends MtdError("RULE_BOTH_EXPENSES_SUPPLIED", "Both expenses and consolidated expenses cannot be present at the same time", BAD_REQUEST)

object RuleSummaryStatusInvalid extends MtdError("RULE_SUMMARY_STATUS_INVALID", "Periodic data has changed. Request a new summary", FORBIDDEN)

object RuleSummaryStatusSuperseded
    extends MtdError("RULE_SUMMARY_STATUS_SUPERSEDED", "A newer summary calculation exists for this accounting period", FORBIDDEN)

object RuleResultingValueNotPermitted
    extends MtdError("RULE_RESULTING_VALUE_NOT_PERMITTED",
                     "The adjustments provided would produce an unacceptable negative monetary value",
                     FORBIDDEN)

object RuleOverConsolidatedExpensesThreshold
    extends MtdError("RULE_OVER_CONSOLIDATED_EXPENSES_THRESHOLD",
                     "The cumulative turnover amount exceeds the consolidated expenses threshold",
                     FORBIDDEN)

object RuleTradingIncomeAllowanceClaimed
    extends MtdError("RULE_TRADING_INCOME_ALLOWANCE_CLAIMED", "A claim for trading income allowance was made. Cannot also have expenses", FORBIDDEN)

object RulePropertyIncomeAllowanceClaimed
    extends MtdError("RULE_PROPERTY_INCOME_ALLOWANCE_CLAIMED", "A claim for property income allowance was made. Cannot also have expenses", FORBIDDEN)

object RuleNotSelfEmployment
    extends MtdError("RULE_NOT_SELF_EMPLOYMENT", "The summary calculation requested is not for a self-employment business", FORBIDDEN)

object RuleTaxYearRangeInvalidError
    extends MtdError("RULE_TAX_YEAR_RANGE_INVALID", "Tax year range invalid. A tax year range of one year is required", BAD_REQUEST)

object RuleAccountingPeriodNotSupportedError
    extends MtdError(
      "RULE_ACCOUNTING_PERIOD_NOT_SUPPORTED",
      "The specified accounting period is not supported, that is, the accounting period specified falls before the minimum tax year value",
      BAD_REQUEST
    )

object RulePeriodicDataIncompleteError
    extends MtdError("RULE_PERIODIC_DATA_INCOMPLETE", "One or more periodic updates missing for this accounting period", FORBIDDEN)

object RuleAccountingPeriodNotEndedError extends MtdError("RULE_ACCOUNTING_PERIOD_NOT_ENDED", "The accounting period has not yet ended", FORBIDDEN)

object RuleNoAccountingPeriodError extends MtdError("RULE_NO_ACCOUNTING_PERIOD", "The accounting period given does not exist", FORBIDDEN)