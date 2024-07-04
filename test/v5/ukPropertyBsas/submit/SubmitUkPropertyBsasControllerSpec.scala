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

package v5.ukPropertyBsas.submit

import common.errors._
import config.MockBsasConfig
import play.api.libs.json.JsValue
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{CalculationId, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v5.ukPropertyBsas.submit.def1.model.request.Def1_SubmitUkPropertyBsasRequestData
import v5.ukPropertyBsas.submit.def1.model.request.SubmitUKPropertyBsasRequestBodyFixtures._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitUkPropertyBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitUkPropertyBsasValidatorFactory
    with MockSubmitUkPropertyBsasService
    with MockIdGenerator
    with MockAuditService
    with MockBsasConfig {

  private val calculationId = CalculationId("c75f40a6-a3df-4429-a697-471eeec46435")
  private val rawTaxYear    = "2023-24"
  private val taxYear       = TaxYear.fromMtd(rawTaxYear)

  private val requestData = Def1_SubmitUkPropertyBsasRequestData(
    nino = parsedNino,
    calculationId = calculationId,
    body = nonFHLBody,
    taxYear = Some(taxYear)
  )

  "submitUkPropertyBsas" should {

    "return OK" when {
      "the request is valid" in new Test {
        MockedBsasConfig.secondaryAgentEndpointsAccessControlConfig.returns(MockedBsasConfig.bsasSecondaryAgentConfig)
        willUseValidator(returningSuccess(requestData))

        MockSubmitUkPropertyBsasService
          .submitPropertyBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = None,
          maybeAuditRequestBody = Some(validNonFHLInputJson),
          maybeAuditResponseBody = None
        )
      }
    }

    "return parser errors as per spec" when {

      "the parser validation fails" in new Test {
        MockedBsasConfig.secondaryAgentEndpointsAccessControlConfig.returns(MockedBsasConfig.bsasSecondaryAgentConfig)
        willUseValidator(returning(NinoFormatError))
        runErrorTestWithAudit(NinoFormatError, maybeAuditRequestBody = Some(validNonFHLInputJson))
      }

      "multiple parser errors occur" in new Test {
        private val errors = List(CalculationIdFormatError, NinoFormatError)
        MockedBsasConfig.secondaryAgentEndpointsAccessControlConfig.returns(MockedBsasConfig.bsasSecondaryAgentConfig)
        willUseValidator(returningErrors(errors))
        runMultipleErrorsTestWithAudit(errors, maybeAuditRequestBody = Some(validNonFHLInputJson))
      }
    }

    "the service returns an error" in new Test {
      MockedBsasConfig.secondaryAgentEndpointsAccessControlConfig.returns(MockedBsasConfig.bsasSecondaryAgentConfig)
      willUseValidator(returningSuccess(requestData))

      MockSubmitUkPropertyBsasService
        .submitPropertyBsas(requestData)
        .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleOverConsolidatedExpensesThreshold))))

      runErrorTestWithAudit(RuleOverConsolidatedExpensesThreshold, maybeAuditRequestBody = Some(validNonFHLInputJson))
    }
  }

  trait Test extends ControllerTest with AuditEventChecking {

    val controller = new SubmitUkPropertyBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockSubmitUkPropertyBsasValidatorFactory,
      service = mockService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator,
      bsasConfig = mockBsasConfig
    )

    protected def callController(): Future[Result] =
      controller.handleRequest(validNino, calculationId.calculationId, Some(rawTaxYear))(fakePostRequest(validNonFHLInputJson))

    protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "SubmitUKPropertyAccountingAdjustments",
        transactionName = "submit-uk-property-accounting-adjustments",
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
