/*
 * Copyright 2020 HM Revenue & Customs
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

package v2.services

import cats.data.EitherT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v2.connectors.SubmitSelfEmploymentBsasConnector
import v2.controllers.EndpointLogContext
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestData
import v2.models.response.SubmitSelfEmploymentBsasResponse
import v2.support.DesResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitSelfEmploymentBsasService @Inject()(connector: SubmitSelfEmploymentBsasConnector) extends DesResponseMappingSupport with Logging {

  def submitSelfEmploymentBsas(request: SubmitSelfEmploymentBsasRequestData)(
                implicit hc: HeaderCarrier, ec: ExecutionContext, logContext: EndpointLogContext,
                correlationId: String):
  Future[Either[ErrorWrapper, ResponseWrapper[SubmitSelfEmploymentBsasResponse]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.submitSelfEmploymentBsas(request)).leftMap(mapDesErrors(mappingDesToMtdError))
      mtdResponseWrapper <- EitherT.fromEither[Future](validateSubmitSelfEmploymentSuccessResponse(desResponseWrapper))
    } yield mtdResponseWrapper

    result.value
  }

  private def mappingDesToMtdError: Map[String, MtdError] = Map(
    "INVALID_TAXABLE_ENTITY_ID"   -> NinoFormatError,
    "INVALID_CALCULATION_ID"      -> BsasIdFormatError,
    "INVALID_PAYLOAD"             -> DownstreamError,
    "INVALID_PAYLOAD_REMOTE"      -> DownstreamError,
    "INVALID_FIELD"               -> RuleNotSelfEmployment,
    "INVALID_MONETARY_FORMAT"     -> DownstreamError,
    "ASC_ID_INVALID"              -> RuleSummaryStatusInvalid,
    "ASC_ALREADY_SUPERSEDED"      -> RuleSummaryStatusSuperseded,
    "ASC_ALREADY_ADJUSTED"        -> RuleBsasAlreadyAdjusted,
    "UNALLOWABLE_VALUE"          -> RuleResultingValueNotPermitted,
    "BVR_FAILURE_C55316"          -> RuleOverConsolidatedExpensesThreshold,
    "BVR_FAILURE_C15320"          -> RuleTradingIncomeAllowanceClaimed,
    "BVR_FAILURE_C55503"          -> RuleNotSelfEmployment,
    "BVR_FAILURE_C55508"          -> RuleNotSelfEmployment,
    "BVR_FAILURE_C55509"          -> RuleNotSelfEmployment,
    "NOT_FOUND"                   -> NotFoundError,
    "SERVER_ERROR"                -> DownstreamError,
    "SERVICE_UNAVAILABLE"         -> DownstreamError
  )
}
