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

import cats.data.EitherT
import cats.implicits._
import javax.inject.{ Inject, Singleton }
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v3.connectors.ListBsasConnector
import v3.controllers.EndpointLogContext
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.ListBsasRequest
import v3.models.response.listBsas.{ BsasSummary, ListBsasResponse }
import v3.support.DownstreamResponseMappingSupport

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class ListBsasService @Inject()(connector: ListBsasConnector) extends DownstreamResponseMappingSupport with Logging {

  def listBsas(request: ListBsasRequest)(implicit hc: HeaderCarrier,
                                         ec: ExecutionContext,
                                         logContext: EndpointLogContext,
                                         correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[ListBsasResponse[BsasSummary]]]] = {

    val result = EitherT(connector.listBsas(request)).leftMap(mapDownstreamErrors(downstreamErrorMap))

    result.value
  }

  private def downstreamErrorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_CORRELATIONID"     -> InternalError,
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_TAXYEAR"           -> TaxYearFormatError,
      "INVALID_INCOMESOURCEID"    -> BusinessIdFormatError,
      "INVALID_INCOMESOURCE_TYPE" -> InternalError,
      "NO_DATA_FOUND"             -> NotFoundError,
      "SERVER_ERROR"              -> InternalError,
      "SERVICE_UNAVAILABLE"       -> InternalError
    )

    val extraTysErrors = Map(
      "INVALID_CORRELATION_ID"  -> InternalError,
      "INVALID_TAX_YEAR"        -> TaxYearFormatError,
      "INVALID_INCOMESOURCE_ID" -> BusinessIdFormatError,
      "NOT_FOUND"               -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED"  -> RuleTaxYearNotSupportedError
    )

    errors ++ extraTysErrors
  }
}
