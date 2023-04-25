/*
 * Copyright 2023 HM Revenue & Customs
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

import api.controllers.ControllerBaseSpec
import api.hateoas.Method.GET
import api.hateoas.{ HateoasWrapper, Link }
import api.mocks.MockIdGenerator
import api.models.audit.{ AuditError, AuditEvent, AuditResponse, GenericAuditDetail }
import api.models.errors._
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v2.fixtures.TriggerBsasRequestBodyFixtures._
import api.hateoas.MockHateoasFactory
import v2.mocks.requestParsers.MockTriggerBsasRequestParser
import api.services.{ MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService }
import v2.mocks.services.MockTriggerBsasService
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import api.models.ResponseWrapper
import api.models.domain.Nino
import config.MockAppConfig
import routing.Version2
import v2.models.request.triggerBsas.{ TriggerBsasRawData, TriggerBsasRequest }
import v2.models.response.TriggerBsasHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TriggerBsasControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockTriggerBsasRequestParser
    with MockTriggerBsasService
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator
    with MockAppConfig {

  private val correlationId = "X-123"
  private val version       = Version2

  trait Test {
    val hc = HeaderCarrier()

    val controller = new TriggerBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRequestParser,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)
    MockedAppConfig.apiStatus(version) returns "DEPRECATED"

  }

  private val nino = "AA123456A"

  private val request        = TriggerBsasRequest(Nino(nino), triggerBsasRequestDataBody())
  private val requestRawData = TriggerBsasRawData(nino, triggerBsasRawDataBody())

  private val requestForProperty        = TriggerBsasRequest(Nino(nino), triggerBsasRequestDataBody(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`))
  private val requestRawDataForProperty = TriggerBsasRawData(nino, triggerBsasRawDataBody(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`.toString))

  val testHateoasLinkSE = Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/c75f40a6-a3df-4429-a697-471eeec46435",
                               method = GET,
                               rel = "self")

  val testHateoasLinkProperty =
    Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/property/c75f40a6-a3df-4429-a697-471eeec46435", method = GET, rel = "self")

  def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
    AuditEvent(
      auditType = "triggerABusinessSourceAdjustableSummary",
      transactionName = "trigger-a-business-source-adjustable-Summary",
      detail = GenericAuditDetail(
        versionNumber = "2.0",
        userType = "Individual",
        agentReferenceNumber = None,
        params = Map("nino" -> nino),
        requestBody = requestBody,
        `X-CorrelationId` = correlationId,
        auditResponse = auditResponse
      )
    )

  "triggerBsas" should {
    "return successful hateoas response for SE with status CREATED" when {
      "a valid request supplied for business type self-employment" in new Test {

        MockTriggerBsasRequestParser
          .parse(requestRawData)
          .returns(Right(request))

        MockTriggerBsasService
          .triggerBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseObj))))

        MockHateoasFactory
          .wrap(responseObj, TriggerBsasHateoasData(nino, TypeOfBusiness.`self-employment`, responseObj.id))
          .returns(HateoasWrapper(responseObj, Seq(testHateoasLinkSE)))

        val result: Future[Result] = controller.triggerBsas(nino)(fakePostRequest(Json.toJson(requestBody)))

        status(result) shouldBe CREATED
        contentAsJson(result) shouldBe Json.parse(hateoasResponseForSE(nino))
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(CREATED, None, Some(Json.parse(hateoasResponseForSE(nino))))
        MockedAuditService.verifyAuditEvent(event(auditResponse, Some(requestBody))).once
      }

      "a valid request supplied for business type uk-property" in new Test {

        MockTriggerBsasRequestParser
          .parse(requestRawDataForProperty)
          .returns(Right(requestForProperty))

        MockTriggerBsasService
          .triggerBsas(requestForProperty)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseObj))))

        MockHateoasFactory
          .wrap(responseObj, TriggerBsasHateoasData(nino, TypeOfBusiness.`uk-property-fhl`, responseObj.id))
          .returns(HateoasWrapper(responseObj, Seq(testHateoasLinkProperty)))

        val result: Future[Result] = controller.triggerBsas(nino)(fakePostRequest(Json.toJson(requestBodyForProperty)))

        status(result) shouldBe CREATED
        contentAsJson(result) shouldBe Json.parse(hateoasResponseForProperty(nino))
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(CREATED, None, Some(Json.parse(hateoasResponseForProperty(nino))))
        MockedAuditService.verifyAuditEvent(event(auditResponse, Some(requestBodyForProperty))).once
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockTriggerBsasRequestParser
              .parse(requestRawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.triggerBsas(nino)(fakePostRequest(requestBody))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(error.code))), None)
            MockedAuditService.verifyAuditEvent(event(auditResponse, Some(requestBody))).once
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (RuleIncorrectOrEmptyBodyError, BAD_REQUEST),
          (RuleAccountingPeriodNotSupportedError, BAD_REQUEST),
          (StartDateFormatError, BAD_REQUEST),
          (EndDateFormatError, BAD_REQUEST),
          (TypeOfBusinessFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (RuleEndBeforeStartDateError, BAD_REQUEST),
          (InternalError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockTriggerBsasRequestParser
              .parse(requestRawData)
              .returns(Right(request))

            MockTriggerBsasService
              .triggerBsas(request)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.triggerBsas(nino)(fakePostRequest(requestBody))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(mtdError.code))), None)
            MockedAuditService.verifyAuditEvent(event(auditResponse, Some(requestBody))).once
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (RuleAccountingPeriodNotEndedError, FORBIDDEN),
          (RulePeriodicDataIncompleteError, FORBIDDEN),
          (RuleNoAccountingPeriodError, FORBIDDEN),
          (NotFoundError, NOT_FOUND),
          (InternalError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
