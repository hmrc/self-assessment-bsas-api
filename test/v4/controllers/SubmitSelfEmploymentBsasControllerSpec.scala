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

package v4.controllers

import common.errors._
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.config.MockAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.Method.GET
import shared.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.CalculationId
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v4.controllers.validators.MockSubmitSelfEmploymentBsasValidatorFactory
import v4.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._
import v4.mocks.services.MockSubmitSelfEmploymentBsasService
import v4.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestData
import v4.models.response.SubmitSelfEmploymentBsasHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitSelfEmploymentBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitSelfEmploymentBsasValidatorFactory
    with MockSubmitSelfEmploymentBsasService
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator
    with MockAppConfig {

  private val calculationId   = CalculationId("c75f40a6-a3df-4429-a697-471eeec46435")
  private val requestData     = SubmitSelfEmploymentBsasRequestData(parsedNino, calculationId, None, submitSelfEmploymentBsasRequestBodyModel)
  private val mtdResponseJson = Json.parse(hateoasResponse(parsedNino, calculationId))

  val testHateoasLinks: Seq[Link] = Seq(
    Link(
      href = s"/individuals/self-assessment/adjustable-summary/$validNino/self-employment/$calculationId",
      method = GET,
      rel = "self"
    )
  )

  "submitSelfEmploymentBsas" should {
    "return OK" when {
      "the request is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockSubmitSelfEmploymentBsasService
          .submitSelfEmploymentBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), SubmitSelfEmploymentBsasHateoasData(validNino, calculationId.calculationId, requestData.taxYear))
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
        willUseValidator(returning(NinoFormatError))
        runErrorTestWithAudit(NinoFormatError, maybeAuditRequestBody = Some(mtdRequestJson))
      }

      "multiple parser errors occur" in new Test {
        private val errors = List(CalculationIdFormatError, NinoFormatError)
        willUseValidator(returningErrors(errors))
        runMultipleErrorsTestWithAudit(errors, maybeAuditRequestBody = Some(mtdRequestJson))
      }

      "multiple errors occur for the customised errors" in new Test {
        val errors = List(
          ValueFormatError.copy(paths = Some(List("turnover"))),
          RuleBothExpensesError.copy(paths = Some(List("expenses")))
        )

        willUseValidator(returningErrors(errors))
        runMultipleErrorsTestWithAudit(errors, maybeAuditRequestBody = Some(mtdRequestJson))
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockSubmitSelfEmploymentBsasService
          .submitSelfEmploymentBsas(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTypeOfBusinessIncorrectError))))

        runErrorTestWithAudit(RuleTypeOfBusinessIncorrectError, maybeAuditRequestBody = Some(mtdRequestJson))
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller = new SubmitSelfEmploymentBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockSubmitSelfEmploymentBsasValidatorFactory,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] =
      controller.submitSelfEmploymentBsas(validNino, calculationId.calculationId, None)(fakePostRequest(mtdRequestJson))

    protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "SubmitSelfEmploymentAccountingAdjustments",
        transactionName = "submit-self-employment-accounting-adjustments",
        detail = GenericAuditDetail(
          versionNumber = apiVersion.name,
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "calculationId" -> calculationId.calculationId),
          requestBody = maybeRequestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
