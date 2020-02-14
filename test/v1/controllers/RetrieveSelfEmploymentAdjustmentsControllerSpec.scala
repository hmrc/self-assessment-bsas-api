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

import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.fixtures.selfEmployment.RetrieveSelfEmploymentAdjustmentsFixtures._
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockRetrieveAdjustmentsRequestParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockRetrieveSelfEmploymentAdjustmentsService}
import v1.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v1.models.errors._
import v1.models.hateoas.Method.GET
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{RetrieveAdjustmentsRawData, RetrieveAdjustmentsRequestData}
import v1.models.response.retrieveBsasAdjustments.selfEmployment.RetrieveSelfEmploymentAdjustmentsHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveSelfEmploymentAdjustmentsControllerSpec extends ControllerBaseSpec
  with MockEnrolmentsAuthService
  with MockMtdIdLookupService
  with MockRetrieveAdjustmentsRequestParser
  with MockRetrieveSelfEmploymentAdjustmentsService
  with MockHateoasFactory
  with MockAuditService  {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new RetrieveSelfEmploymentAdjustmentsController(
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

  private val nino = "AA123456A"
  private val correlationId = "X-123"
  private val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val request = RetrieveAdjustmentsRequestData(Nino(nino), bsasId)
  private val requestRawData = RetrieveAdjustmentsRawData(nino, bsasId)

  val testHateoasLinkSubmit = Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$bsasId?adjustedStatus=true",
    method = GET, rel = "retrieve-adjustable-summary")

  val testHateoasLinkAdjustSelf = Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$bsasId/adjust",
    method = GET, rel = "self")

  def event(auditResponse: AuditResponse): AuditEvent[GenericAuditDetail] =
    AuditEvent(
      auditType = "retrieveBusinessSourceAccountingAdjustments",
      transactionName = "retrieve-a-self-employment-business-accounting-adjustments",
      detail = GenericAuditDetail(
        userType = "Individual",
        agentReferenceNumber = None,
        params = Map("nino" -> nino, "bsasId" -> bsasId),
        requestBody = None,
        `X-CorrelationId` = correlationId,
        auditResponse = auditResponse
      )
    )

  "retrieve" should {
    "return successful hateoas response for self-assessment with status OK" when {
      "a valid request supplied" in new Test {

        MockRetrieveAdjustmentsRequestParser
          .parse(requestRawData)
          .returns(Right(request))

        MockRetrieveSelfEmploymentBsasService
          .retrieveAdjustments(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveSelfEmploymentAdjustmentResponseModel))))

        MockHateoasFactory
          .wrap(retrieveSelfEmploymentAdjustmentResponseModel, RetrieveSelfEmploymentAdjustmentsHateoasData(nino, bsasId))
          .returns(HateoasWrapper(retrieveSelfEmploymentAdjustmentResponseModel , Seq(testHateoasLinkSubmit, testHateoasLinkAdjustSelf))
        )

        val result: Future[Result] = controller.retrieve(nino, bsasId)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.parse(hateoasResponseForSelfEmploymentAdjustments(nino, bsasId))
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(OK, None, Some(Json.parse(hateoasResponseForSelfEmploymentAdjustments(nino, bsasId))))
        MockedAuditService.verifyAuditEvent(event(auditResponse)).once
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockRetrieveAdjustmentsRequestParser
              .parse(requestRawData)
              .returns(Left(ErrorWrapper(Some(correlationId), error, None)))

            val result: Future[Result] = controller.retrieve(nino, bsasId)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(error.code))), None)
            MockedAuditService.verifyAuditEvent(event(auditResponse)).once
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (BsasIdFormatError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockRetrieveAdjustmentsRequestParser
              .parse(requestRawData)
              .returns(Right(request))

            MockRetrieveSelfEmploymentBsasService
              .retrieveAdjustments(request)
              .returns(Future.successful(Left(ErrorWrapper(Some(correlationId), mtdError))))

            val result: Future[Result] = controller.retrieve(nino, bsasId)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(mtdError.code))), None)
            MockedAuditService.verifyAuditEvent(event(auditResponse)).once
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (BsasIdFormatError, BAD_REQUEST),
          (DownstreamError, INTERNAL_SERVER_ERROR),
          (RuleNoAdjustmentsMade, FORBIDDEN),
          (NotFoundError, NOT_FOUND),
          (RuleNotSelfEmployment, FORBIDDEN)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
