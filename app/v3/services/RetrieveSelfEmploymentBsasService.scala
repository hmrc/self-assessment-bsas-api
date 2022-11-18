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
import v3.connectors.RetrieveSelfEmploymentBsasConnector
import v3.controllers.EndpointLogContext
import v3.models.domain.TypeOfBusiness
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.retrieveBsas.selfEmployment.RetrieveSelfEmploymentBsasRequestData
import v3.models.response.retrieveBsas.selfEmployment.RetrieveSelfEmploymentBsasResponse
import v3.support.DesResponseMappingSupport

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class RetrieveSelfEmploymentBsasService @Inject()(connector: RetrieveSelfEmploymentBsasConnector)
    extends BaseRetrieveBsasService
    with DesResponseMappingSupport
    with Logging {

  protected val supportedTypesOfBusiness: Set[TypeOfBusiness] = Set(TypeOfBusiness.`self-employment`)

  def retrieveSelfEmploymentBsas(request: RetrieveSelfEmploymentBsasRequestData)(
      implicit hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[RetrieveSelfEmploymentBsasResponse]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.retrieveSelfEmploymentBsas(request)).leftMap(mapDesErrors(mappingDesToMtdError))
      mtdResponseWrapper <- EitherT.fromEither[Future](validateTypeOfBusiness(desResponseWrapper))
    } yield mtdResponseWrapper

    result.value
  }

  private def mappingDesToMtdError: Map[String, MtdError] = Map(
    "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
    "INVALID_CALCULATION_ID"    -> CalculationIdFormatError,
    "INVALID_CORRELATIONID"     -> InternalError,
    "INVALID_RETURN"            -> InternalError,
    "UNPROCESSABLE_ENTITY"      -> InternalError,
    "NO_DATA_FOUND"             -> NotFoundError,
    "SERVER_ERROR"              -> InternalError,
    "SERVICE_UNAVAILABLE"       -> InternalError
  )
}
