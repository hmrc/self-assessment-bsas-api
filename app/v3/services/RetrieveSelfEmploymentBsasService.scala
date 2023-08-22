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
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.ServiceOutcome
import cats.data.EitherT
import cats.implicits._
import v3.connectors.RetrieveSelfEmploymentBsasConnector
import v3.models.domain.TypeOfBusiness
import v3.models.request.retrieveBsas.RetrieveSelfEmploymentBsasRequestData
import v3.models.response.retrieveBsas.selfEmployment.{RetrieveSelfEmploymentBsasResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveSelfEmploymentBsasService @Inject() (connector: RetrieveSelfEmploymentBsasConnector) extends BaseRetrieveBsasService {

  protected val supportedTypesOfBusiness: Set[TypeOfBusiness] = Set(TypeOfBusiness.`self-employment`)

  private val errorMap: Map[String, MtdError] = {

    val errors = Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_CALCULATION_ID"    -> CalculationIdFormatError,
      "INVALID_CORRELATIONID"     -> InternalError,
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
      validatedResponse  <- EitherT.fromEither[Future](validateAdjustableSummaryCalculation(mtdResponseWrapper))
    } yield validatedResponse

    result.value
  }

  private def validateAdjustableSummaryCalculation(
      responseWrapper: ResponseWrapper[RetrieveSelfEmploymentBsasResponse]): ServiceOutcome[RetrieveSelfEmploymentBsasResponse] = {
    if (validateSummaryCalculationExpenses(responseWrapper) && validateSummaryCalculationAdditions(responseWrapper)) {
      Right(responseWrapper)
    } else {
      logger.warn("Unexpected negative value returned from downstream.")
      Left(ErrorWrapper(responseWrapper.correlationId, InternalError, None))
    }
  }

  private def isPositive(property: BigDecimal): Boolean = property >= 0

  private def validateSummaryCalculationAdditions(responseWrapper: ResponseWrapper[RetrieveSelfEmploymentBsasResponse]): Boolean = {
    val additions = responseWrapper.responseData.adjustableSummaryCalculation.additions
    additions match {
      case None => true
      case Some(add) =>
        List(
          add.paymentsToSubcontractorsDisallowable,
          add.wagesAndStaffCostsDisallowable,
          add.carVanTravelExpensesDisallowable,
          add.adminCostsDisallowable,
          add.professionalFeesDisallowable,
          add.otherExpensesDisallowable,
          add.advertisingCostsDisallowable,
          add.businessEntertainmentCostsDisallowable
        ).forall(_.forall(isPositive))
    }
  }

  def validateSummaryCalculationExpenses(responseWrapper: ResponseWrapper[RetrieveSelfEmploymentBsasResponse]): Boolean = {
    val expenses = responseWrapper.responseData.adjustableSummaryCalculation.expenses
    expenses match {
      case None => true
      case Some(exp) =>
        List(
          exp.consolidatedExpenses,
          exp.paymentsToSubcontractorsAllowable,
          exp.wagesAndStaffCostsAllowable,
          exp.carVanTravelExpensesAllowable,
          exp.adminCostsAllowable,
          exp.otherExpensesAllowable,
          exp.advertisingCostsAllowable,
          exp.businessEntertainmentCostsAllowable
        ).forall(_.forall(isPositive))
    }
  }

}
