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

import api.controllers.RequestContext
import api.models
import api.models.errors._
import api.services.ServiceOutcome
import cats.data.EitherT
import cats.implicits._
import v3.connectors.RetrieveUkPropertyBsasConnector
import v3.models.domain.TypeOfBusiness
import v3.models.request.retrieveBsas.RetrieveUkPropertyBsasRequestData
import v3.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveUkPropertyBsasService @Inject()(connector: RetrieveUkPropertyBsasConnector) extends BaseRetrieveBsasService {

  protected val supportedTypesOfBusiness: Set[TypeOfBusiness] = Set(TypeOfBusiness.`uk-property-fhl`, TypeOfBusiness.`uk-property-non-fhl`)
  private val errorMap: Map[String, MtdError] = {

    val errors = Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_CALCULATION_ID" -> CalculationIdFormatError,
      "INVALID_CORRELATIONID" -> models.errors.InternalError,
      "INVALID_RETURN" -> models.errors.InternalError,
      "UNPROCESSABLE_ENTITY" -> models.errors.InternalError,
      "NO_DATA_FOUND" -> NotFoundError,
      "SERVER_ERROR" -> models.errors.InternalError,
      "SERVICE_UNAVAILABLE" -> models.errors.InternalError
    )

    val extraTysErrors: Map[String, MtdError] = Map(
      "INVALID_TAX_YEAR" -> TaxYearFormatError,
      "NOT_FOUND" -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
    )

    errors ++ extraTysErrors
  }

  def retrieve(request: RetrieveUkPropertyBsasRequestData)(implicit ctx: RequestContext,
                                                           ec: ExecutionContext): Future[ServiceOutcome[RetrieveUkPropertyBsasResponse]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.retrieve(request)).leftMap(mapDownstreamErrors(errorMap))
      mtdResponseWrapper <- EitherT.fromEither[Future](validateTypeOfBusiness(desResponseWrapper))

    } yield mtdResponseWrapper

    result.value
  }

}
