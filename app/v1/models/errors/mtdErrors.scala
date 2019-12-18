/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.errors

import play.api.libs.json.{Json, Writes}

case class MtdError(code: String, message: String)

object MtdError {
  implicit val writes: Writes[MtdError] = Json.writes[MtdError]
}

object MtdErrorWithCustomMessage {
//  implicit val writes: Writes[MtdError] = Json.writes[MtdError]

  def unapply(arg: MtdError): Option[String] = Some(arg.code)
}

object NinoFormatError extends MtdError("FORMAT_NINO", "The provided NINO is invalid")

object TaxYearFormatError extends MtdError("FORMAT_TAX_YEAR", "The provided tax year is invalid")

object StartDateFormatError
  extends MtdError("FORMAT_START_DATE","The supplied accounting period start date format is invalid")

object EndDateFormatError
  extends MtdError("FORMAT_END_DATE","The supplied accounting period end date format is invalid")

object TypeOfBusinessFormatError
  extends MtdError("FORMAT_TYPE_OF_BUSINESS","The supplied type of business format is invalid")

object AdjustedStatusFormatError
  extends MtdError("FORMAT_ADJUSTED_STATUS", "The supplied adjusted status format is invalid")

object FormatAdjustmentValueError extends MtdError("FORMAT_ADJUSTMENT_VALUE", "") {
  def withFieldName(fieldName: String): MtdError = this.copy(message = s"The format of the '$fieldName' value is invalid")
}

object SelfEmploymentIdFormatError extends MtdError("FORMAT_SELF_EMPLOYMENT_ID","The supplied self-employment ID format is invalid")

object BsasIdFormatError extends  MtdError("FORMAT_BSAS_ID", "The provided Business Source Adjustable Summary ID format is invalid")


// Rule Errors
object SelfEmploymentIdRuleError
  extends MtdError("RULE_SELF_EMPLOYMENT_ID","A self-employment ID should be supplied for a self-employment business type")

object EndBeforeStartDateError
  extends MtdError("RULE_END_DATE_BEFORE_START_DATE","The accounting period end date predates the start date")

object RuleBothExpensesError
  extends MtdError("RULE_BOTH_EXPENSES_SUPPLIED", "Both expenses and consolidated expenses cannot be present at the same time")

object RuleSelfEmploymentAdjusted
  extends MtdError("RULE_SELF_EMPLOYMENT_ADJUSTED",
    "A self-employment business type was adjusted. Re-trigger an adjustable summary for the self-employment to correct")


// Rule Errors
object RuleAccountingPeriodNotSupportedError
  extends MtdError("RULE_ACCOUNTING_PERIOD_NOT_SUPPORTED", "The accounting period is not supported, because it predates the earliest allowable tax year")

object RuleTaxYearNotSupportedError
  extends MtdError("RULE_TAX_YEAR_NOT_SUPPORTED", "Tax year not supported, because it precedes the earliest allowable tax year")

object RuleIncorrectOrEmptyBodyError extends MtdError("RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED", "An empty or non-matching body was submitted")

object RuleTaxYearRangeInvalidError
  extends MtdError("RULE_TAX_YEAR_RANGE_INVALID", "Tax year range invalid. A tax year range of one year is required")

object RuleAccountingPeriodNotEndedError extends MtdError("RULE_ACCOUNTING_PERIOD_NOT_ENDED","The accounting period has not yet ended")

object RulePeriodicDataIncompleteError extends MtdError("RULE_PERIODIC_DATA_INCOMPLETE","One or more periodic updates missing for this accounting period")

object RuleNoAccountingPeriodError extends MtdError("RULE_NO_ACCOUNTING_PERIOD","The accounting period given does not exist")

object RuleTypeOfBusinessError extends MtdError("RULE_TYPE_OF_BUSINESS_INCORRECT",  "The submission is for a different type of business")

object RuleSummaryStatusInvalid extends MtdError( "RULE_SUMMARY_STATUS_INVALID", "Periodic data has changed. Request a new summary")

object RuleSummaryStatusSuperseded extends MtdError( "RULE_SUMMARY_STATUS_SUPERSEDED", "A newer summary calculation exists for this accounting period")

object RuleBsasAlreadyAdjusted extends MtdError("RULE_BSAS_ALREADY_ADJUSTED", "A summary may only be adjusted once. Request a new summary")

object RuleOverConsolidatedExpensesThreshold
  extends MtdError("RULE_OVER_CONSOLIDATED_EXPENSES_THRESHOLD", "The cumulative turnover amount exceeds the consolidated expenses threshold")

object RulePropertyIncomeAllowanceClaimed
  extends MtdError("RULE_PROPERTY_INCOME_ALLOWANCE_CLAIMED", "A claim for property income allowance was made. Cannot also have expenses")

object RuleNoAdjustmentsMade extends MtdError("RULE_NO_ADJUSTMENTS_MADE", "An adjusted summary calculation does not exist")

object RuleNotUkProperty extends MtdError("RULE_NOT_UK_PROPERTY", "The summary calculation requested is not for a UK property business")

object RuleNotSelfEmployment extends MtdError("RULE_NOT_SELF_EMPLOYMENT", "The summary calculation requested is not for a self employment business")

object RuleAdjustmentRangeInvalid extends MtdError("RULE_RANGE_INVALID", "") {
  def withFieldName(fieldName: String): MtdError = this.copy(message = s"Adjustment value for '$fieldName' falls outside the accepted range")
}

//Standard Errors
object NotFoundError extends MtdError("MATCHING_RESOURCE_NOT_FOUND", "Matching resource not found")

object DownstreamError extends MtdError("INTERNAL_SERVER_ERROR", "An internal server error occurred")

object BadRequestError extends MtdError("INVALID_REQUEST", "Invalid request")

object BVRError extends MtdError("BUSINESS_ERROR", "Business validation error")

object ServiceUnavailableError extends MtdError("SERVICE_UNAVAILABLE", "Internal server error")

//Authorisation Errors
object UnauthorisedError extends MtdError("CLIENT_OR_AGENT_NOT_AUTHORISED", "The client and/or agent is not authorised")

object InvalidBearerTokenError extends MtdError("UNAUTHORIZED", "Bearer token is missing or not authorized")

// Accept header Errors
object  InvalidAcceptHeaderError extends MtdError("ACCEPT_HEADER_INVALID", "The accept header is missing or invalid")

object  UnsupportedVersionError extends MtdError("NOT_FOUND", "The requested resource could not be found")

object InvalidBodyTypeError extends MtdError("INVALID_BODY_TYPE", "Expecting text/json or application/json body")
