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

package v1.support

import utils.Logging
import v1.controllers.EndpointLogContext
import v1.models.domain.TypeOfBusiness
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.response.{SubmitSelfEmploymentBsasResponse, SubmitUkPropertyBsasResponse}
import v1.models.response.retrieveBsas.selfEmployment.RetrieveSelfEmploymentBsasResponse
import v1.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse
import v1.models.response.retrieveBsasAdjustments.RetrieveSelfEmploymentAdjustmentResponse

trait DesResponseMappingSupport {
  self: Logging =>

  final def mapDesErrors[D](errorCodeMap: PartialFunction[String, MtdError])(desResponseWrapper: ResponseWrapper[DesError])(
    implicit logContext: EndpointLogContext): ErrorWrapper = {

    lazy val defaultErrorCodeMapping: String => MtdError = { code =>
      logger.info(s"[${logContext.controllerName}] [${logContext.endpointName}] - No mapping found for error code $code")
      DownstreamError
    }

    desResponseWrapper match {
      case ResponseWrapper(correlationId, DesErrors(error :: Nil)) =>
        ErrorWrapper(Some(correlationId), errorCodeMap.applyOrElse(error.code, defaultErrorCodeMapping), None)

      case ResponseWrapper(correlationId, DesErrors(errorCodes)) =>
        val mtdErrors = errorCodes.map(error => errorCodeMap.applyOrElse(error.code, defaultErrorCodeMapping))

        if (mtdErrors.contains(DownstreamError)) {
          logger.info(
            s"[${logContext.controllerName}] [${logContext.endpointName}] [CorrelationId - $correlationId]" +
              s" - downstream returned ${errorCodes.map(_.code).mkString(",")}. Revert to ISE")

          ErrorWrapper(Some(correlationId), DownstreamError, None)
        } else {
          ErrorWrapper(Some(correlationId), BadRequestError, Some(mtdErrors))
        }

      case ResponseWrapper(correlationId, OutboundError(error, errors)) =>
        ErrorWrapper(Some(correlationId), error, errors)
    }
  }

  final def validateRetrieveSelfEmploymentAdjustmentsSuccessResponse[T](desResponseWrapper: ResponseWrapper[T], typeOfBusiness: Option[TypeOfBusiness]):
  Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case retrieveSelfEmploymentAdjustmentsResponse: RetrieveSelfEmploymentAdjustmentResponse
        if retrieveSelfEmploymentAdjustmentsResponse.metadata.typeOfBusiness != TypeOfBusiness.`self-employment` =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleNotSelfEmployment, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateRetrieveSelfEmploymentBsasSuccessResponse[T](desResponseWrapper: ResponseWrapper[T], typeOfBusiness: Option[TypeOfBusiness]):
  Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case retrieveSelfEmploymentBsasResponse: RetrieveSelfEmploymentBsasResponse
        if retrieveSelfEmploymentBsasResponse.metadata.typeOfBusiness != TypeOfBusiness.`self-employment` =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleNotSelfEmployment, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateSubmitSelfEmploymentSuccessResponse[T](desResponseWrapper: ResponseWrapper[T], typeOfBusiness: Option[TypeOfBusiness]):
  Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case submitSelfEmploymentBsasResponse: SubmitSelfEmploymentBsasResponse
        if TypeOfBusiness.`self-employment` != submitSelfEmploymentBsasResponse.typeOfBusiness =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleErrorPropertyAdjusted, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateRetrieveUkPropertyBsasSuccessResponse[T](desResponseWrapper: ResponseWrapper[T], typeOfBusiness: Option[TypeOfBusiness]):
  Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case retrieveUkPropertyBsasResponse: RetrieveUkPropertyBsasResponse
        if retrieveUkPropertyBsasResponse.metadata.typeOfBusiness == TypeOfBusiness.`self-employment` =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleNotUkProperty, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateSubmitUkPropertyBsasSuccessResponse[T](desResponseWrapper: ResponseWrapper[T], typeOfBusiness: Option[TypeOfBusiness]):
  Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case submitUkPropertyBsasResponse: SubmitUkPropertyBsasResponse
        if submitUkPropertyBsasResponse.typeOfBusiness == TypeOfBusiness.`self-employment` =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleSelfEmploymentAdjustedError, None))

      case submitUkPropertyBsasResponse: SubmitUkPropertyBsasResponse
        if typeOfBusiness.exists(_ != submitUkPropertyBsasResponse.typeOfBusiness) =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleIncorrectPropertyAdjusted, None))

      case _ => Right(desResponseWrapper)
    }
}
