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
import v2.controllers.requestParsers.SubmitUkPropertyBsasDataParser
import v2.hateoas.HateoasFactory
import v2.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import v2.models.errors.{FormatAdjustmentValueError, RuleAdjustmentRangeInvalid, _}
import v2.models.request.submitBsas.ukProperty.SubmitUkPropertyBsasRawData
import v2.models.response.SubmitUkPropertyBsasHateoasData
import v2.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, SubmitUkPropertyBsasService, SubmitUKPropertyBsasNrsProxyService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitUkPropertyBsasController @Inject()(val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               nrsService: SubmitUKPropertyBsasNrsProxyService,
                                               requestParser: SubmitUkPropertyBsasDataParser,
                                               service: SubmitUkPropertyBsasService,
                                               hateoasFactory: HateoasFactory,
                                               auditService: AuditService,
                                               cc: ControllerComponents,
                                               val idGenerator: IdGenerator)(implicit ec: ExecutionContext)
  extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "SubmitUkPropertyBsasController",
      endpointName = "submitUkPropertyBsas"
    )

  def submitUkPropertyBsas(nino: String, bsasId: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>

      implicit val correlationId: String = idGenerator.generateCorrelationId
      logger.info(
        s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with CorrelationId: $correlationId")

      val rawData = SubmitUkPropertyBsasRawData(nino, bsasId, AnyContentAsJson(request.body))

      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          response      <- {
            //Submit asynchronously to NRS
            nrsService.submit(nino, parsedRequest.body)
            //Submit Return to ETMP
            EitherT(service.submitPropertyBsas(parsedRequest))
          }
          hateoasResponse <- EitherT.fromEither[Future](
            hateoasFactory
              .wrap(
                response.responseData,
                SubmitUkPropertyBsasHateoasData(nino, response.responseData.id)
              )
              .asRight[ErrorWrapper])
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${response.correlationId}"
          )

          auditSubmission(
            GenericAuditDetail(
              userDetails = request.userDetails,
              params = Map("nino" -> nino, "bsasId" -> bsasId),
              requestBody = Some(request.body), `X-CorrelationId` = response.correlationId,
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
            requestBody = Some(request.body),
            `X-CorrelationId` = resCorrelationId,
            auditResponse = AuditResponse(httpStatus = result.header.status, response = Left(errorWrapper.auditErrors))
          )
        )

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) =
    errorWrapper.error match {
      case BadRequestError | NinoFormatError | BsasIdFormatError |
           CustomMtdError(RuleIncorrectOrEmptyBodyError.code) | RuleBothExpensesError |
           CustomMtdError(FormatAdjustmentValueError.code) |
           CustomMtdError(RuleAdjustmentRangeInvalid.code) => BadRequest(Json.toJson(errorWrapper))
      case RuleSummaryStatusInvalid | RuleSummaryStatusSuperseded |
           RuleBsasAlreadyAdjusted | RuleOverConsolidatedExpensesThreshold |
           RulePropertyIncomeAllowanceClaimed | RuleResultingValueNotPermitted |
           RuleTypeOfBusinessError => Forbidden(Json.toJson(errorWrapper))
      case NotFoundError   => NotFound(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
      case _               => unhandledError(errorWrapper)
    }

  private def auditSubmission(details: GenericAuditDetail)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = {

    val event = AuditEvent(
      auditType = "submitBusinessSourceAccountingAdjustments",
      transactionName = "submit-uk-property-accounting-adjustments",
      detail = details
    )

    auditService.auditEvent(event)
  }
}
