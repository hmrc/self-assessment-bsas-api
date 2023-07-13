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

package v2.services

import api.controllers.RequestContext
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.BaseService
import cats.data.EitherT
import cats.implicits._
import v2.connectors.RetrieveForeignPropertyBsasConnector
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import v2.models.request.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasRequestData
import v2.models.response.retrieveBsas
import v2.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveForeignPropertyBsasService @Inject()(connector: RetrieveForeignPropertyBsasConnector) extends BaseService {

  private val errorMap: Map[String, MtdError] =
    Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_CORRELATION_ID" -> InternalError,
      "INVALID_RETURN" -> AdjustedStatusFormatError,
      "INVALID_CALCULATION_ID" -> BsasIdFormatError,
      "UNPROCESSABLE_ENTITY" -> RuleNoAdjustmentsMade,
      "NO_DATA_FOUND" -> NotFoundError,
      "SERVER_ERROR" -> InternalError,
      "SERVICE_UNAVAILABLE" -> InternalError
    )

  def retrieveForeignPropertyBsas(request: RetrieveForeignPropertyBsasRequestData)(
    implicit ctx: RequestContext,
    ec: ExecutionContext): Future[Either[ErrorWrapper, ResponseWrapper[RetrieveForeignPropertyBsasResponse]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.retrieveForeignPropertyBsas(request)).leftMap(mapDownstreamErrors(errorMap))
      mtdResponseWrapper <- EitherT.fromEither[Future](validateRetrieveForeignPropertyBsasSuccessResponse(desResponseWrapper))
    } yield mtdResponseWrapper

    result.value
  }

  private def validateRetrieveForeignPropertyBsasSuccessResponse[T](
                                                                     desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveForeignPropertyBsasResponse(retrieveBsas.foreignProperty.Metadata(typeOfBusiness, _, _, _, _, _, _), _)
        if !List(TypeOfBusiness.`foreign-property`, TypeOfBusiness.`foreign-property-fhl-eea`).contains(typeOfBusiness) =>
        Left(ErrorWrapper(desResponseWrapper.correlationId, RuleNotForeignProperty, None))

      case _ => Right(desResponseWrapper)
    }
}
