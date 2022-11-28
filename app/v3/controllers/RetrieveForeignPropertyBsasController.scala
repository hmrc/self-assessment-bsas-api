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

package v3.controllers

import cats.data.EitherT
import cats.implicits._
import play.api.libs.json.Json
import play.api.mvc.{ Action, AnyContent, ControllerComponents }
import utils.{ IdGenerator, Logging }
import v3.models.request.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasRawData
import v3.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyHateoasData
import v3.controllers.requestParsers.RetrieveForeignPropertyRequestParser
import v3.hateoas.HateoasFactory
import v3.models.errors._
import v3.services.{ EnrolmentsAuthService, MtdIdLookupService, RetrieveForeignPropertyBsasService }

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class RetrieveForeignPropertyBsasController @Inject()(
    val authService: EnrolmentsAuthService,
    val lookupService: MtdIdLookupService,
    requestParser: RetrieveForeignPropertyRequestParser,
    service: RetrieveForeignPropertyBsasService,
    hateoasFactory: HateoasFactory,
    cc: ControllerComponents,
    val idGenerator: IdGenerator
)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "RetrieveForeignPropertyBsasController",
      endpointName = "retrieve"
    )

  def retrieve(nino: String, calculationId: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val correlationId: String = idGenerator.generateCorrelationId

      logger.info(
        s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with CorrelationId: $correlationId")

      val rawData = RetrieveForeignPropertyBsasRawData(nino, calculationId, taxYear)

      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          response      <- EitherT(service.retrieveForeignPropertyBsas(parsedRequest))
        } yield {
          val hateoasData    = RetrieveForeignPropertyHateoasData(nino, calculationId, None)
          val vendorResponse = hateoasFactory.wrap(response.responseData, hateoasData)

          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with correlationId: ${response.correlationId}"
          )

          Ok(Json.toJson(vendorResponse))
            .withApiHeaders(response.correlationId)
        }

      result.leftMap { errorWrapper =>
        val resCorrelationId = errorWrapper.correlationId
        logger.info(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: $resCorrelationId")

        errorResult(errorWrapper).withApiHeaders(resCorrelationId)
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) =
    errorWrapper.error match {
      case _
          if errorWrapper.containsAnyOf(
            BadRequestError,
            NinoFormatError,
            TaxYearFormatError,
            RuleTaxYearRangeInvalidError,
            InvalidTaxYearParameterError,
            RuleTaxYearNotSupportedError,
            RuleTypeOfBusinessIncorrectError,
            CalculationIdFormatError
          ) =>
        BadRequest(Json.toJson(errorWrapper))
      case NotFoundError => NotFound(Json.toJson(errorWrapper))
      case InternalError => InternalServerError(Json.toJson(errorWrapper))
      case _             => unhandledError(errorWrapper)
    }
}
