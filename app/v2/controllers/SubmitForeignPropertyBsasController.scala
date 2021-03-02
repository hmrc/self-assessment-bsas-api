/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.controllers

import cats.data.EitherT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import utils.{IdGenerator, Logging}
import v2.controllers.requestParsers.SubmitForeignPropertyBsasRequestParser
import v2.hateoas.HateoasFactory
import v2.models.errors._
import v2.models.request.submitBsas.foreignProperty.SubmitForeignPropertyRawData
import v2.models.response.SubmitForeignPropertyBsasHateoasData
import v2.services.{EnrolmentsAuthService, MtdIdLookupService, SubmitForeignPropertyBsasService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitForeignPropertyBsasController @Inject()(val authService: EnrolmentsAuthService,
                                                     val lookupService: MtdIdLookupService,
                                                     parser: SubmitForeignPropertyBsasRequestParser,
                                                     service: SubmitForeignPropertyBsasService,
                                                     hateoasFactory: HateoasFactory,
                                                     cc: ControllerComponents,
                                                    val idGenerator: IdGenerator)(implicit ec: ExecutionContext)
  extends AuthorisedController(cc) with BaseController with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SubmitForeignPropertyBsasController", endpointName = "SubmitForeignPropertyBsas")
  def handleRequest(nino: String, bsasId: String): Action[JsValue] =

    authorisedAction(nino).async(parse.json) { implicit request =>

      implicit val correlationId: String = idGenerator.generateCorrelationId
      logger.info(
        s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with CorrelationId: $correlationId")

      val rawData = SubmitForeignPropertyRawData(nino, bsasId, request.body)
      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](parser.parseRequest(rawData))
          serviceResponse <- EitherT(service.submitForeignPropertyBsas(parsedRequest))
          vendorResponse <- EitherT.fromEither[Future](
            hateoasFactory.wrap(serviceResponse.responseData, SubmitForeignPropertyBsasHateoasData(nino, bsasId)).asRight[ErrorWrapper])
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

          Ok(Json.toJson(vendorResponse))
            .withApiHeaders(serviceResponse.correlationId)
        }

      result.leftMap { errorWrapper =>
        val resCorrelationId = errorWrapper.correlationId
        val result = errorResult(errorWrapper).withApiHeaders(resCorrelationId)
        logger.info(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: $resCorrelationId")
        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {

    (errorWrapper.error: @unchecked) match {
      case BadRequestError |
           NinoFormatError |
           BsasIdFormatError |
           CustomMtdError(FormatAdjustmentValueError.code) |
           CustomMtdError(RuleAdjustmentRangeInvalid.code) |
           CustomMtdError(RuleIncorrectOrEmptyBodyError.code) |
           CustomMtdError(RuleCountryCodeError.code) |
           CustomMtdError(CountryCodeFormatError.code) |
           RuleBothExpensesError => BadRequest(Json.toJson(errorWrapper))
      case RuleTypeOfBusinessError |
           RuleSummaryStatusInvalid |
           RuleSummaryStatusSuperseded |
           RuleBsasAlreadyAdjusted |
           RuleResultingValueNotPermitted |
           RuleOverConsolidatedExpensesThreshold |
           RulePropertyIncomeAllowanceClaimed => Forbidden(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
      case NotFoundError => NotFound(Json.toJson(errorWrapper))
    }
  }
}
