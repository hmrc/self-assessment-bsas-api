/*
 * Copyright 2021 HM Revenue & Customs
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
import v2.connectors.RetrieveForeignPropertyBsasConnector
import v2.controllers.EndpointLogContext
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasRequestData
import v2.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasResponse
import v2.support.DesResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveForeignPropertyBsasService @Inject()(connector: RetrieveForeignPropertyBsasConnector) extends DesResponseMappingSupport with Logging{

  def retrieveForeignPropertyBsas(request: RetrieveForeignPropertyBsasRequestData)(
  implicit hc: HeaderCarrier, ec: ExecutionContext, logContext: EndpointLogContext,
  correlationId: String):
  Future[Either[ErrorWrapper, ResponseWrapper[RetrieveForeignPropertyBsasResponse]]] = {

  val result = for {
  desResponseWrapper <-
  EitherT(connector.retrieveForeignPropertyBsas(request)).leftMap(mapDesErrors(mappingDesToMtdError))
  mtdResponseWrapper <- EitherT.fromEither[Future](validateRetrieveForeignPropertyBsasSuccessResponse(desResponseWrapper))
} yield mtdResponseWrapper

  result.value
}

  private def mappingDesToMtdError: Map[String, MtdError] = Map(
  "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
  "INVALID_CORRELATION_ID" -> DownstreamError,
  "UNPROCESSABLE_ENTITY" -> RuleNoAdjustmentsMade,
  "NO_DATA_FOUND" -> NotFoundError,
  "SERVER_ERROR" -> DownstreamError,
  "SERVICE_UNAVAILABLE" -> DownstreamError
  )
}
