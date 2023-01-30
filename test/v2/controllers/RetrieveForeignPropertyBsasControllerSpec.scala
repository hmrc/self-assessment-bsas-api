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

import mocks.MockIdGenerator
import play.api.libs.json.Json
import play.api.mvc.Result
import domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v2.fixtures.foreignProperty.RetrieveForeignPropertyBsasFixtures._
import v2.mocks.hateoas.MockHateoasFactory
import v2.mocks.requestParsers.MockRetrieveForeignPropertyRequestParser
import v2.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockRetrieveForeignPropertyBsasService}
import v2.models.errors._
import v2.models.hateoas.Method.{GET, POST}
import v2.models.hateoas.{HateoasWrapper, Link}
import v2.models.outcomes.ResponseWrapper
import v2.models.request.retrieveBsas.foreignProperty.{RetrieveForeignPropertyBsasRequestData, RetrieveForeignPropertyRawData}
import v2.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveForeignPropertyBsasControllerSpec
  extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveForeignPropertyRequestParser
    with MockRetrieveForeignPropertyBsasService
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator {

  private val correlationId = "X-123"

  trait Test {
    val hc = HeaderCarrier()

    val controller = new RetrieveForeignPropertyBsasController(
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
  private val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val adjustedMtdStatus = Some("true")
  private val adjustedDesStatus = Some("03")

  private val request = RetrieveForeignPropertyBsasRequestData(Nino(nino), bsasId, adjustedDesStatus)
  private val requestRawData = RetrieveForeignPropertyRawData(nino, bsasId, adjustedMtdStatus)

  val testHateoasLinkPropertySelf = Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId",
    method = GET, rel = "self")

  val testHateoasLinkPropertyAdjust = Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId/adjust",
    method = POST, rel = "submit-summary-adjustments")

  "retrieve" should {
    "return successful hateoas response for property with status OK" when {
      "a valid request supplied" in new Test {

        MockRetrieveForeignPropertyRequestParser
          .parse(requestRawData)
          .returns(Right(request))

        MockRetrieveForeignPropertyBsasService
          .retrieveBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveForeignPropertyBsasResponse))))

        MockHateoasFactory
          .wrap(retrieveForeignPropertyBsasResponse, RetrieveForeignPropertyHateoasData(nino, bsasId))
          .returns(HateoasWrapper(retrieveForeignPropertyBsasResponse, Seq(testHateoasLinkPropertySelf, testHateoasLinkPropertyAdjust)))

        val result: Future[Result] = controller.retrieve(nino, bsasId, adjustedMtdStatus)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.parse(hateoasResponseForeignProperty(nino, bsasId))
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockRetrieveForeignPropertyRequestParser
              .parse(requestRawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.retrieve(nino, bsasId, adjustedMtdStatus)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (BsasIdFormatError, BAD_REQUEST),
          (AdjustedStatusFormatError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockRetrieveForeignPropertyRequestParser
              .parse(requestRawData)
              .returns(Right(request))

            MockRetrieveForeignPropertyBsasService
              .retrieveBsas(request)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.retrieve(nino, bsasId, adjustedMtdStatus)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (RuleNoAdjustmentsMade, FORBIDDEN),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR),
          (RuleNotForeignProperty, FORBIDDEN)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
