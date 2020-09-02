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

package v2.support

import utils.Logging
import v2.controllers.EndpointLogContext
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasResponse
import v2.models.response.retrieveBsas.selfEmployment.RetrieveSelfEmploymentBsasResponse
import v2.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse
import v2.models.response.retrieveBsasAdjustments.foreignProperty.RetrieveForeignPropertyAdjustmentsResponse
import v2.models.response.retrieveBsasAdjustments.selfEmployment.RetrieveSelfEmploymentAdjustmentsResponse
import v2.models.response.retrieveBsasAdjustments.ukProperty.RetrieveUkPropertyAdjustmentsResponse
import v2.models.response.{SubmitForeignPropertyBsasResponse, SubmitSelfEmploymentBsasResponse, SubmitUkPropertyBsasResponse, retrieveBsas, retrieveBsasAdjustments}

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

  final def validateRetrieveUkPropertyAdjustmentsSuccessResponse[T](
      desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveUkPropertyAdjustmentsResponse(retrieveBsasAdjustments.ukProperty.Metadata(typeOfBusiness, _, _, _, _, _, _), _)
          if !List(TypeOfBusiness.`uk-property-fhl`, TypeOfBusiness.`uk-property-non-fhl`).contains(typeOfBusiness) =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleNotUkProperty, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateRetrieveSelfEmploymentAdjustmentsSuccessResponse[T](
      desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveSelfEmploymentAdjustmentsResponse(retrieveBsasAdjustments.selfEmployment.Metadata(typeOfBusiness, _, _, _, _, _, _, _), _)
          if typeOfBusiness != TypeOfBusiness.`self-employment` =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleNotSelfEmployment, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateRetrieveForeignPropertyAdjustmentsSuccessResponse[T](
                                                                               desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveForeignPropertyAdjustmentsResponse(retrieveBsasAdjustments.foreignProperty.Metadata(typeOfBusiness, _, _, _, _, _, _), _)
        if !List(TypeOfBusiness.`foreign-property`, TypeOfBusiness.`foreign-property-fhl-eea`).contains(typeOfBusiness) =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleNotForeignProperty, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateRetrieveForeignPropertyBsasSuccessResponse[T](
                                                                          desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveForeignPropertyBsasResponse(retrieveBsas.foreignProperty.Metadata(typeOfBusiness, _, _, _, _, _, _), _)
        if !List(TypeOfBusiness.`foreign-property`, TypeOfBusiness.`foreign-property-fhl-eea`).contains(typeOfBusiness) =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleNotForeignProperty, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateRetrieveSelfEmploymentBsasSuccessResponse[T](desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveSelfEmploymentBsasResponse(retrieveBsas.selfEmployment.Metadata(typeOfBusiness, _, _, _, _, _, _, _), _)
          if typeOfBusiness != TypeOfBusiness.`self-employment` =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleNotSelfEmployment, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateSubmitSelfEmploymentSuccessResponse[T](desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case SubmitSelfEmploymentBsasResponse(_, typeOfBusiness) if typeOfBusiness != TypeOfBusiness.`self-employment` =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleErrorPropertyAdjusted, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateRetrieveUkPropertyBsasSuccessResponse[T](desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveUkPropertyBsasResponse(retrieveBsas.ukProperty.Metadata(typeOfBusiness, _, _, _, _, _, _), _)
          if !List(TypeOfBusiness.`uk-property-fhl`, TypeOfBusiness.`uk-property-non-fhl`).contains(typeOfBusiness) =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleNotUkProperty, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateSubmitUkPropertyBsasSuccessResponse[T](desResponseWrapper: ResponseWrapper[T],
                                                           optionalTypeOfBusiness: Option[TypeOfBusiness]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case SubmitUkPropertyBsasResponse(_, typeOfBusiness)
          if !List(TypeOfBusiness.`uk-property-fhl`, TypeOfBusiness.`uk-property-non-fhl`).contains(typeOfBusiness) =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleSelfEmploymentAdjustedError, None))

      case SubmitUkPropertyBsasResponse(_, typeOfBusiness) if optionalTypeOfBusiness.exists(_ != typeOfBusiness) =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleIncorrectPropertyAdjusted, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateSubmitForeignPropertyBsasSuccessResponse[T](desResponseWrapper: ResponseWrapper[T],
                                                           businessType: TypeOfBusiness): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case SubmitForeignPropertyBsasResponse(_, typeOfBusiness)
        if !List(TypeOfBusiness.`foreign-property`, TypeOfBusiness.`foreign-property-fhl-eea`).contains(typeOfBusiness) =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleSelfEmploymentAdjustedError, None))

      case SubmitForeignPropertyBsasResponse(_, typeOfBusiness) if businessType != typeOfBusiness =>
        Left(ErrorWrapper(Some(desResponseWrapper.correlationId), RuleIncorrectPropertyAdjusted, None))

      case _ => Right(desResponseWrapper)
    }
}
