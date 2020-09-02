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
import javax.inject.Inject
import play.api.http.MimeTypes
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.Logging
import v2.hateoas.HateoasFactory
import v2.models.errors._
import v2.models.request.RetrieveAdjustmentsRawData
import v2.models.response.retrieveBsasAdjustments.selfEmployment.RetrieveSelfEmploymentAdjustmentsHateoasData
import v2.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}

import scala.concurrent.{ExecutionContext, Future}

class RetrieveForeignPropertyBsasController @Inject()(
                                                       val authService: EnrolmentsAuthService,
                                                       val lookupService: MtdIdLookupService,
                                                       requestParser: RetrieveForeignPropertyBsasRequestParser,
                                                       service: RetrieveForeignPropertyBsasService,
                                                       hateoasFactory: HateoasFactory,
                                                       auditService: AuditService,
                                                       cc: ControllerComponents
                                                     )(implicit ec: ExecutionContext)
  extends AuthorisedController(cc)
    with BaseController
    with Logging {


  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "RetrieveSelfEmploymentAdjustmentsController",
      endpointName = "retrieve"
    )

  def retrieve(nino: String, bsasId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>

      val rawData = RetrieveAdjustmentsRawData(nino, bsasId)
      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          response <- EitherT(service.retrieveForeignPropertyBsas(parsedRequest))
          hateoasResponse <- EitherT.fromEither[Future](
            hateoasFactory.wrap(response.responseData,
              RetrieveSelfEmploymentAdjustmentsHateoasData(nino, response.responseData.metadata.bsasId)).asRight[ErrorWrapper])
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
        errorResult(errorWrapper).withApiHeaders(correlationId)
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {
    (errorWrapper.error: @unchecked) match {
      case BadRequestError |
           NinoFormatError |
           BsasIdFormatError |
           AdjustedStatusFormatError => BadRequest(Json.toJson(errorWrapper))
      case RuleNotForeignProperty |
           RuleNoAdjustmentsMade  => Forbidden(Json.toJson(errorWrapper))
      case NotFoundError => NotFound(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
    }
  }
}
