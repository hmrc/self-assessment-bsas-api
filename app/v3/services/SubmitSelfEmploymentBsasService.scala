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
import v3.support.DownstreamResponseMappingSupport

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class SubmitSelfEmploymentBsasService @Inject()(connector: SubmitSelfEmploymentBsasConnector) extends DownstreamResponseMappingSupport with Logging {

  def submitSelfEmploymentBsas(request: SubmitSelfEmploymentBsasRequestData)(
      implicit hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[Unit]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.submitSelfEmploymentBsas(request)).leftMap(mapDownstreamErrors(mappingDesToMtdError))
    } yield desResponseWrapper

    result.value
  }

  private def mappingDesToMtdError: Map[String, MtdError] = Map(
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
    "SERVICE_UNAVAILABLE"           -> InternalError
  )
}
