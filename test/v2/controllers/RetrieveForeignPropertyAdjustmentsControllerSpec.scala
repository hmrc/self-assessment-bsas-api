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

package v2.controllers

import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v2.models.errors._
import v2.fixtures.foreignProperty.RetrieveForeignPropertyAdjustmentsFixtures._
import v2.models.outcomes.ResponseWrapper
import v2.mocks.hateoas.MockHateoasFactory
import v2.mocks.requestParsers.MockRetrieveAdjustmentsRequestParser
import v2.mocks.services._
import v2.models.hateoas.{HateoasWrapper, Link}
import v2.models.hateoas.Method.GET
import v2.models.request.{RetrieveAdjustmentsRawData, RetrieveAdjustmentsRequestData}
import v2.models.response.retrieveBsasAdjustments.foreignProperty.{RetrieveForeignPropertyAdjustmentsHateoasData, RetrieveForeignPropertyAdjustmentsResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveForeignPropertyAdjustmentsControllerSpec extends ControllerBaseSpec
  with MockEnrolmentsAuthService
  with MockMtdIdLookupService
  with MockRetrieveAdjustmentsRequestParser
  with MockRetrieveForeignPropertyAdjustmentsService
  with MockHateoasFactory  {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new RetrieveForeignPropertyAdjustmentsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockRequestParser,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful((Right("test-mtd-id"))))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  private val nino = "AA123456A"
  private val correlationId = "X-123"
  private val bsasId = "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5"

  private val request = RetrieveAdjustmentsRequestData(Nino(nino), bsasId)
  private val requestRawData = RetrieveAdjustmentsRawData(nino, bsasId)

  val testHateoasLinkRetrieveBsas = Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId",
    method = GET, rel = "retrieve-adjustable-summary")

  val testHateoasLinkAdjustSelf = Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId/adjust",
    method = GET, rel = "self")


  "retrieve" should {
    "return successful hateoas response for self-assessment with status OK" when {
      "a valid request is supplied" in new Test {

        MockRetrieveAdjustmentsRequestParser
          .parse(requestRawData)
          .returns(Right(request))

        MockRetrieveForeignPropertyAdjustmentsService
          .retrieveAdjustments(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, validForeignPropertyRetrieveForeignPropertyAdjustmentResponseModel))))

        MockHateoasFactory
          .wrap(validForeignPropertyRetrieveForeignPropertyAdjustmentResponseModel, RetrieveForeignPropertyAdjustmentsHateoasData(nino, bsasId))
          .returns(HateoasWrapper(validForeignPropertyRetrieveForeignPropertyAdjustmentResponseModel, Seq(testHateoasLinkRetrieveBsas, testHateoasLinkAdjustSelf))
          )

        val result: Future[Result] = controller.retrieve(nino, bsasId)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.parse(hateoasResponseForeignPropertyAdjustments(nino, bsasId))
        header("X-CorrelationId", result) shouldBe Some(correlationId)

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

            MockRetrieveForeignPropertyAdjustmentsService
              .retrieveAdjustments(request)
              .returns(Future.successful(Left(ErrorWrapper(Some(correlationId), mtdError))))

            val result: Future[Result] = controller.retrieve(nino, bsasId)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (BsasIdFormatError, BAD_REQUEST),
          (DownstreamError, INTERNAL_SERVER_ERROR),
          (RuleNoAdjustmentsMade, FORBIDDEN),
          (NotFoundError, NOT_FOUND),
          (RuleNotForeignProperty, FORBIDDEN)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
