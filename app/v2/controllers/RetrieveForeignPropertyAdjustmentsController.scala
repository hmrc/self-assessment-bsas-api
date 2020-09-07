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

package v2.controllers

import cats.data.EitherT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import play.api.http.MimeTypes
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.Logging
import v2.controllers.requestParsers.RetrieveAdjustmentsRequestParser
import v2.hateoas.HateoasFactory
import v2.models.errors.{ErrorWrapper, _}
import v2.models.request.RetrieveAdjustmentsRawData
import v2.models.response.retrieveBsasAdjustments.foreignProperty.RetrieveForeignPropertyAdjustmentsHateoasData
import v2.services.{EnrolmentsAuthService, MtdIdLookupService, RetrieveForeignPropertyAdjustmentsService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveForeignPropertyAdjustmentsController @Inject()(
                                                              val authService: EnrolmentsAuthService,
                                                              val lookupService: MtdIdLookupService,
                                                              requestParser: RetrieveAdjustmentsRequestParser,
                                                              service: RetrieveForeignPropertyAdjustmentsService,
                                                              hateoasFactory: HateoasFactory,
                                                              cc: ControllerComponents
                                                            )(implicit ec: ExecutionContext)
  extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "RetrieveForeignPropertyAdjustmentsController",
      endpointName = "retrieve"
    )

  def retrieve(nino: String, bsasId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>

      val rawData = RetrieveAdjustmentsRawData(nino, bsasId)
      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          response <- EitherT(service.retrieveForeignPropertyAdjustments(parsedRequest))
          hateoasResponse <- EitherT.fromEither[Future](
            hateoasFactory.wrap(response.responseData,
              RetrieveForeignPropertyAdjustmentsHateoasData(nino, response.responseData.metadata.bsasId)).asRight[ErrorWrapper])
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with correlationId: ${response.correlationId}"
          )

          Ok(Json.toJson(hateoasResponse))
            .withApiHeaders(response.correlationId)
            .as(MimeTypes.JSON)
        }
      result.leftMap { errorWrapper =>
        val correlationId = getCorrelationId(errorWrapper)
        val result = errorResult(errorWrapper).withApiHeaders(correlationId)

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {
    (errorWrapper.error: @unchecked) match {
      case BadRequestError | NinoFormatError | BsasIdFormatError => BadRequest(Json.toJson(errorWrapper))
      case RuleNotForeignProperty | RuleNoAdjustmentsMade  => Forbidden(Json.toJson(errorWrapper))
      case NotFoundError => NotFound(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
    }
  }

}
