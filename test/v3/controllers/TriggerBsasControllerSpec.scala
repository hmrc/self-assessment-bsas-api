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
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v3.fixtures.TriggerBsasRequestBodyFixtures._
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockTriggerBsasRequestParser
import v3.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService, MockTriggerBsasService}
import v3.models.domain.TypeOfBusiness
import v3.models.errors._
import v3.models.hateoas.Method.GET
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.triggerBsas.{TriggerBsasRawData, TriggerBsasRequest}
import v3.models.response.TriggerBsasHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TriggerBsasControllerSpec
  extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockTriggerBsasRequestParser
    with MockTriggerBsasService
    with MockHateoasFactory
    with MockIdGenerator {

  private val correlationId = "X-123"
  private val nino          = "AA123456A"

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new TriggerBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockRequestParser,
      triggerBsasService = mockService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)

  }

  private val request = TriggerBsasRequest(
    Nino(nino),
    triggerBsasRequestDataBody()
  )

  private val requestRawData = TriggerBsasRawData(
    nino,
    triggerBsasRawDataBody()
  )

  private val requestForProperty = TriggerBsasRequest(
    Nino(nino),
    triggerBsasRequestDataBody(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`)
  )
  private val requestRawDataForProperty = TriggerBsasRawData(
    nino,
    triggerBsasRawDataBody(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`.toString)
  )

  val testHateoasLinkSE: Link = Link(
    href = s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/c75f40a6-a3df-4429-a697-471eeec46435",
    method = GET,
    rel = "self"
  )

  val testHateoasLinkProperty: Link = Link(
    href = s"/individuals/self-assessment/adjustable-summary/$nino/uk-property/c75f40a6-a3df-4429-a697-471eeec46435",
    method = GET,
    rel = "self"
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
          .wrap(responseObj, TriggerBsasHateoasData(nino, TypeOfBusiness.`self-employment`, responseObj.calculationId))
          .returns(HateoasWrapper(responseObj, Seq(testHateoasLinkSE)))

        val result: Future[Result] = controller.triggerBsas(nino)(fakePostRequest(Json.toJson(requestBody)))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.parse(hateoasResponseForSE(nino))
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }

      "a valid request supplied for business type uk-property" in new Test {

        MockTriggerBsasRequestParser
          .parse(requestRawDataForProperty)
          .returns(Right(requestForProperty))

        MockTriggerBsasService
          .triggerBsas(requestForProperty)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseObj))))

        MockHateoasFactory
          .wrap(responseObj, TriggerBsasHateoasData(nino, TypeOfBusiness.`uk-property-fhl`, responseObj.calculationId))
          .returns(HateoasWrapper(responseObj, Seq(testHateoasLinkProperty)))

        val result: Future[Result] = controller.triggerBsas(nino)(fakePostRequest(Json.toJson(requestBodyForProperty)))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.parse(hateoasResponseForProperty(nino))
        header("X-CorrelationId", result) shouldBe Some(correlationId)
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
          (DownstreamError, INTERNAL_SERVER_ERROR)
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
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (RuleAccountingPeriodNotEndedError, FORBIDDEN),
          (RulePeriodicDataIncompleteError, FORBIDDEN),
          (RuleNoAccountingPeriodError, FORBIDDEN),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
