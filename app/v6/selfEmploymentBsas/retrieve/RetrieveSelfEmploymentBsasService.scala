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

package v6.selfEmploymentBsas.retrieve

import cats.data.EitherT
import cats.implicits.*
import shared.controllers.RequestContext
import shared.models.errors.*
import shared.services.ServiceOutcome
import v6.common.BaseRetrieveBsasService
import v6.common.model.IncomeSourceType
import v6.selfEmploymentBsas.retrieve.model.request.RetrieveSelfEmploymentBsasRequestData
import v6.selfEmploymentBsas.retrieve.model.response.RetrieveSelfEmploymentBsasResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveSelfEmploymentBsasService @Inject() (connector: RetrieveSelfEmploymentBsasConnector) extends BaseRetrieveBsasService {

  protected val supportedIncomeSourceType: Set[String] = Set(IncomeSourceType.`01`.toString)

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

    val extraTysErrors = Map(
      "INVALID_TAX_YEAR"       -> TaxYearFormatError,
      "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError,
      "NOT_FOUND"              -> NotFoundError
    )

    errors ++ extraTysErrors
  }

  def retrieveSelfEmploymentBsas(request: RetrieveSelfEmploymentBsasRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveSelfEmploymentBsasResponse]] = {

    val result = for {
      responseWrapper    <- EitherT(connector.retrieveSelfEmploymentBsas(request)).leftMap(mapDownstreamErrors(errorMap))
      mtdResponseWrapper <- EitherT.fromEither[Future](validateTypeOfBusiness(responseWrapper))
      mtdResponseWrapper <- EitherT.fromEither[Future](checkTaxYear(request.taxYear, mtdResponseWrapper))
    } yield mtdResponseWrapper

    result.value
  }

}
