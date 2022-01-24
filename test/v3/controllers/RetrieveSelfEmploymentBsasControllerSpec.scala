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
import v3.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures._
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockRetrieveSelfEmploymentRequestParser
import v3.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService, MockRetrieveSelfEmploymentBsasService}
import v3.models.errors._
import v3.models.hateoas.Method.{GET, POST}
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.retrieveBsas.selfEmployment.{RetrieveSelfEmploymentBsasRawData, RetrieveSelfEmploymentBsasRequestData}
import v3.models.response.retrieveBsas.selfEmployment.RetrieveSelfAssessmentBsasHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveSelfEmploymentBsasControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveSelfEmploymentRequestParser
    with MockRetrieveSelfEmploymentBsasService
    with MockHateoasFactory
    with MockIdGenerator {

  private val correlationId = "X-123"

  trait Test {
    val hc = HeaderCarrier()

    val controller = new RetrieveSelfEmploymentBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockRequestParser,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)

  }

  private val nino          = "AA123456A"
  private val calculationId = "03e3bc8b-910d-4f5b-88d7-b627c84f2ed7"

  private val request        = RetrieveSelfEmploymentBsasRequestData(Nino(nino), calculationId)
  private val requestRawData = RetrieveSelfEmploymentBsasRawData(nino, calculationId)

  val testHateoasLinkSelf =
    Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$calculationId", method = GET, rel = "self")

  val testHateoasLinkAdjustSubmit = Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$calculationId/adjust",
                                         method = POST,
                                         rel = "submit-summary-adjustments")

  "retrieve" should {
    "return successful hateoas response for self-assessment with status OK" when {
      "a valid request supplied" in new Test {

        MockRetrieveSelfEmploymentRequestParser
          .parse(requestRawData)
          .returns(Right(request))

        MockRetrieveSelfEmploymentBsasService
          .retrieveBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveBsasResponseModel))))

        MockHateoasFactory
          .wrap(retrieveBsasResponseModel, RetrieveSelfAssessmentBsasHateoasData(nino, calculationId))
          .returns(HateoasWrapper(retrieveBsasResponseModel, Seq(testHateoasLinkSelf, testHateoasLinkAdjustSubmit)))

        val result: Future[Result] = controller.retrieve(nino, calculationId)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe mtdRetrieveBsasReponseJsonWithHateoas(nino, calculationId)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockRetrieveSelfEmploymentRequestParser
              .parse(requestRawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.retrieve(nino, calculationId)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST),
          (RuleNotSelfEmployment, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockRetrieveSelfEmploymentRequestParser
              .parse(requestRawData)
              .returns(Right(request))

            MockRetrieveSelfEmploymentBsasService
              .retrieveBsas(request)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.retrieve(nino, calculationId)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (DownstreamError, INTERNAL_SERVER_ERROR),
          (NotFoundError, NOT_FOUND),
          (RuleNotSelfEmployment, BAD_REQUEST)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
