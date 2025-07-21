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

package v5.selfEmploymentBsas.submit

import common.errors.*
import play.api.Configuration
import play.api.libs.json.JsValue
import play.api.mvc.Result
import shared.config.MockSharedAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.CalculationId
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v5.selfEmploymentBsas.submit.def1.model.request.Def1_SubmitSelfEmploymentBsasRequestData
import v5.selfEmploymentBsas.submit.def1.model.request.fixtures.SubmitSelfEmploymentBsasFixtures.{mtdRequestJson, submitSelfEmploymentBsasRequestBody}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitSelfEmploymentBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitSelfEmploymentBsasValidatorFactory
    with MockSubmitSelfEmploymentBsasService
    with MockAuditService
    with MockIdGenerator
    with MockSharedAppConfig {

  private val calculationId = CalculationId("c75f40a6-a3df-4429-a697-471eeec46435")
  private val requestData   = Def1_SubmitSelfEmploymentBsasRequestData(parsedNino, calculationId, None, submitSelfEmploymentBsasRequestBody)

  "submitSelfEmploymentBsas" should {
    "return OK" when {
      "the request is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockSubmitSelfEmploymentBsasService
          .submitSelfEmploymentBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = None,
          maybeAuditRequestBody = Some(mtdRequestJson),
          maybeAuditResponseBody = None
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
        private val errors = List(
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

    val controller: SubmitSelfEmploymentBsasController = new SubmitSelfEmploymentBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockSubmitSelfEmploymentBsasValidatorFactory,
      service = mockService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

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
