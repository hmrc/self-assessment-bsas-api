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
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockSubmitForeignPropertyBsasRequestParser
import v3.mocks.services._
import v3.models.audit.{ AuditError, AuditEvent, AuditResponse, GenericAuditDetail }
import v3.models.domain.TaxYear
import v3.models.errors._
import v3.models.hateoas.Method.GET
import v3.models.hateoas.{ HateoasWrapper, Link }
import v3.models.outcomes.ResponseWrapper
import v3.models.request.submitBsas.foreignProperty._
import v3.models.response.SubmitForeignPropertyBsasHateoasData

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
    with MockIdGenerator {

  private val nino          = "AA123456A"
  private val bsasId        = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val correlationId = "X-123"
  private val rawTaxYear    = "2023-24"
  private val taxYear       = TaxYear.fromMtd(rawTaxYear)

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new SubmitForeignPropertyBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRequestParser,
      service = mockService,
      nrsService = mockSubmitForeignPropertyBsasNrsProxyService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)

  }

  private val testHateoasLink =
    Link(href = s"individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId/adjust", method = GET, rel = "self")

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
      Some(
        ForeignPropertyIncome(
          Some(123.12),
          Some(123.12),
          Some(123.12)
        )),
      Some(
        ForeignPropertyExpenses(
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

  private val rawData     = SubmitForeignPropertyRawData(nino, bsasId, Some(rawTaxYear), requestJson)
  private val requestData = SubmitForeignPropertyBsasRequestData(Nino(nino), bsasId, Some(taxYear), requestBody)

  def event(auditResponse: AuditResponse): AuditEvent[GenericAuditDetail] =
    AuditEvent(
      auditType = "SubmitForeignPropertyAccountingAdjustments",
      transactionName = "submit-foreign-property-accounting-adjustments",
      detail = GenericAuditDetail(
        versionNumber = "3.0",
        userType = "Individual",
        agentReferenceNumber = None,
        params = Map("nino" -> nino, "calculationId" -> bsasId),
        requestBody = Some(requestJson),
        `X-CorrelationId` = correlationId,
        auditResponse = auditResponse
      )
    )

  val responseBody: JsValue = Json.parse(s"""
       |{
       |  "links":[
       |    {
       |      "href":"individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId/adjust",
       |      "rel":"self",
       |      "method":"GET"
       |    }
       |  ]
       |}
       |""".stripMargin)

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
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), SubmitForeignPropertyBsasHateoasData(nino, bsasId, requestData.taxYear))
          .returns(HateoasWrapper((), Seq(testHateoasLink)))

        val result: Future[Result] = controller.handleRequest(nino, bsasId, Some(rawTaxYear))(fakePostRequest(requestJson))
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(OK, None, Some(responseBody))
        MockedAuditService.verifyAuditEvent(event(auditResponse)).once
      }
    }

    "return the error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockSubmitForeignPropertyBsasRequestParser
              .parseRequest(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.handleRequest(nino, bsasId, Some(rawTaxYear))(fakePostRequest(requestJson))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(error.code))), None)
            MockedAuditService.verifyAuditEvent(event(auditResponse)).once
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (RuleTaxYearRangeInvalidError, BAD_REQUEST),
          (InvalidTaxYearParameterError, BAD_REQUEST),
          (ValueFormatError, BAD_REQUEST),
          (CountryCodeFormatError, BAD_REQUEST),
          (RuleDuplicateCountryCodeError, BAD_REQUEST),
          (RuleCountryCodeError, BAD_REQUEST),
          (RuleBothPropertiesSuppliedError, BAD_REQUEST),
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

            val result: Future[Result] = controller.handleRequest(nino, bsasId, Some(rawTaxYear))(fakePostRequest(requestJson))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(mtdError.code))), None)
            MockedAuditService.verifyAuditEvent(event(auditResponse)).once
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (InternalError, INTERNAL_SERVER_ERROR),
          (RuleTypeOfBusinessIncorrectError, BAD_REQUEST),
          (RuleSummaryStatusInvalid, BAD_REQUEST),
          (RuleSummaryStatusSuperseded, BAD_REQUEST),
          (RuleAlreadyAdjusted, BAD_REQUEST),
          (RuleOverConsolidatedExpensesThreshold, BAD_REQUEST),
          (RulePropertyIncomeAllowanceClaimed, BAD_REQUEST),
          (RuleResultingValueNotPermitted, BAD_REQUEST)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
