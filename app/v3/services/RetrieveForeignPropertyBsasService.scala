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
import shared.controllers.RequestContext
import shared.models.errors._
import shared.services.ServiceOutcome
import v3.connectors.RetrieveForeignPropertyBsasConnector
import v3.models.domain.TypeOfBusiness
import v3.models.request.retrieveBsas.RetrieveForeignPropertyBsasRequestData
import v3.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveForeignPropertyBsasService @Inject() (connector: RetrieveForeignPropertyBsasConnector) extends BaseRetrieveBsasService {

  protected val supportedTypesOfBusiness: Set[TypeOfBusiness] = Set(TypeOfBusiness.`foreign-property`, TypeOfBusiness.`foreign-property-fhl-eea`)

  private val errorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_CALCULATION_ID"    -> CalculationIdFormatError,
      "INVALID_RETURN"            -> InternalError,
      "INVALID_CORRELATIONID"     -> InternalError,
      "NO_DATA_FOUND"             -> NotFoundError,
      "UNPROCESSABLE_ENTITY"      -> InternalError,
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

  def retrieveForeignPropertyBsas(request: RetrieveForeignPropertyBsasRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveForeignPropertyBsasResponse]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.retrieveForeignPropertyBsas(request)).leftMap(mapDownstreamErrors(errorMap))
      mtdResponseWrapper <- EitherT.fromEither[Future](validateTypeOfBusiness(desResponseWrapper))
    } yield mtdResponseWrapper

    result.value
  }

}
