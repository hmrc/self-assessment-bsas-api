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
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v3.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures._
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockRetrieveUkPropertyRequestParser
import v3.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService, MockRetrieveUkPropertyBsasService}
import v3.models.errors._
import v3.models.hateoas.Method.GET
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.retrieveBsas.ukProperty.{RetrieveUkPropertyBsasRawData, RetrieveUkPropertyBsasRequestData}
import v3.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveUkPropertyBsasControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveUkPropertyRequestParser
    with MockRetrieveUkPropertyBsasService
    with MockHateoasFactory
    with MockIdGenerator {

  private val correlationId = "X-123"

  private val nino          = "AA123456A"
  private val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val request        = RetrieveUkPropertyBsasRequestData(Nino(nino), calculationId)
  private val requestRawData = RetrieveUkPropertyBsasRawData(nino, calculationId)

  private val testHateoasLinks =
    Seq(Link(href = "/some/link", method = GET, rel = "someRel"))

  private val hateoasFhlResponse = mtdRetrieveBsasResponseFhlJson.as[JsObject] ++ Json.parse(
    """{
      |  "links": [ { "href":"/some/link", "method":"GET", "rel":"someRel" } ]
      |}
      |""".stripMargin).as[JsObject]

  private val hateoasNonFhlResponse = mtdRetrieveBsasResponseNonFhlJson.as[JsObject] ++ Json.parse(
    """{
      |  "links": [ { "href":"/some/link", "method":"GET", "rel":"someRel" } ]
      |}
      |""".stripMargin).as[JsObject]

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new RetrieveUkPropertyBsasController(
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

  "retrieve" should {
    "return successful hateoas response for fhl with status OK" when {
      "a valid request supplied" in new Test {

        MockRetrieveUkPropertyRequestParser
          .parse(requestRawData)
          .returns(Right(request))

        MockRetrieveUkPropertyBsasService
          .retrieveBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveBsasResponseFhlModel))))

        MockHateoasFactory
          .wrap(retrieveBsasResponseFhlModel, RetrieveUkPropertyHateoasData(nino, calculationId))
          .returns(HateoasWrapper(retrieveBsasResponseFhlModel, testHateoasLinks))

        val result: Future[Result] = controller.retrieve(nino, calculationId)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe hateoasFhlResponse
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }
    "return successful hateoas response for non-fhl with status OK" when {
      "a valid request supplied" in new Test {

        MockRetrieveUkPropertyRequestParser
          .parse(requestRawData)
          .returns(Right(request))

        MockRetrieveUkPropertyBsasService
          .retrieveBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveBsasResponseNonFhlModel))))

        MockHateoasFactory
          .wrap(retrieveBsasResponseNonFhlModel, RetrieveUkPropertyHateoasData(nino, calculationId))
          .returns(HateoasWrapper(retrieveBsasResponseNonFhlModel, testHateoasLinks))

        val result: Future[Result] = controller.retrieve(nino, calculationId)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe hateoasNonFhlResponse
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockRetrieveUkPropertyRequestParser
              .parse(requestRawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.retrieve(nino, calculationId)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST),
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
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.retrieve(nino, calculationId)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST),
          (RuleNotUkProperty, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
