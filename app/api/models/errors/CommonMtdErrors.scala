/*
 * Copyright 2026 HM Revenue & Customs
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

package api.models.errors

import play.api.http.Status.*

// MtdError types that are common across MTD APIs.

// Format Errors
object NinoFormatError extends MtdError("FORMAT_NINO", "The NINO format is invalid", BAD_REQUEST)

object TaxYearFormatError extends MtdError("FORMAT_TAX_YEAR", "The taxYear format is invalid", BAD_REQUEST)

object StartDateFormatError extends MtdError("FORMAT_START_DATE", "The provided Start date is invalid", BAD_REQUEST)

object EndDateFormatError extends MtdError("FORMAT_END_DATE", "The provided End date is invalid", BAD_REQUEST)

object DateFormatError extends MtdError(code = "FORMAT_DATE", message = "The supplied date format is not valid", BAD_REQUEST)

object BusinessIdFormatError extends MtdError("FORMAT_BUSINESS_ID", "The Business ID format is invalid", BAD_REQUEST)

object PropertyIdFormatError extends MtdError(code = "FORMAT_PROPERTY_ID", message = "The property ID format is invalid", BAD_REQUEST)

object CountryCodeFormatError extends MtdError("FORMAT_COUNTRY_CODE", "The provided Country code is invalid", BAD_REQUEST)

object ValueFormatError extends MtdError("FORMAT_VALUE", "The value must be between 0 and 99999999999.99", BAD_REQUEST) {

  def forPathAndRange(path: String, min: String, max: String): MtdError =
    ValueFormatError.copy(paths = Some(Seq(path)), message = s"The value must be between $min and $max")

  def forPathAndRangeExcludeZero(path: String, min: String, max: String): MtdError =
    ValueFormatError.copy(paths = Some(Seq(path)), message = s"The value must be between $min and $max (but cannot be 0 or 0.00)")

  def forPathAndMin(path: String, min: String): MtdError =
    ValueFormatError.copy(paths = Some(Seq(path)), message = s"The value must be $min or more")

}

object CalculationIdFormatError extends MtdError("FORMAT_CALCULATION_ID", "The provided calculation ID is invalid", BAD_REQUEST)

object TypeOfBusinessFormatError extends MtdError("FORMAT_TYPE_OF_BUSINESS", "The provided type of business is invalid", BAD_REQUEST)

//Standard Errors
object NotFoundError extends MtdError("MATCHING_RESOURCE_NOT_FOUND", "Matching resource not found", NOT_FOUND)

object GatewayTimeoutError extends MtdError("GATEWAY_TIMEOUT", "The request has timed out", GATEWAY_TIMEOUT)

object InternalError extends MtdError("INTERNAL_SERVER_ERROR", "An internal server error occurred", INTERNAL_SERVER_ERROR)

object BadRequestError extends MtdError("INVALID_REQUEST", "Invalid request", BAD_REQUEST)

object BVRError extends MtdError("BUSINESS_ERROR", "Business validation error", BAD_REQUEST)

object InvalidHttpMethodError extends MtdError("INVALID_HTTP_METHOD", "Invalid HTTP method", METHOD_NOT_ALLOWED)

object InvalidBodyTypeError extends MtdError("INVALID_BODY_TYPE", "Expecting text/json or application/json body", UNSUPPORTED_MEDIA_TYPE)

object InvalidTaxYearParameterError
    extends MtdError(code = "INVALID_TAX_YEAR_PARAMETER", message = "A tax year before 2023-24 was supplied", BAD_REQUEST)

object TriggerNotFoundError
    extends MtdError(
      "MATCHING_RESOURCE_NOT_FOUND",
      "A matching incomeSourceId record was not found, or the incomeSourceType provided does not relate to the incomeSourceId",
      NOT_FOUND)

//Authentication/Authorisation errors

/** Authentication OK but not allowed access to the requested resource
  */
object ClientOrAgentNotAuthorisedError extends MtdError("CLIENT_OR_AGENT_NOT_AUTHORISED", "The client or agent is not authorised", FORBIDDEN) {
  def withStatus401: MtdError = copy(httpStatus = UNAUTHORIZED)
}

object InvalidBearerTokenError extends MtdError("UNAUTHORIZED", "Bearer token is missing or not authorized", UNAUTHORIZED)

// Accept header Errors
object InvalidAcceptHeaderError extends MtdError("ACCEPT_HEADER_INVALID", "The accept header is missing or invalid", NOT_ACCEPTABLE)

object UnsupportedVersionError extends MtdError("NOT_FOUND", "The requested resource could not be found", NOT_FOUND)

// Common rule errors
object RuleRequestCannotBeFulfilledError
    extends MtdError("RULE_REQUEST_CANNOT_BE_FULFILLED", "Custom (will vary in production depending on the actual error)", 422)

object RuleTaxYearNotSupportedError
    extends MtdError("RULE_TAX_YEAR_NOT_SUPPORTED", "The tax year specified does not lie within the supported range", BAD_REQUEST)

object RuleIncorrectOrEmptyBodyError
    extends MtdError("RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED", "An empty or non-matching body was submitted", BAD_REQUEST)

object RuleTaxYearRangeInvalidError extends MtdError("RULE_TAX_YEAR_RANGE_INVALID", "A tax year range of one year is required", BAD_REQUEST)

object RuleTaxYearNotEndedError extends MtdError("RULE_TAX_YEAR_NOT_ENDED", "The specified tax year has not yet ended", BAD_REQUEST)

object RuleDateRangeInvalidError extends MtdError(code = "RULE_DATE_RANGE_INVALID", message = "The specified date range is invalid", BAD_REQUEST)

object RuleEndBeforeStartDateError
    extends MtdError("RULE_END_DATE_BEFORE_START_DATE", "The supplied accounting period end date is before the start date", BAD_REQUEST)

object RuleCountryCodeError extends MtdError("RULE_COUNTRY_CODE", "The country code is not a permitted ISO 3166-1 alpha-3 country code", BAD_REQUEST)

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

object RuleDuplicatePropertyIdError
    extends MtdError("RULE_DUPLICATE_PROPERTY_ID", "You cannot supply the same property ID for multiple properties", BAD_REQUEST) {

  def forDuplicatedIdsAndPaths(id: String, paths: Seq[String]): MtdError = copy(
    message = s"The property ID '$id' is duplicated for multiple properties",
    paths = Some(paths)
  )

}

object RuleResidentialFinancialCostNotAllowedError
    extends MtdError(
      "RULE_RESIDENTIAL_FINANCIAL_COST_NOT_ALLOWED",
      "The residentialFinancialCost field is not applicable for this tax year",
      BAD_REQUEST)

object RulePropertyIdMismatchError
    extends MtdError("RULE_PROPERTY_ID_MISMATCH", "The supplied property ID is not valid for this income source", BAD_REQUEST)

object RuleAccountingPeriodNotAlignedError
    extends MtdError(
      "RULE_ACCOUNTING_PERIOD_NOT_ALIGNED",
      "The accounting period must be a complete tax year (6 April to 5 April of the following year)",
      BAD_REQUEST
    )

//Stub Errors
object RuleIncorrectGovTestScenarioError
    extends MtdError("RULE_INCORRECT_GOV_TEST_SCENARIO", "The supplied Gov-Test-Scenario is not valid", BAD_REQUEST)
