/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.services

import cats.data.EitherT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.connectors.SubmitUkPropertyBsasConnector
import v1.controllers.EndpointLogContext
import v1.models.domain.TypeOfBusiness
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.submitBsas.{SubmitUKPropertyBsasRequestBody, SubmitUkPropertyBsasRequestData}
import v1.models.response.SubmitUkPropertyBsasResponse
import v1.support.DesResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitUkPropertyBsasService @Inject()(connector: SubmitUkPropertyBsasConnector) extends DesResponseMappingSupport with Logging {

  def submitPropertyBsas(request: SubmitUkPropertyBsasRequestData)(
                implicit hc: HeaderCarrier, ec: ExecutionContext, logContext: EndpointLogContext):
  Future[Either[ErrorWrapper, ResponseWrapper[SubmitUkPropertyBsasResponse]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.submitPropertyBsas(request)).leftMap(mapDesErrors(mappingDesToMtdError))
      mtdResponseWrapper <-
        EitherT.fromEither[Future](validateSubmitUkPropertyBsasSuccessResponse(desResponseWrapper, Some(retrieveTypeOfBusiness(request.body))))
    } yield mtdResponseWrapper

    result.value
  }

  private def mappingDesToMtdError: Map[String, MtdError] = Map(
    "INVALID_TAXABLE_ENTITY_ID"   -> NinoFormatError,
    "INVALID_CALCULATION_ID"      -> BsasIdFormatError,
    "INVALID_PAYLOAD"     -> DownstreamError,
    "INVALID_PAYLOAD_REMOTE"      -> DownstreamError,
    "INVALID_FIELD"               -> RuleTypeOfBusinessError,
    "INVALID_MONETARY_FORMAT"     -> DownstreamError,
    "ASC_ID_INVALID"              -> RuleSummaryStatusInvalid,
    "ASC_ALREADY_SUPERSEDED"      -> RuleSummaryStatusSuperseded,
    "ASC_ALREADY_ADJUSTED"        -> RuleBsasAlreadyAdjusted,
    "UNALLOWABLE_VALUE"          -> RuleResultingValueNotPermitted,
    "BVR_FAILURE_C55316"          -> RuleTypeOfBusinessError,
    "BVR_FAILURE_C15320"          -> RuleTypeOfBusinessError,
    "BVR_FAILURE_C55503"          -> RuleOverConsolidatedExpensesThreshold,
    "BVR_FAILURE_C55508"          -> RulePropertyIncomeAllowanceClaimed,
    "BVR_FAILURE_C55509"          -> RulePropertyIncomeAllowanceClaimed,
    "NOT_FOUND"                   -> NotFoundError,
    "SERVER_ERROR"                -> DownstreamError,
    "SERVICE_UNAVAILABLE"         -> DownstreamError
  )

  private def retrieveTypeOfBusiness(body: SubmitUKPropertyBsasRequestBody): TypeOfBusiness = {
    (body: @unchecked) match {
      case SubmitUKPropertyBsasRequestBody(Some(_), None) => TypeOfBusiness.`uk-property-non-fhl`
      case SubmitUKPropertyBsasRequestBody(None, Some(_)) => TypeOfBusiness.`uk-property-fhl`
    }
  }
}
