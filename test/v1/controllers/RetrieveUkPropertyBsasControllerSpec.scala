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
import v1.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures._
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockRetrieveUkPropertyRequestParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockRetrieveUkPropertyBsasService}
import v1.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v1.models.errors.{AdjustedStatusFormatError, BsasIdFormatError, RuleNoAdjustmentsMade, RuleNotUkProperty, _}
import v1.models.hateoas.Method.{GET, POST}
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{RetrieveUkPropertyBsasRawData, RetrieveUkPropertyBsasRequestData}
import v1.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveUkPropertyBsasControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveUkPropertyRequestParser
    with MockRetrieveUkPropertyBsasService
    with MockHateoasFactory
      with MockAuditService  {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new RetrieveUkPropertyBsasController(
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

  private val nino          = "AA123456A"
  private val correlationId = "X-123"
  private val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val adjustedMtdStatus = Some("true")
  private val adjustedDesStatus = Some("03")

  private val request = RetrieveUkPropertyBsasRequestData(Nino(nino), bsasId, adjustedDesStatus)
  private val requestRawData = RetrieveUkPropertyBsasRawData(nino, bsasId, adjustedMtdStatus)

  val testHateoasLinkPropertySelf = Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId",
    method = GET, rel = "self")

  val testHateoasLinkPropertyAdjust = Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/property/$bsasId/adjust",
    method = POST, rel = "submit-summary-adjustments")

  "retrieve" should {
    "return successful hateoas response for property with status OK" when {
      "a valid request supplied" in new Test {

        MockRetrieveUkPropertyRequestParser
          .parse(requestRawData)
          .returns(Right(request))

        MockRetrieveUkPropertyBsasService
          .retrieveBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveUkPropertyBsasResponseModel))))

        MockHateoasFactory
          .wrap(retrieveUkPropertyBsasResponseModel, RetrieveUkPropertyHateoasData(nino, bsasId))
          .returns(HateoasWrapper(retrieveUkPropertyBsasResponseModel, Seq(testHateoasLinkPropertySelf, testHateoasLinkPropertyAdjust)))

        val result: Future[Result] = controller.retrieve(nino, bsasId, adjustedMtdStatus)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.parse(hateoasResponseForProperty(nino, bsasId))
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail: GenericAuditDetail =
          GenericAuditDetail(
            userType = "Individual",
            agentReferenceNumber = None,
            pathParams = Map("nino" -> nino, "bsasId" -> bsasId),
            requestBody = None,
            `X-CorrelationId` = correlationId,
            auditResponse = AuditResponse(OK, None, Some(Json.parse(hateoasResponseForProperty(nino, bsasId))))
          )

        val event: AuditEvent[GenericAuditDetail] =
          AuditEvent(
            auditType = "retrieveABusinessSourceAdjustableSummary",
            transactionName = "adjustable-summary-api",
            detail = detail
          )

        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockRetrieveUkPropertyRequestParser
              .parse(requestRawData)
              .returns(Left(ErrorWrapper(Some(correlationId), error, None)))

            val result: Future[Result] = controller.retrieve(nino, bsasId, adjustedMtdStatus)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            val detail: GenericAuditDetail =
              GenericAuditDetail(
                userType = "Individual",
                agentReferenceNumber = None,
                pathParams = Map("nino" -> nino, "bsasId" -> bsasId),
                requestBody = None,
                `X-CorrelationId` = correlationId,
                auditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(error.code))), None)
              )

            val event: AuditEvent[GenericAuditDetail] =
              AuditEvent(
                auditType = "retrieveABusinessSourceAdjustableSummary",
                transactionName = "adjustable-summary-api",
                detail = detail
              )

            MockedAuditService.verifyAuditEvent(event).once
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (BsasIdFormatError, BAD_REQUEST),
          (AdjustedStatusFormatError, BAD_REQUEST),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockRetrieveUkPropertyRequestParser
              .parse(requestRawData)
              .returns(Right(request))

            MockRetrieveUkPropertyBsasService
              .retrieveBsas(request)
              .returns(Future.successful(Left(ErrorWrapper(Some(correlationId), mtdError))))

            val result: Future[Result] = controller.retrieve(nino, bsasId, adjustedMtdStatus)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            val detail: GenericAuditDetail =
              GenericAuditDetail(
                userType = "Individual",
                agentReferenceNumber = None,
                pathParams = Map("nino" -> nino, "bsasId" -> bsasId),
                requestBody = None,
                `X-CorrelationId` = correlationId,
                auditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(mtdError.code))), None)
              )

            val event: AuditEvent[GenericAuditDetail] =
              AuditEvent(
                auditType = "retrieveABusinessSourceAdjustableSummary",
                transactionName = "adjustable-summary-api",
                detail = detail
              )

            MockedAuditService.verifyAuditEvent(event).once
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (BsasIdFormatError, BAD_REQUEST),
          (RuleNotUkProperty, FORBIDDEN),
          (RuleNoAdjustmentsMade, NOT_FOUND),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
