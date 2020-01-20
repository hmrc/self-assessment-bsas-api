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
import javax.inject.{ Inject, Singleton }
import play.api.http.MimeTypes
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, AnyContentAsJson, ControllerComponents }
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import utils.Logging
import v1.controllers.requestParsers.SubmitUkPropertyBsasDataParser
import v1.hateoas.HateoasFactory
import v1.models.audit.{ AuditEvent, AuditResponse, GenericAuditDetail }
import v1.models.errors.{ FormatAdjustmentValueError, RuleAdjustmentRangeInvalid, _ }
import v1.models.request.submitBsas.SubmitUkPropertyBsasRawData
import v1.models.response.SubmitUkPropertyBsasHateoasData
import v1.services.{ AuditService, EnrolmentsAuthService, MtdIdLookupService, SubmitUkPropertyBsasService }

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class SubmitUkPropertyBsasController @Inject()(val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               requestParser: SubmitUkPropertyBsasDataParser,
                                               service: SubmitUkPropertyBsasService,
                                               hateoasFactory: HateoasFactory,
                                               auditService: AuditService,
                                               cc: ControllerComponents)(implicit ec: ExecutionContext)
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
      val rawData = SubmitUkPropertyBsasRawData(nino, bsasId, AnyContentAsJson(request.body))
      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          response      <- EitherT(service.submitPropertyBsas(parsedRequest))
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
              pathParams = Map("nino" -> nino, "bsasId" -> bsasId),
              requestBody = Some(request.body), `X-CorrelationId` = response.correlationId,
              auditResponse = AuditResponse(httpStatus = OK, response = Right(Some(Json.toJson(hateoasResponse))))
            )
          )

          Ok(Json.toJson(hateoasResponse))
            .withApiHeaders(response.correlationId)
            .as(MimeTypes.JSON)
        }

      result.leftMap { errorWrapper =>
        val correlationId = getCorrelationId(errorWrapper)
        val result        = errorResult(errorWrapper).withApiHeaders(correlationId)

        auditSubmission(
          GenericAuditDetail(
            userDetails = request.userDetails,
            pathParams = Map("nino" -> nino, "bsasId" -> bsasId),
            requestBody = Some(request.body),
            `X-CorrelationId` = correlationId,
            auditResponse = AuditResponse(httpStatus = result.header.status, response = Left(errorWrapper.auditErrors))
          )
        )

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {
    (errorWrapper.error: @unchecked) match {
      case BadRequestError | NinoFormatError | BsasIdFormatError |
           RuleIncorrectOrEmptyBodyError | RuleBothExpensesError |
           MtdErrorWithCustomMessage(FormatAdjustmentValueError.code) |
           MtdErrorWithCustomMessage(RuleAdjustmentRangeInvalid.code) => BadRequest(Json.toJson(errorWrapper))
      case RuleSummaryStatusInvalid | RuleSummaryStatusSuperseded |
           RuleBsasAlreadyAdjusted | RuleOverConsolidatedExpensesThreshold |
           RulePropertyIncomeAllowanceClaimed | RuleResultingValueNotPermitted |
           RuleSelfEmploymentAdjustedError | RuleIncorrectPropertyAdjusted |
           RuleTypeOfBusinessError => Forbidden(Json.toJson(errorWrapper))
      case NotFoundError   => NotFound(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
    }
  }

  private def auditSubmission(details: GenericAuditDetail)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = {

    val event = AuditEvent(
      auditType = "submitBusinessSourceAccountingAdjustments",
      transactionName = "adjustable-summary-api",
      detail = details
    )

    auditService.auditEvent(event)
  }
}
