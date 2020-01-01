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

package v1.services

import cats.data.EitherT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.connectors.TriggerBsasConnector
import v1.controllers.EndpointLogContext
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.triggerBsas.TriggerBsasRequest
import v1.models.response.TriggerBsasResponse
import v1.support.DesResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerBsasService @Inject()(connector: TriggerBsasConnector) extends DesResponseMappingSupport with Logging{

  def triggerBsas(request: TriggerBsasRequest)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext, logContext: EndpointLogContext):
  Future[Either[ErrorWrapper, ResponseWrapper[TriggerBsasResponse]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.triggerBsas(request)).leftMap(mapDesErrors(mappingDesToMtdError))
    } yield desResponseWrapper.map(des => des)

    result.value
  }

  private def mappingDesToMtdError: Map[String, MtdError] = Map(
    "INVALID_TAXABLE_ENTITY_ID"   -> NinoFormatError,
    "ACCOUNTING_PERIOD_NOT_ENDED" -> RuleAccountingPeriodNotEndedError,
    "OBLIGATIONS_NOT_MET"         -> RulePeriodicDataIncompleteError,
    "NO_ACCOUNTING_PERIOD"        -> RuleNoAccountingPeriodError,
    "NOT_FOUND"                   -> NotFoundError,
    "INVALID_PAYLOAD"             -> DownstreamError,
    "SERVER_ERROR"                -> DownstreamError,
    "SERVICE_UNAVAILABLE"         -> DownstreamError
  )
}
