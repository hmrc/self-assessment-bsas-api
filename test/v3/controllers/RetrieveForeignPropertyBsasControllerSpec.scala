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
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockRetrieveForeignPropertyRequestParser
import v3.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService, MockRetrieveForeignPropertyBsasService}
import v3.models.errors._
import v3.models.hateoas.Method.GET
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.retrieveBsas.foreignProperty.{RetrieveForeignPropertyBsasRawData, RetrieveForeignPropertyBsasRequestData}
import v3.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveForeignPropertyBsasControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveForeignPropertyRequestParser
    with MockRetrieveForeignPropertyBsasService
    with MockHateoasFactory
    with MockIdGenerator {

  private val correlationId = "X-123"

  private val nino   = "AA123456A"
  private val calcId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val request        = RetrieveForeignPropertyBsasRequestData(Nino(nino), calcId)
  private val requestRawData = RetrieveForeignPropertyBsasRawData(nino, calcId)

  private val testHateoasLinks =
    Seq(Link(href = "/some/link", method = GET, rel = "someRel"))

  private val hateoasResponse = retrieveForeignPropertyBsasMtdNonFhlJson.as[JsObject] ++ Json.parse(
    """{
      |  "links": [ { "href":"/some/link", "method":"GET", "rel":"someRel" } ]
      |}
      |""".stripMargin).as[JsObject]

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new RetrieveForeignPropertyBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockRequestParser,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino) returns Future.successful(Right("test-mtd-id"))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId returns correlationId
  }

  "retrieve" should {
    "return successful hateoas response for property with status OK" when {
      "a valid request supplied" in new Test {
        MockRetrieveForeignPropertyRequestParser.parse(requestRawData) returns Right(request)

        MockRetrieveForeignPropertyBsasService.retrieveBsas(request) returns
          Future.successful(Right(ResponseWrapper(correlationId, retrieveForeignPropertyBsasResponseNonFhlModel)))

        MockHateoasFactory
          .wrap(retrieveForeignPropertyBsasResponseNonFhlModel, RetrieveForeignPropertyHateoasData(nino, calcId)) returns
          HateoasWrapper(retrieveForeignPropertyBsasResponseNonFhlModel, testHateoasLinks)

        val result: Future[Result] = controller.retrieve(nino, calcId)(fakeGetRequest)

        contentAsJson(result) shouldBe hateoasResponse
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {
            MockRetrieveForeignPropertyRequestParser.parse(requestRawData) returns Left(ErrorWrapper(correlationId, error, None))

            val result: Future[Result] = controller.retrieve(nino, calcId)(fakeGetRequest)

            contentAsJson(result) shouldBe Json.toJson(error)
            status(result) shouldBe expectedStatus
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {
            MockRetrieveForeignPropertyRequestParser.parse(requestRawData) returns Right(request)

            MockRetrieveForeignPropertyBsasService.retrieveBsas(request) returns Future.successful(Left(ErrorWrapper(correlationId, mtdError)))

            val result: Future[Result] = controller.retrieve(nino, calcId)(fakeGetRequest)

            contentAsJson(result) shouldBe Json.toJson(mtdError)
            status(result) shouldBe expectedStatus
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST),
          (RuleTypeOfBusinessIncorrectError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (InternalError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
