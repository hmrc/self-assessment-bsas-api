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

package v3.services

import cats.data.EitherT
import cats.implicits._
import javax.inject.{ Inject, Singleton }
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v3.connectors.SubmitSelfEmploymentBsasConnector
import v3.controllers.EndpointLogContext
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestData
import v3.support.DesResponseMappingSupport

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class SubmitSelfEmploymentBsasService @Inject()(connector: SubmitSelfEmploymentBsasConnector) extends DesResponseMappingSupport with Logging {

  def submitSelfEmploymentBsas(request: SubmitSelfEmploymentBsasRequestData)(
      implicit hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[Unit]]] = {

    val result = EitherT(connector.submitSelfEmploymentBsas(request)).leftMap(mapDesErrors(errorMap))

    result.value
  }

  private val errorMap: Map[String, MtdError] = {
    val errors: Map[String, MtdError] = Map(
      "INVALID_TAXABLE_ENTITY_ID"     -> NinoFormatError,
      "INVALID_CALCULATION_ID"        -> CalculationIdFormatError,
      "INVALID_PAYLOAD"               -> DownstreamError,
      "ASC_ID_INVALID"                -> RuleSummaryStatusInvalid,
      "ASC_ALREADY_SUPERSEDED"        -> RuleSummaryStatusSuperseded,
      "ASC_ALREADY_ADJUSTED"          -> RuleAlreadyAdjusted,
      "UNALLOWABLE_VALUE"             -> RuleResultingValueNotPermitted,
      "INCOMESOURCE_TYPE_NOT_MATCHED" -> RuleTypeOfBusinessIncorrectError,
      "BVR_FAILURE_C55316"            -> RuleOverConsolidatedExpensesThreshold,
      "BVR_FAILURE_C15320"            -> RuleTradingIncomeAllowanceClaimed,
      "BVR_FAILURE_C55503"            -> DownstreamError,
      "BVR_FAILURE_C55508"            -> DownstreamError,
      "BVR_FAILURE_C55509"            -> DownstreamError,
      "BVR_FAILURE_C559107"           -> DownstreamError,
      "BVR_FAILURE_C559103"           -> DownstreamError,
      "BVR_FAILURE_C559099"           -> DownstreamError,
      "NO_DATA_FOUND"                 -> NotFoundError,
      "INVALID_CORRELATIONID"         -> DownstreamError,
      "SERVER_ERROR"                  -> DownstreamError,
      "SERVICE_UNAVAILABLE"           -> DownstreamError,
      "RULE_TAX_YEAR_RANGE_INVALID"   -> RuleTaxYearRangeInvalidError
    )

    val extraTysErrors =
      Map(
        "INVALID_TAX_YEAR"       -> TaxYearFormatError,
        "NOT_FOUND"              -> NotFoundError,
        "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
      )

    errors ++ extraTysErrors
  }
}
