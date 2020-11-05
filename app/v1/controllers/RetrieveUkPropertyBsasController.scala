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

package v1.controllers

import cats.data.EitherT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import play.api.http.MimeTypes
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import utils.{IdGenerator, Logging}
import v1.controllers.requestParsers.RetrieveUkPropertyRequestParser
import v1.hateoas.HateoasFactory
import v1.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import v1.models.errors._
import v1.models.request.RetrieveUkPropertyBsasRawData
import v1.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse.RetrieveUkPropertyBsasHateoasFactory
import v1.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyHateoasData
import v1.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, RetrieveUkPropertyBsasService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveUkPropertyBsasController @Inject()(
                                                  val authService: EnrolmentsAuthService,
                                                  val lookupService: MtdIdLookupService,
                                                  requestParser: RetrieveUkPropertyRequestParser,
                                                  service: RetrieveUkPropertyBsasService,
                                                  hateoasFactory: HateoasFactory,
                                                  auditService: AuditService,
                                                  cc: ControllerComponents,
                                                  val idGenerator: IdGenerator
                                              )(implicit ec: ExecutionContext)
  extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "RetrievePropertyBsasController",
      endpointName = "retrieve"
    )

  def retrieve(nino: String, bsasId: String, adjustedStatus: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>

      implicit val correlationId: String = idGenerator.generateCorrelationId
      logger.info(
        s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with CorrelationId: $correlationId")

      val rawData = RetrieveUkPropertyBsasRawData(nino, bsasId, adjustedStatus)
      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          response <- EitherT(service.retrieve(parsedRequest))
          hateoasResponse <- EitherT.fromEither[Future](
            hateoasFactory.wrap(response.responseData,
              RetrieveUkPropertyHateoasData(nino, response.responseData.metadata.bsasId)).asRight[ErrorWrapper])
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with correlationId: ${response.correlationId}"
          )

          auditSubmission(
            GenericAuditDetail(
              userDetails = request.userDetails,
              params = Map("nino" -> nino, "bsasId" -> bsasId),
              requestBody = None,
              `X-CorrelationId` = response.correlationId,
              auditResponse = AuditResponse(httpStatus = OK, response = Right(Some(Json.toJson(hateoasResponse))))
            )
          )

          Ok(Json.toJson(hateoasResponse))
            .withApiHeaders(response.correlationId)
            .as(MimeTypes.JSON)
        }
      result.leftMap { errorWrapper =>
        val resCorrelationId = errorWrapper.correlationId
        val result = errorResult(errorWrapper).withApiHeaders(resCorrelationId)
        logger.info(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: $resCorrelationId")

        auditSubmission(
          GenericAuditDetail(
            userDetails = request.userDetails,
            params = Map("nino" -> nino, "bsasId" -> bsasId),
            requestBody = None,
            `X-CorrelationId` = resCorrelationId,
            auditResponse = AuditResponse(httpStatus = result.header.status, response = Left(errorWrapper.auditErrors))
          )
        )

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {
    (errorWrapper.error: @unchecked) match {
      case BadRequestError | NinoFormatError
           | BsasIdFormatError | AdjustedStatusFormatError => BadRequest(Json.toJson(errorWrapper))
      case RuleNotUkProperty | RuleNoAdjustmentsMade => Forbidden(Json.toJson(errorWrapper))
      case NotFoundError => NotFound(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
    }
  }

  private def auditSubmission(details: GenericAuditDetail)
                             (implicit hc: HeaderCarrier,
                              ec: ExecutionContext): Future[AuditResult] = {

    val event = AuditEvent(
      auditType = "retrieveABusinessSourceAdjustableSummary",
      transactionName = "retrieve-a-uk-property-bsas",
      detail = details
    )

    auditService.auditEvent(event)
  }
}
