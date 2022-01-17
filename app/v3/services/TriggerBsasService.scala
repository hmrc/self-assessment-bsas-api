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
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v2.connectors.TriggerBsasConnector
import v2.controllers.EndpointLogContext
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.triggerBsas.TriggerBsasRequest
import v2.models.response.TriggerBsasResponse
import v2.support.DesResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerBsasService @Inject()(connector: TriggerBsasConnector) extends DesResponseMappingSupport with Logging{

  def triggerBsas(request: TriggerBsasRequest)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext, logContext: EndpointLogContext,
                        correlationId: String):
  Future[Either[ErrorWrapper, ResponseWrapper[TriggerBsasResponse]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.triggerBsas(request)).leftMap(mapDesErrors(mappingDesToMtdError))
    } yield desResponseWrapper.map(des => des)

    result.value
  }

  private def mappingDesToMtdError: Map[String, MtdError] = Map(
    "INVALID_TAXABLE_ENTITY_ID"     -> NinoFormatError,
    "ACCOUNTING_PERIOD_NOT_ENDED"   -> RuleAccountingPeriodNotEndedError,
    "OBLIGATIONS_NOT_MET"           -> RulePeriodicDataIncompleteError,
    "NO_ACCOUNTING_PERIOD"          -> RuleNoAccountingPeriodError,
    "NO_DATA_FOUND"                 -> NotFoundError,
    "INVALID_PAYLOAD"               -> DownstreamError,
    "SERVER_ERROR"                  -> DownstreamError,
    "SERVICE_UNAVAILABLE"           -> DownstreamError,
    "INCOME_SOURCEID_NOT_PROVIDED"  -> DownstreamError,
    "INVALID_CORRELATIONID"         -> DownstreamError
  )
}
