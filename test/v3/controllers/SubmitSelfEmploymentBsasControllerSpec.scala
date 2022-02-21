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

import domain.Nino
import mocks.MockIdGenerator
import play.api.libs.json.Json
import play.api.mvc.{ AnyContentAsJson, Result }
import uk.gov.hmrc.http.HeaderCarrier
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockSubmitSelfEmploymentRequestParser
import v3.mocks.services._
import v3.models.audit.{ AuditError, AuditEvent, AuditResponse, GenericAuditDetail }
import v3.models.errors._
import v3.models.hateoas.Method.GET
import v3.models.hateoas.{ HateoasWrapper, Link }
import v3.models.outcomes.ResponseWrapper
import v3.models.request.submitBsas.selfEmployment.{ SubmitSelfEmploymentBsasRawData, SubmitSelfEmploymentBsasRequestData }
import v3.models.response.SubmitSelfEmploymentBsasHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitSelfEmploymentBsasControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitSelfEmploymentRequestParser
    with MockSubmitSelfEmploymentBsasService
    with MockSubmitSelfEmploymentBsasNrsProxyService
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator {

  private val correlationId = "X-123"

  import v3.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._

  private val nino = "AA123456A"

  private val calculationId = "c75f40a6-a3df-4429-a697-471eeec46435"

  private val rawRequest = SubmitSelfEmploymentBsasRawData(nino, calculationId, AnyContentAsJson(mtdRequest))
  private val request    = SubmitSelfEmploymentBsasRequestData(Nino(nino), calculationId, submitSelfEmploymentBsasRequestBodyModel)

  val testHateoasLinks: Seq[Link] = Seq(
    Link(
      href = s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$calculationId",
      method = GET,
      rel = "self"
    )
  )

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new SubmitSelfEmploymentBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockRequestParser,
      service = mockService,
      nrsService = mockSubmitSelfEmploymentBsasNrsProxyService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)

  }

  def event(auditResponse: AuditResponse): AuditEvent[GenericAuditDetail] =
    AuditEvent(
      auditType = "submitBusinessSourceAccountingAdjustments",
      transactionName = "submit-self-employment-accounting-adjustments",
      detail = GenericAuditDetail(
        userType = "Individual",
        agentReferenceNumber = None,
        params = Map("nino" -> nino, "calculationId" -> calculationId),
        requestBody = Some(Json.toJson(mtdRequest)),
        `X-CorrelationId` = correlationId,
        auditResponse = auditResponse
      )
    )

  "submitSelfEmploymentBsas" should {
    "return a successful hateoas response with status 200 (OK)" when {
      "a valid request is supplied" in new Test {

        MockSubmitSelfEmploymentBsasDataParser
          .parse(rawRequest)
          .returns(Right(request))

        MockSubmitSelfEmploymentBsasNrsProxyService
          .submit(nino)
          .returns(Future.successful(Unit))

        MockSubmitSelfEmploymentBsasService
          .submitSelfEmploymentBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), SubmitSelfEmploymentBsasHateoasData(nino, calculationId))
          .returns(HateoasWrapper((), testHateoasLinks))

        val result: Future[Result] = controller.submitSelfEmploymentBsas(nino, calculationId)(fakePostRequest(Json.toJson(mtdRequest)))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.parse(hateoasResponse(nino, calculationId))
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(OK, None, Some(Json.parse(hateoasResponse(nino, calculationId))))
        MockedAuditService.verifyAuditEvent(event(auditResponse)).once
      }
    }

    "return parser errors as per spec" when {
      def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
        s"a ${error.code} error is returned from the parser" in new Test {

          MockSubmitSelfEmploymentBsasDataParser
            .parse(rawRequest)
            .returns(Left(ErrorWrapper(correlationId, error, None)))

          val result: Future[Result] = controller.submitSelfEmploymentBsas(nino, calculationId)(fakePostRequest(mtdRequest))

          status(result) shouldBe expectedStatus
          contentAsJson(result) shouldBe Json.toJson(error)
          header("X-CorrelationId", result) shouldBe Some(correlationId)

          val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(error.code))), None)
          MockedAuditService.verifyAuditEvent(event(auditResponse)).once
        }
      }

      val input = Seq(
        (BadRequestError, BAD_REQUEST),
        (NinoFormatError, BAD_REQUEST),
        (CalculationIdFormatError, BAD_REQUEST),
        (RuleIncorrectOrEmptyBodyError, BAD_REQUEST),
        (ValueFormatError, BAD_REQUEST),
        (RuleBothExpensesError, BAD_REQUEST),
        (DownstreamError, INTERNAL_SERVER_ERROR)
      )

      input.foreach(args => (errorsFromParserTester _).tupled(args))

      "multiple parser errors occur" in new Test {

        val error: ErrorWrapper = ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, CalculationIdFormatError)))

        MockSubmitSelfEmploymentBsasDataParser
          .parse(rawRequest)
          .returns(Left(error))

        val result: Future[Result] = controller.submitSelfEmploymentBsas(nino, calculationId)(fakePostRequest(mtdRequest))

        status(result) shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(error)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse =
          AuditResponse(
            httpStatus = BAD_REQUEST,
            errors = Some(
              Seq(
                AuditError(NinoFormatError.code),
                AuditError(CalculationIdFormatError.code)
              )),
            body = None
          )

        MockedAuditService.verifyAuditEvent(event(auditResponse)).once
      }

      "multiple errors occur for the customised errors" in new Test {
        val error: ErrorWrapper = ErrorWrapper(
          correlationId,
          BadRequestError,
          Some(
            Seq(
              FormatAdjustmentValueError.copy(paths = Some(Seq("turnover"))),
              RuleAdjustmentRangeInvalid.copy(paths = Some(Seq("other")))
            )
          )
        )

        MockSubmitSelfEmploymentBsasDataParser
          .parse(rawRequest)
          .returns(Left(error))

        val result: Future[Result] = controller.submitSelfEmploymentBsas(nino, calculationId)(fakePostRequest(mtdRequest))

        status(result) shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(error)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse =
          AuditResponse(httpStatus = BAD_REQUEST,
                        errors = Some(
                          Seq(
                            AuditError(FormatAdjustmentValueError.code),
                            AuditError(RuleAdjustmentRangeInvalid.code)
                          )),
                        body = None)

        MockedAuditService.verifyAuditEvent(event(auditResponse)).once
      }
    }

    "return downstream errors as per the spec" when {
      def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
        s"a ${mtdError.code} error is returned from the service" in new Test {

          MockSubmitSelfEmploymentBsasDataParser
            .parse(rawRequest)
            .returns(Right(request))

          MockSubmitSelfEmploymentBsasNrsProxyService
            .submit(nino)
            .returns(Future.successful(Unit))

          MockSubmitSelfEmploymentBsasService
            .submitSelfEmploymentBsas(request)
            .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

          val result: Future[Result] = controller.submitSelfEmploymentBsas(nino, calculationId)(fakePostRequest(mtdRequest))

          status(result) shouldBe expectedStatus
          contentAsJson(result) shouldBe Json.toJson(mtdError)
          header("X-CorrelationId", result) shouldBe Some(correlationId)

          val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(mtdError.code))), None)
          MockedAuditService.verifyAuditEvent(event(auditResponse)).once
        }
      }

      val input = Seq(
        (NinoFormatError, BAD_REQUEST),
        (CalculationIdFormatError, BAD_REQUEST),
        (NotFoundError, NOT_FOUND),
        (DownstreamError, INTERNAL_SERVER_ERROR),
        (RuleSummaryStatusInvalid, FORBIDDEN),
        (RuleSummaryStatusSuperseded, FORBIDDEN),
        (RuleAlreadyAdjusted, FORBIDDEN),
        (RuleResultingValueNotPermitted, FORBIDDEN),
        (RuleOverConsolidatedExpensesThreshold, FORBIDDEN),
        (RuleTypeOfBusinessIncorrectError, BAD_REQUEST),
        (RuleTradingIncomeAllowanceClaimed, FORBIDDEN)
      )
      input.foreach(args => (serviceErrors _).tupled(args))
    }
  }
}
