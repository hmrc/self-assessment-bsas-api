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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockSubmitUkPropertyRequestParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockSubmitUkPropertyBsasService}
import v1.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v1.models.domain.TypeOfBusiness
import v1.models.errors._
import v1.models.hateoas.Method.GET
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.submitBsas.{SubmitUkPropertyBsasRawData, SubmitUkPropertyBsasRequestData}
import v1.models.response.{SubmitUkPropertyBsasHateoasData, SubmitUkPropertyBsasResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitUkPropertyBsasControllerSpec
  extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitUkPropertyRequestParser
    with MockSubmitUkPropertyBsasService
    with MockHateoasFactory
    with MockAuditService  {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new SubmitUkPropertyBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockRequestParser,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  import v1.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._

  private val nino          = "AA123456A"
  private val correlationId = "X-123"

  private val bsasId = "c75f40a6-a3df-4429-a697-471eeec46435"

  private val fhlRawRequest = SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyFHL(fhlIncomeAllFields, fhlExpensesAllFields))
  private val fhlRequest = SubmitUkPropertyBsasRequestData(Nino(nino), bsasId, fhlBody)

  private val nonFhlRawRequest = SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyNonFHL(nonFHLIncomeAllFields, nonFHLExpensesAllFields))
  private val nonFhlRequest = SubmitUkPropertyBsasRequestData(Nino(nino), bsasId, nonFHLBody)

  val response = SubmitUkPropertyBsasResponse(bsasId, TypeOfBusiness.`uk-property-fhl`)

  val testHateoasLinks: Seq[Link] = Seq(
    Link(
      href = s"/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId/adjust",
      method = GET,
      rel = "self"
    ),
    Link(
      href = s"/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId?adjustedStatus=true",
      method = GET,
      rel = "retrieve-adjustable-summary"
    )
  )

  def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
    AuditEvent(
      auditType = "submitBusinessSourceAccountingAdjustments",
      transactionName = "adjustable-summary-api",
      detail = GenericAuditDetail(
        userType = "Individual",
        agentReferenceNumber = None,
        pathParams = Map("nino" -> nino, "bsasId" -> bsasId),
        requestBody = requestBody,
        `X-CorrelationId` = correlationId,
        auditResponse = auditResponse
      )
    )

  "submitUkPropertyBsas" should {
    "return a successful hateoas response with status 200 (OK)" when {
      "a valid request is supplied for an FHL property" in new Test {

        MockSubmitUkPropertyBsasDataParser
          .parse(fhlRawRequest)
          .returns(Right(fhlRequest))

        MockSubmitUkPropertyBsasService
          .submitPropertyBsas(fhlRequest)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrap(response, SubmitUkPropertyBsasHateoasData(nino, bsasId))
          .returns(HateoasWrapper(response, testHateoasLinks))

        val result: Future[Result] = controller.submitUkPropertyBsas(nino, bsasId)(fakePostRequest(Json.toJson(validfhlInputJson)))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.parse(hateoasResponse(nino, bsasId))
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(OK, None, Some(Json.parse(hateoasResponse(nino, bsasId))))
        MockedAuditService.verifyAuditEvent(event(auditResponse, Some(validfhlInputJson) )).once
      }

      "a valid request is supplied for a non-FHL property" in new Test {

        MockSubmitUkPropertyBsasDataParser
          .parse(nonFhlRawRequest)
          .returns(Right(nonFhlRequest))

        MockSubmitUkPropertyBsasService
          .submitPropertyBsas(nonFhlRequest)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrap(response, SubmitUkPropertyBsasHateoasData(nino, bsasId))
          .returns(HateoasWrapper(response, testHateoasLinks))

        val result: Future[Result] = controller.submitUkPropertyBsas(nino, bsasId)(fakePostRequest(Json.toJson(validNonFHLInputJson)))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.parse(hateoasResponse(nino, bsasId))
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(OK, None, Some(Json.parse(hateoasResponse(nino, bsasId))))
        MockedAuditService.verifyAuditEvent(event(auditResponse, Some(validNonFHLInputJson))).once
      }
    }

    "return parser errors as per spec" when {
      def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
        s"a ${error.code} error is returned from the parser" in new Test {

          MockSubmitUkPropertyBsasDataParser
            .parse(fhlRawRequest)
            .returns(Left(ErrorWrapper(Some(correlationId), error, None)))

          val result: Future[Result] = controller.submitUkPropertyBsas(nino, bsasId)(fakePostRequest(validfhlInputJson))

          status(result) shouldBe expectedStatus
          contentAsJson(result) shouldBe Json.toJson(error)
          header("X-CorrelationId", result) shouldBe Some(correlationId)

          val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(error.code))), None)
          MockedAuditService.verifyAuditEvent(event(auditResponse, Some(validfhlInputJson))).once
        }
      }

      val input = Seq(
        (BadRequestError, BAD_REQUEST),
        (NinoFormatError, BAD_REQUEST),
        (BsasIdFormatError, BAD_REQUEST),
        (RuleIncorrectOrEmptyBodyError, BAD_REQUEST),
        (DownstreamError, INTERNAL_SERVER_ERROR),
        (RuleBothExpensesError, BAD_REQUEST),
        (FormatAdjustmentValueError, BAD_REQUEST),
        (RuleAdjustmentRangeInvalid, BAD_REQUEST)
      )

      input.foreach(args => (errorsFromParserTester _).tupled(args))

      "multiple parser errors occur" in new Test {

        val error = ErrorWrapper(Some(correlationId), BadRequestError, Some(Seq(NinoFormatError, BsasIdFormatError)))

        MockSubmitUkPropertyBsasDataParser
          .parse(fhlRawRequest)
          .returns(Left(error))

        val result: Future[Result] = controller.submitUkPropertyBsas(nino, bsasId)(fakePostRequest(validfhlInputJson))

        status(result) shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(error)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(BAD_REQUEST, Some(Seq(AuditError(NinoFormatError.code), AuditError(BsasIdFormatError.code))), None)
        MockedAuditService.verifyAuditEvent(event(auditResponse, Some(validfhlInputJson))).once
      }

      "multiple errors occur for the customised errors" in new Test {
        val error = ErrorWrapper(
          Some(correlationId),
          BadRequestError,
          Some(
            Seq(
              FormatAdjustmentValueError.withFieldName("doesntMatter"),
              RuleAdjustmentRangeInvalid.withFieldName("thePatternMatchIgnoresMe")
            )
          )
        )

        MockSubmitUkPropertyBsasDataParser
          .parse(fhlRawRequest)
          .returns(Left(error))

        val result: Future[Result] = controller.submitUkPropertyBsas(nino, bsasId)(fakePostRequest(validfhlInputJson))

        status(result) shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(error)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(BAD_REQUEST, Some(Seq(
              AuditError(FormatAdjustmentValueError.withFieldName("doesntMatter").code),
              AuditError(RuleAdjustmentRangeInvalid.withFieldName("thePatternMatchIgnoresMe").code)
            )), None)

        MockedAuditService.verifyAuditEvent(event(auditResponse, Some(validfhlInputJson))).once
      }
    }

    "return downstream errors as per the spec" when {
      def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
        s"a ${mtdError.code} error is returned from the service" in new Test {

          MockSubmitUkPropertyBsasDataParser
            .parse(fhlRawRequest)
            .returns(Right(fhlRequest))

          MockSubmitUkPropertyBsasService
            .submitPropertyBsas(fhlRequest)
            .returns(Future.successful(Left(ErrorWrapper(Some(correlationId), mtdError))))

          val result: Future[Result] = controller.submitUkPropertyBsas(nino, bsasId)(fakePostRequest(validfhlInputJson))

          status(result) shouldBe expectedStatus
          contentAsJson(result) shouldBe Json.toJson(mtdError)
          header("X-CorrelationId", result) shouldBe Some(correlationId)

          val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(mtdError.code))), None)
          MockedAuditService.verifyAuditEvent(event(auditResponse, Some(validfhlInputJson))).once
        }
      }

      val input = Seq(
        (NinoFormatError, BAD_REQUEST),
        (BsasIdFormatError, BAD_REQUEST),
        (NotFoundError, NOT_FOUND),
        (DownstreamError, INTERNAL_SERVER_ERROR),
        (RuleTypeOfBusinessError, FORBIDDEN),
        (RuleSummaryStatusInvalid, FORBIDDEN),
        (RuleSummaryStatusSuperseded, FORBIDDEN),
        (RuleBsasAlreadyAdjusted, FORBIDDEN),
        (RuleOverConsolidatedExpensesThreshold, FORBIDDEN),
        (RuleSelfEmploymentAdjustedError, FORBIDDEN),
        (RulePropertyIncomeAllowanceClaimed, FORBIDDEN),
        (RuleResultingValueNotPermitted, FORBIDDEN)
      )
      input.foreach(args => (serviceErrors _).tupled(args))
    }
  }
}
