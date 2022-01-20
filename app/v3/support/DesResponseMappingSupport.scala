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

package v3.support

import utils.Logging
import v3.controllers.EndpointLogContext
import v3.models.domain.TypeOfBusiness
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasResponse
import v3.models.response.retrieveBsas.selfEmployment.RetrieveSelfEmploymentBsasResponse
import v3.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse
import v3.models.response.retrieveBsasAdjustments.foreignProperty.RetrieveForeignPropertyAdjustmentsResponse
import v3.models.response.retrieveBsasAdjustments.selfEmployment.RetrieveSelfEmploymentAdjustmentsResponse
import v3.models.response.retrieveBsasAdjustments.ukProperty.RetrieveUkPropertyAdjustmentsResponse
import v3.models.response.{retrieveBsas, retrieveBsasAdjustments}

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
        ErrorWrapper(correlationId, errorCodeMap.applyOrElse(error.code, defaultErrorCodeMapping), None)

      case ResponseWrapper(correlationId, DesErrors(errorCodes)) =>
        val mtdErrors = errorCodes.map(error => errorCodeMap.applyOrElse(error.code, defaultErrorCodeMapping))

        if (mtdErrors.contains(DownstreamError)) {
          logger.info(
            s"[${logContext.controllerName}] [${logContext.endpointName}] [CorrelationId - $correlationId]" +
              s" - downstream returned ${errorCodes.map(_.code).mkString(",")}. Revert to ISE")

          ErrorWrapper(correlationId, DownstreamError, None)
        } else {
          ErrorWrapper(correlationId, BadRequestError, Some(mtdErrors))
        }

      case ResponseWrapper(correlationId, OutboundError(error, errors)) =>
        ErrorWrapper(correlationId, error, errors)
    }
  }

  final def validateRetrieveUkPropertyAdjustmentsSuccessResponse[T](
      desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveUkPropertyAdjustmentsResponse(retrieveBsasAdjustments.ukProperty.Metadata(typeOfBusiness, _, _, _, _, _, _, _), _)
          if !List(TypeOfBusiness.`uk-property-fhl`, TypeOfBusiness.`uk-property-non-fhl`).contains(typeOfBusiness) =>
        Left(ErrorWrapper(desResponseWrapper.correlationId, RuleNotUkProperty, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateRetrieveSelfEmploymentAdjustmentsSuccessResponse[T](
      desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveSelfEmploymentAdjustmentsResponse(retrieveBsasAdjustments.selfEmployment.Metadata(typeOfBusiness, _, _, _, _, _, _, _), _)
          if typeOfBusiness != TypeOfBusiness.`self-employment` =>
        Left(ErrorWrapper(desResponseWrapper.correlationId, RuleNotSelfEmployment, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateRetrieveForeignPropertyAdjustmentsSuccessResponse[T](
                                                                               desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveForeignPropertyAdjustmentsResponse(retrieveBsasAdjustments.foreignProperty.Metadata(typeOfBusiness, _, _, _, _, _, _, _), _)
        if !List(TypeOfBusiness.`foreign-property`, TypeOfBusiness.`foreign-property-fhl-eea`).contains(typeOfBusiness) =>
        Left(ErrorWrapper(desResponseWrapper.correlationId, RuleNotForeignProperty, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateRetrieveForeignPropertyBsasSuccessResponse[T](
                                                                          desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveForeignPropertyBsasResponse(retrieveBsas.foreignProperty.Metadata(typeOfBusiness, _, _, _, _, _, _), _)
        if !List(TypeOfBusiness.`foreign-property`, TypeOfBusiness.`foreign-property-fhl-eea`).contains(typeOfBusiness) =>
        Left(ErrorWrapper(desResponseWrapper.correlationId, RuleNotForeignProperty, None))

      case _ => Right(desResponseWrapper)
    }

  final def validateRetrieveSelfEmploymentBsasSuccessResponse[T](desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveSelfEmploymentBsasResponse(_, retrieveBsas.selfEmployment.Inputs(typeOfBusiness, _, _, _, _, _, _), _, _, _)
          if typeOfBusiness != TypeOfBusiness.`self-employment` =>
        Left(ErrorWrapper(desResponseWrapper.correlationId, RuleNotSelfEmployment, None))

      case _ => Right(desResponseWrapper)
    }


  final def validateRetrieveUkPropertyBsasSuccessResponse[T](desResponseWrapper: ResponseWrapper[T]): Either[ErrorWrapper, ResponseWrapper[T]] =
    desResponseWrapper.responseData match {
      case RetrieveUkPropertyBsasResponse(retrieveBsas.ukProperty.Metadata(typeOfBusiness, _, _, _, _, _, _, _), _)
          if !List(TypeOfBusiness.`uk-property-fhl`, TypeOfBusiness.`uk-property-non-fhl`).contains(typeOfBusiness) =>
        Left(ErrorWrapper(desResponseWrapper.correlationId, RuleNotUkProperty, None))

      case _ => Right(desResponseWrapper)
    }
}
