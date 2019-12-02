/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.controllers

import cats.data.EitherT
import javax.inject.{Inject, Singleton}
import play.api.http.MimeTypes
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.Logging
import v1.controllers.requestParsers.ListBsasRequestDataParser
import v1.models.errors.{BadRequestError, DownstreamError, ErrorWrapper, NinoFormatError, NotFoundError, RuleTaxYearRangeExceededError, TaxYearFormatError, TypeOfBusinessFormatError}
import v1.models.request.ListBsasRawData
import v1.services.{EnrolmentsAuthService, MtdIdLookupService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListBsasController @Inject()(
                                    val authService: EnrolmentsAuthService,
                                    val lookupService: MtdIdLookupService,
                                    requestParser: ListBsasRequestDataParser,
                                    service: ListBsasService,
                                    cc: ControllerComponents
                                  )(implicit ec: ExecutionContext)
  extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "ListBsasController",
      endpointName = "listBsas"
    )

  def listBsas(nino: String, taxYear: String, typeOfBusiness: Option[String], selfEmploymentId: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = ListBsasRawData(nino, taxYear, typeOfBusiness, selfEmploymentId)
      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          response <- EitherT(service.listBsas(parsedRequest))
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with correlationId: ${response.correlationId}"
          )

          Ok(Json.toJson(response.responseData))
            .withApiHeaders(response.correlationId)
            .as(MimeTypes.JSON)
        }
      result.leftMap { errorWrapper =>
        val correlationId = getCorrelationId(errorWrapper)
        errorResult(errorWrapper).withApiHeaders(correlationId)
        }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {
    errorWrapper.error match {
      case BadRequestError | NinoFormatError | TaxYearFormatError | TypeOfBusinessFormatError | RuleTaxYearRangeExceededError=>
        BadRequest(Json.toJson(errorWrapper))
      case NotFoundError => NotFound(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
    }
  }


}
