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
import v3.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockSubmitUkPropertyRequestParser
import v3.mocks.services._
import v3.models.errors._
import v3.models.hateoas.Method.GET
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.submitBsas.ukProperty.{SubmitUkPropertyBsasRawData, SubmitUkPropertyBsasRequestData}
import v3.models.response.SubmitUkPropertyBsasHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitUkPropertyBsasControllerSpec
  extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitUkPropertyRequestParser
    with MockSubmitUkPropertyBsasService
    with MockSubmitUKPropertyBsasNrsProxyService
    with MockHateoasFactory
    with MockIdGenerator {

  private val correlationId = "X-123"
  private val nino = "AA123456A"
  private val calculationId = "c75f40a6-a3df-4429-a697-471eeec46435"

  private val rawData = SubmitUkPropertyBsasRawData(
    nino = nino,
    calculationId = calculationId,
    body = submitBsasRawDataBodyNonFHL(
      income = nonFHLIncomeAllFields,
      expenses = nonFHLExpensesAllFields
    )
  )
  private val requestData = SubmitUkPropertyBsasRequestData(
    nino = Nino(nino),
    calculationId = calculationId,
    body = nonFHLBody
  )

  val testHateoasLinks: Seq[Link] = Seq(
    Link(
      href = s"/individuals/self-assessment/adjustable-summary/$nino/uk-property/$calculationId",
      method = GET,
      rel = "self"
    )
  )

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new SubmitUkPropertyBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockRequestParser,
      service = mockService,
      nrsService = mockSubmitUKPropertyBsasNrsProxyService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)

  }

  "submitUkPropertyBsas" should {
    "return a successful hateoas response with status 200 (OK)" when {
      "a valid request is supplied" in new Test {

        MockSubmitUkPropertyBsasDataParser
          .parse(rawData)
          .returns(Right(requestData))

        MockSubmitUKPropertyBsasNrsProxyService
          .submit(nino)
          .returns(Future.successful(Unit))

        MockSubmitUkPropertyBsasService
          .submitPropertyBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), SubmitUkPropertyBsasHateoasData(nino, calculationId))
          .returns(HateoasWrapper((), testHateoasLinks))

        val result: Future[Result] = controller.submitUkPropertyBsas(nino, calculationId)(fakePostRequest(validNonFHLInputJson))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.parse(hateoasResponse(nino, calculationId))
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return parser errors as per spec" when {
      def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
        s"a ${error.code} error is returned from the parser" in new Test {

          MockSubmitUkPropertyBsasDataParser
            .parse(rawData)
            .returns(Left(ErrorWrapper(correlationId, error, None)))

          val result: Future[Result] = controller.submitUkPropertyBsas(nino, calculationId)(fakePostRequest(validNonFHLInputJson))

          status(result) shouldBe expectedStatus
          contentAsJson(result) shouldBe Json.toJson(error)
          header("X-CorrelationId", result) shouldBe Some(correlationId)
        }
      }

      val paths = Some(Seq("/path"))
      val input = Seq(
        (BadRequestError, BAD_REQUEST),
        (NinoFormatError, BAD_REQUEST),
        (CalculationIdFormatError, BAD_REQUEST),
        (ValueFormatError.copy(paths = paths), BAD_REQUEST),
        (RuleBothExpensesError.copy(paths = paths), BAD_REQUEST),
        (RuleIncorrectOrEmptyBodyError.copy(paths = paths), BAD_REQUEST),
        (RuleBothPropertiesSuppliedError, BAD_REQUEST)
      )

      input.foreach(args => (errorsFromParserTester _).tupled(args))

      "multiple parser errors occur" in new Test {

        val error: ErrorWrapper = ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, CalculationIdFormatError)))

        MockSubmitUkPropertyBsasDataParser
          .parse(rawData)
          .returns(Left(error))

        val result: Future[Result] = controller.submitUkPropertyBsas(nino, calculationId)(fakePostRequest(validNonFHLInputJson))

        status(result) shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(error)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return downstream errors as per the spec" when {
      def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
        s"a ${mtdError.code} error is returned from the service" in new Test {

          MockSubmitUkPropertyBsasDataParser
            .parse(rawData)
            .returns(Right(requestData))

          MockSubmitUKPropertyBsasNrsProxyService
            .submit(nino)
            .returns(Future.successful(Unit))

          MockSubmitUkPropertyBsasService
            .submitPropertyBsas(requestData)
            .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

          val result: Future[Result] = controller.submitUkPropertyBsas(nino, calculationId)(fakePostRequest(validNonFHLInputJson))

          status(result) shouldBe expectedStatus
          contentAsJson(result) shouldBe Json.toJson(mtdError)
          header("X-CorrelationId", result) shouldBe Some(correlationId)
        }
      }

      val input = Seq(
        (NinoFormatError, BAD_REQUEST),
        (CalculationIdFormatError, BAD_REQUEST),
        (DownstreamError, INTERNAL_SERVER_ERROR),
        (RulePropertyIncomeAllowanceClaimed, FORBIDDEN),
        (RuleOverConsolidatedExpensesThreshold, FORBIDDEN),
        (NotFoundError, NOT_FOUND),
        (RuleSummaryStatusSuperseded, FORBIDDEN),
        (RuleAlreadyAdjusted, FORBIDDEN),
        (RuleResultingValueNotPermitted, FORBIDDEN),
        (RuleSummaryStatusInvalid, FORBIDDEN),
        (RuleTypeOfBusinessIncorrectError, BAD_REQUEST)
      )
      input.foreach(args => (serviceErrors _).tupled(args))
    }
  }
}