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
import play.api.http.MimeTypes
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContentAsJson, ControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import utils.{IdGenerator, Logging}
import v2.controllers.requestParsers.TriggerBsasRequestParser
import v2.hateoas.HateoasFactory
import v2.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import v2.models.request.triggerBsas.TriggerBsasRawData
import v2.models.response.TriggerBsasHateoasData
import v2.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, TriggerBsasService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerBsasController @Inject()(
                                       val authService: EnrolmentsAuthService,
                                       val lookupService: MtdIdLookupService,
                                       requestParser: TriggerBsasRequestParser,
                                       triggerBsasService: TriggerBsasService,
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
      controllerName = "TriggerBsasController",
      endpointName = "triggerBsas"
    )

  def triggerBsas(nino: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>

      implicit val correlationId: String = idGenerator.generateCorrelationId
      logger.info(
        s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with CorrelationId: $correlationId")

      val rawData = TriggerBsasRawData(nino, AnyContentAsJson(request.body))
      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          response <- EitherT(triggerBsasService.triggerBsas(parsedRequest))
          hateoasResponse <- EitherT.fromEither[Future](
            hateoasFactory
              .wrap(response.responseData,
                TriggerBsasHateoasData(nino, TypeOfBusiness.parser(parsedRequest.body.typeOfBusiness), response.responseData.id))
              .asRight[ErrorWrapper])
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${response.correlationId}"
          )

          auditSubmission(
            GenericAuditDetail(
              userDetails = request.userDetails,
              params = Map("nino" -> nino),
              requestBody = Some(request.body),
              `X-CorrelationId` = response.correlationId,
              auditResponse = AuditResponse(httpStatus = CREATED, response = Right(Some(Json.toJson(hateoasResponse))))
            )
          )

          Created(Json.toJson(hateoasResponse))
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
            params = Map("nino" -> nino),
            requestBody = Some(request.body),
            `X-CorrelationId` = resCorrelationId,
            auditResponse = AuditResponse(httpStatus = result.header.status, response = Left(errorWrapper.auditErrors))
          )
        )

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {
    (errorWrapper.error: @unchecked) match {
      case BadRequestError |
           NinoFormatError |
           RuleAccountingPeriodNotSupportedError |
           StartDateFormatError |
           EndDateFormatError |
           TypeOfBusinessFormatError |
           CustomMtdError(RuleIncorrectOrEmptyBodyError.code) |
           BusinessIdFormatError | RuleEndBeforeStartDateError => BadRequest(Json.toJson(errorWrapper))
      case RuleAccountingPeriodNotEndedError | RulePeriodicDataIncompleteError | RuleNoAccountingPeriodError => Forbidden(Json.toJson(errorWrapper))
      case NotFoundError => NotFound(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
    }
  }

  private def auditSubmission(details: GenericAuditDetail)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = {

    val event = AuditEvent(
      auditType = "triggerABusinessSourceAdjustableSummary",
      transactionName = "trigger-a-business-source-adjustable-Summary",
      detail = details
    )

    auditService.auditEvent(event)
  }
}
