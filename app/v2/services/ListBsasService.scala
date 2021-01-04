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
import v2.connectors.ListBsasConnector
import v2.controllers.EndpointLogContext
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.ListBsasRequest
import v2.models.response.listBsas.{BsasEntries, ListBsasResponse}
import v2.support.DesResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListBsasService @Inject()(connector: ListBsasConnector) extends DesResponseMappingSupport with Logging {

  def listBsas(request: ListBsasRequest)
              (implicit hc: HeaderCarrier, ec: ExecutionContext, logContext: EndpointLogContext,
               correlationId: String):
  Future[Either[ErrorWrapper, ResponseWrapper[ListBsasResponse[BsasEntries]]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.listBsas(request)).leftMap(mapDesErrors(mappingDesToMtdError))
    } yield desResponseWrapper.map(des => des)

    result.value
  }

  private def mappingDesToMtdError: Map[String, MtdError] = Map(
    "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
    "NOT_FOUND" -> NotFoundError,
    "INVALID_TAXYEAR" -> DownstreamError,
    "INVALID_INCOMESOURCE_IDENTIFIER" -> DownstreamError,
    "INVALID_IDENTIFIER_VALUE" -> DownstreamError,
    "SERVER_ERROR" -> DownstreamError,
    "SERVICE_UNAVAILABLE" -> DownstreamError
  )
}
