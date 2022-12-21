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
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, ControllerComponents }
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import utils.{ IdGenerator, Logging }
import v3.controllers.requestParsers.SubmitUkPropertyBsasRequestParser
import v3.hateoas.HateoasFactory
import v3.models.audit.{ AuditEvent, AuditResponse, GenericAuditDetail }
import v3.models.errors._
import v3.models.request.submitBsas.ukProperty.SubmitUkPropertyBsasRawData
import v3.models.response.SubmitUkPropertyBsasHateoasData
import v3.services._

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class SubmitUkPropertyBsasController @Inject()(val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               nrsService: SubmitUKPropertyBsasNrsProxyService,
                                               requestParser: SubmitUkPropertyBsasRequestParser,
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

  def handleRequest(nino: String, calculationId: String, taxYear: Option[String]): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val correlationId: String = idGenerator.generateCorrelationId
      logger.info(
        s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with CorrelationId: $correlationId")

      val rawData = SubmitUkPropertyBsasRawData(nino, calculationId, request.body, taxYear)

      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          response <- {
            //Submit asynchronously to NRS
            nrsService.submit(nino, parsedRequest.body)
            //Submit Return to ETMP
            EitherT(service.submitPropertyBsas(parsedRequest))
          }
        } yield {
          val hateoasData    = SubmitUkPropertyBsasHateoasData(nino, calculationId, parsedRequest.taxYear)
          val vendorResponse = hateoasFactory.wrap(response.responseData, hateoasData)

          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${response.correlationId}"
          )
          auditSubmission(
            GenericAuditDetail(
              userDetails = request.userDetails,
              params = Map("nino" -> nino, "calculationId" -> calculationId),
              requestBody = Some(request.body),
              `X-CorrelationId` = response.correlationId,
              auditResponse = AuditResponse(httpStatus = OK, response = Right(Some(Json.toJson(vendorResponse))))
            )
          )

          Ok(Json.toJson(vendorResponse))
            .withApiHeaders(response.correlationId)
        }

      result.leftMap { errorWrapper =>
        val resCorrelationId = errorWrapper.correlationId
        val result           = errorResult(errorWrapper).withApiHeaders(resCorrelationId)
        logger.info(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: $resCorrelationId")
        auditSubmission(
          GenericAuditDetail(
            userDetails = request.userDetails,
            params = Map("nino" -> nino, "calculationId" -> calculationId),
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
      case _
          if errorWrapper.containsAnyOf(
            BadRequestError,
            NinoFormatError,
            CalculationIdFormatError,
            ValueFormatError,
            RuleBothExpensesError,
            RuleTypeOfBusinessIncorrectError,
            RuleIncorrectOrEmptyBodyError,
            RuleBothPropertiesSuppliedError,
            TaxYearFormatError,
            RuleTaxYearRangeInvalidError,
            InvalidTaxYearParameterError,
            RuleTaxYearNotSupportedError,
            RuleSummaryStatusInvalid,
            RuleSummaryStatusSuperseded,
            RuleAlreadyAdjusted,
            RuleResultingValueNotPermitted,
            RuleOverConsolidatedExpensesThreshold,
            RulePropertyIncomeAllowanceClaimed
          ) =>
        BadRequest(Json.toJson(errorWrapper))

      case NotFoundError => NotFound(Json.toJson(errorWrapper))
      case InternalError => InternalServerError(Json.toJson(errorWrapper))
      case _             => unhandledError(errorWrapper)
    }

  private def auditSubmission(details: GenericAuditDetail)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = {

    val event = AuditEvent(
      auditType = "SubmitUKPropertyAccountingAdjustments",
      transactionName = "submit-uk-property-accounting-adjustments",
      detail = details
    )

    auditService.auditEvent(event)
  }
}
