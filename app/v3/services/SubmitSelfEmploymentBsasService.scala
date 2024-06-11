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

package v3.services

import cats.implicits._
import common.errors._
import shared.controllers.RequestContext
import shared.models.errors._
import shared.services.{BaseService, ServiceOutcome}
import v3.connectors.SubmitSelfEmploymentBsasConnector
import v3.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitSelfEmploymentBsasService @Inject() (connector: SubmitSelfEmploymentBsasConnector) extends BaseService {

  private val errorMap: Map[String, MtdError] = {
    val errors: Map[String, MtdError] = Map(
      "INVALID_TAXABLE_ENTITY_ID"     -> NinoFormatError,
      "INVALID_CALCULATION_ID"        -> CalculationIdFormatError,
      "INVALID_PAYLOAD"               -> InternalError,
      "ASC_ID_INVALID"                -> RuleSummaryStatusInvalid,
      "ASC_ALREADY_SUPERSEDED"        -> RuleSummaryStatusSuperseded,
      "ASC_ALREADY_ADJUSTED"          -> RuleAlreadyAdjusted,
      "UNALLOWABLE_VALUE"             -> RuleResultingValueNotPermitted,
      "INCOMESOURCE_TYPE_NOT_MATCHED" -> RuleTypeOfBusinessIncorrectError,
      "BVR_FAILURE_C55316"            -> RuleOverConsolidatedExpensesThreshold,
      "BVR_FAILURE_C15320"            -> RuleTradingIncomeAllowanceClaimed,
      "BVR_FAILURE_C55503"            -> InternalError,
      "BVR_FAILURE_C55508"            -> InternalError,
      "BVR_FAILURE_C55509"            -> InternalError,
      "BVR_FAILURE_C559107"           -> InternalError,
      "BVR_FAILURE_C559103"           -> InternalError,
      "BVR_FAILURE_C559099"           -> InternalError,
      "NO_DATA_FOUND"                 -> NotFoundError,
      "INVALID_CORRELATIONID"         -> InternalError,
      "SERVER_ERROR"                  -> InternalError,
      "SERVICE_UNAVAILABLE"           -> InternalError,
      "RULE_TAX_YEAR_RANGE_INVALID"   -> RuleTaxYearRangeInvalidError
    )

    val extraTysErrors =
      Map(
        "INCOME_SOURCE_TYPE_NOT_MATCHED" -> RuleTypeOfBusinessIncorrectError,
        "INVALID_TAX_YEAR"               -> TaxYearFormatError,
        "NOT_FOUND"                      -> NotFoundError,
        "TAX_YEAR_NOT_SUPPORTED"         -> RuleTaxYearNotSupportedError
      )

    errors ++ extraTysErrors
  }

  def submitSelfEmploymentBsas(
      request: SubmitSelfEmploymentBsasRequestData)(implicit ctx: RequestContext, ec: ExecutionContext): Future[ServiceOutcome[Unit]] = {

    connector
      .submitSelfEmploymentBsas(request)
      .map(_.leftMap(mapDownstreamErrors(errorMap)))
  }

}
