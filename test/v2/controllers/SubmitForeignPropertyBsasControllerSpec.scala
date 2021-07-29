/*
 * Copyright 2021 HM Revenue & Customs
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
import v2.mocks.hateoas.MockHateoasFactory
import v2.mocks.requestParsers.MockSubmitForeignPropertyBsasRequestParser
import v2.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockSubmitForeignPropertyBsasNrsProxyService, MockSubmitForeignPropertyBsasService}
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import v2.models.hateoas.{HateoasWrapper, Link}
import v2.models.hateoas.Method.GET
import v2.models.outcomes.ResponseWrapper
import v2.models.request.submitBsas.foreignProperty._
import v2.models.response.{SubmitForeignPropertyBsasHateoasData, SubmitForeignPropertyBsasResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitForeignPropertyBsasControllerSpec
  extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitForeignPropertyBsasService
    with MockSubmitForeignPropertyBsasRequestParser
    with MockSubmitForeignPropertyBsasNrsProxyService
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator
{

  private val correlationId = "X-123"

  trait Test {
    val hc = HeaderCarrier()

    val controller = new SubmitForeignPropertyBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRequestParser,
      service = mockService,
      nrsService = mockSubmitForeignPropertyBsasNrsProxyService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)

  }

  private val nino = "AA123456A"
  private val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val testHateoasLink = Link(href = s"individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId/adjust", method = GET, rel = "self")

  private val requestJson = Json.parse(
    """|{
       |    "foreignProperty": {
       |        "income": {
       |            "rentIncome": 123.12,
       |            "premiumsOfLeaseGrant": 123.12,
       |            "otherPropertyIncome": 123.12
       |        },
       |        "expenses": {
       |            "premisesRunningCosts": 123.12,
       |            "repairsAndMaintenance": 123.12,
       |            "financialCosts": 123.12,
       |            "professionalFees": 123.12,
       |            "travelCosts": 123.12,
       |            "costOfServices": 123.12,
       |            "residentialFinancialCost": 123.12,
       |            "other": 123.12
       |        }
       |    }
       |}
       |""".stripMargin
  )

  private val foreignProperty: ForeignProperty =
    ForeignProperty(
      "FRA",
      Some(ForeignPropertyIncome(
        Some(123.12),
        Some(123.12),
        Some(123.12)
      )),
      Some(ForeignPropertyExpenses(
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        Some(123.12),
        None
      ))
    )

  val requestBody: SubmitForeignPropertyBsasRequestBody =
    SubmitForeignPropertyBsasRequestBody(Some(Seq(foreignProperty)), None)

  val responseBody = SubmitForeignPropertyBsasResponse("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", TypeOfBusiness.`foreign-property`)

  private val rawData = SubmitForeignPropertyRawData(nino, bsasId, requestJson)
  private val requestData = SubmitForeignPropertyBsasRequestData(Nino(nino), bsasId, requestBody)

  "handleRequest" should {
    "return Ok" when {
      "the request received is valid" in new Test {

        MockSubmitForeignPropertyBsasRequestParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockSubmitForeignPropertyBsasNrsProxyService
          .submit(nino)
          .returns(Future.successful(Unit))

        MockSubmitForeignPropertyBsasService
          .submitForeignPropertyBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, SubmitForeignPropertyBsasHateoasData(nino, bsasId))
          .returns(HateoasWrapper(responseBody, Seq(testHateoasLink)))

        val result: Future[Result] = controller.handleRequest(nino, bsasId)(fakePostRequest(requestJson))
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }
    "return the error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockSubmitForeignPropertyBsasRequestParser
              .parseRequest(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.handleRequest(nino, bsasId)(fakePostRequest(requestJson))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (BsasIdFormatError, BAD_REQUEST),
          (FormatAdjustmentValueError, BAD_REQUEST),
          (RuleAdjustmentRangeInvalid, BAD_REQUEST),
          (RuleIncorrectOrEmptyBodyError, BAD_REQUEST),
          (RuleBothExpensesError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" should {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockSubmitForeignPropertyBsasRequestParser
              .parseRequest(rawData)
              .returns(Right(requestData))

            MockSubmitForeignPropertyBsasNrsProxyService
              .submit(nino)
              .returns(Future.successful(Unit))

            MockSubmitForeignPropertyBsasService
              .submitForeignPropertyBsas(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.handleRequest(nino, bsasId)(fakePostRequest(requestJson))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
            (NinoFormatError, BAD_REQUEST),
            (BsasIdFormatError, BAD_REQUEST),
            (NotFoundError, NOT_FOUND),
            (DownstreamError, INTERNAL_SERVER_ERROR),
            (RuleTypeOfBusinessError, FORBIDDEN),
            (RuleSummaryStatusInvalid, FORBIDDEN),
            (RuleSummaryStatusSuperseded, FORBIDDEN),
            (RuleBsasAlreadyAdjusted, FORBIDDEN),
            (RuleOverConsolidatedExpensesThreshold, FORBIDDEN),
            (RulePropertyIncomeAllowanceClaimed, FORBIDDEN),
            (RuleResultingValueNotPermitted, FORBIDDEN)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}