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

package v3.controllers

import api.controllers.{ ControllerBaseSpec, ControllerTestRunner }
import api.hateoas.Method.GET
import api.hateoas.{ HateoasWrapper, Link, MockHateoasFactory }
import api.mocks.MockIdGenerator
import api.models.ResponseWrapper
import api.models.audit.{ AuditEvent, AuditResponse, GenericAuditDetail }
import api.models.domain.Nino
import api.models.errors._
import api.services.{ MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService }
import config.MockAppConfig
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ AnyContentAsJson, Result }
import routing.Version3
import v3.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._
import v3.mocks.requestParsers.MockSubmitSelfEmploymentRequestParser
import v3.mocks.services._
import v3.models.errors._
import v3.models.request.submitBsas.selfEmployment.{ SubmitSelfEmploymentBsasRawData, SubmitSelfEmploymentBsasRequestData }
import v3.models.response.SubmitSelfEmploymentBsasHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitSelfEmploymentBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitSelfEmploymentRequestParser
    with MockSubmitSelfEmploymentBsasService
    with MockSubmitSelfEmploymentBsasNrsProxyService
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator
    with MockAppConfig {

  private val version = Version3

  private val calculationId = "c75f40a6-a3df-4429-a697-471eeec46435"

  private val rawRequest = SubmitSelfEmploymentBsasRawData(nino, calculationId, None, AnyContentAsJson(mtdRequestJson))
  private val request    = SubmitSelfEmploymentBsasRequestData(Nino(nino), calculationId, None, submitSelfEmploymentBsasRequestBodyModel)

  private val mtdResponseJson = Json.parse(hateoasResponse(nino, calculationId))

  val testHateoasLinks: Seq[Link] = Seq(
    Link(
      href = s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/$calculationId",
      method = GET,
      rel = "self"
    )
  )

  "submitSelfEmploymentBsas" should {
    "return OK" when {
      "the request is valid" in new Test {

        MockSubmitSelfEmploymentBsasDataParser
          .parse(rawRequest)
          .returns(Right(request))

        MockSubmitSelfEmploymentBsasNrsProxyService
          .submit(nino)
          .returns(Future.successful(Unit))

        MockSubmitSelfEmploymentBsasService
          .submitSelfEmploymentBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), SubmitSelfEmploymentBsasHateoasData(nino, calculationId, request.taxYear))
          .returns(HateoasWrapper((), testHateoasLinks))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseJson),
          maybeAuditRequestBody = Some(mtdRequestJson),
          maybeAuditResponseBody = Some(mtdResponseJson)
        )
      }
    }

    "return the error as per spec" when {

      "the parser validation fails" in new Test {
        MockSubmitSelfEmploymentBsasDataParser
          .parse(rawRequest)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError, None)))

        runErrorTestWithAudit(NinoFormatError, maybeAuditRequestBody = Some(mtdRequestJson))
      }

      "multiple parser errors occur" in new Test {

        private val error = ErrorWrapper(correlationId, BadRequestError, Some(List(NinoFormatError, CalculationIdFormatError)))

        MockSubmitSelfEmploymentBsasDataParser
          .parse(rawRequest)
          .returns(Left(error))

        runMultipleErrorsTestWithAudit(List(NinoFormatError, CalculationIdFormatError), maybeAuditRequestBody = Some(mtdRequestJson))
      }

      "multiple errors occur for the customised errors" in new Test {
        val errors = List(
          RuleBothExpensesError.copy(paths = Some(List("expenses"))),
          ValueFormatError.copy(paths = Some(List("turnover")))
        )

        val error: ErrorWrapper = ErrorWrapper(
          correlationId,
          BadRequestError,
          Some(errors)
        )

        MockSubmitSelfEmploymentBsasDataParser
          .parse(rawRequest)
          .returns(Left(error))

        runMultipleErrorsTestWithAudit(errors, maybeAuditRequestBody = Some(mtdRequestJson))
      }

      "the service returns an error" in new Test {
        MockSubmitSelfEmploymentBsasDataParser
          .parse(rawRequest)
          .returns(Right(request))

        MockSubmitSelfEmploymentBsasNrsProxyService
          .submit(nino)
          .returns(Future.successful(Unit))

        MockSubmitSelfEmploymentBsasService
          .submitSelfEmploymentBsas(request)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTypeOfBusinessIncorrectError))))

        runErrorTestWithAudit(RuleTypeOfBusinessIncorrectError, maybeAuditRequestBody = Some(mtdRequestJson))
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking {

    val controller = new SubmitSelfEmploymentBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRequestParser,
      service = mockService,
      nrsService = mockSubmitSelfEmploymentBsasNrsProxyService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.submitSelfEmploymentBsas(nino, calculationId, None)(fakePostRequest(mtdRequestJson))

    protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "SubmitSelfEmploymentAccountingAdjustments",
        transactionName = "submit-self-employment-accounting-adjustments",
        detail = GenericAuditDetail(
          versionNumber = "3.0",
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> nino, "calculationId" -> calculationId),
          requestBody = maybeRequestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

    MockedAppConfig.isApiDeprecated(version) returns false
  }
}
