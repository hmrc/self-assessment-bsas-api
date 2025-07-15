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

package v5.ukPropertyBsas.retrieve

import cats.data.EitherT
import cats.implicits.*
import shared.controllers.RequestContext
import shared.models.errors.*
import shared.services.ServiceOutcome
import v5.common.BaseRetrieveBsasService
import v5.common.model.TypeOfBusiness
import v5.ukPropertyBsas.retrieve.model.request.RetrieveUkPropertyBsasRequestData
import v5.ukPropertyBsas.retrieve.model.response.RetrieveUkPropertyBsasResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveUkPropertyBsasService @Inject() (connector: RetrieveUkPropertyBsasConnector) extends BaseRetrieveBsasService {

  protected val supportedTypesOfBusiness: Set[TypeOfBusiness] = Set(TypeOfBusiness.`uk-property-fhl`, TypeOfBusiness.`uk-property-non-fhl`)

  private val errorMap: Map[String, MtdError] = {

    val errors = Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_CALCULATION_ID"    -> CalculationIdFormatError,
      "INVALID_CORRELATIONID"     -> InternalError,
      "INVALID_CORRELATION_ID"    -> InternalError,
      "INVALID_RETURN"            -> InternalError,
      "UNPROCESSABLE_ENTITY"      -> InternalError,
      "NO_DATA_FOUND"             -> NotFoundError,
      "SERVER_ERROR"              -> InternalError,
      "SERVICE_UNAVAILABLE"       -> InternalError
    )

    val extraTysErrors: Map[String, MtdError] = Map(
      "INVALID_TAX_YEAR"       -> TaxYearFormatError,
      "NOT_FOUND"              -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
    )

    errors ++ extraTysErrors
  }

  def retrieve(request: RetrieveUkPropertyBsasRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveUkPropertyBsasResponse]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.retrieve(request)).leftMap(mapDownstreamErrors(errorMap))
      mtdResponseWrapper <- EitherT.fromEither[Future](validateTypeOfBusiness(desResponseWrapper))

    } yield mtdResponseWrapper

    result.value
  }

}
