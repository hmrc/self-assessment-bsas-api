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

package v5.bsas.trigger

import common.errors._
import config.MockBsasConfig
import play.api.libs.json.JsValue
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v5.bsas.trigger.def1.model.Def1_TriggerBsasFixtures._
import v5.bsas.trigger.def1.model.request.Def1_TriggerBsasRequestData
import v5.common.model.TypeOfBusiness

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TriggerBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockTriggerBsasValidatorFactory
    with MockTriggerBsasService
    with MockIdGenerator
    with MockAuditService
    with MockBsasConfig {

  private val requestData = Def1_TriggerBsasRequestData(
    parsedNino,
    triggerBsasRequestDataBody()
  )

  private val requestForProperty = Def1_TriggerBsasRequestData(
    parsedNino,
    triggerBsasRequestDataBody(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`)
  )

  "triggerBsas" should {
    "return OK" when {
      "a valid request is supplied for business type self-employment" in new Test {
        private val mtdResponseJson = mtdResponseJs

        MockedBsasConfig.secondaryAgentEndpointsAccessControlConfig.returns(MockedBsasConfig.bsasSecondaryAgentConfig)
        willUseValidator(returningSuccess(requestData))

        MockTriggerBsasService
          .triggerBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseObj))))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseJson),
          maybeAuditRequestBody = Some(requestBody),
          maybeAuditResponseBody = Some(mtdResponseJson)
        )
      }

      "a valid request is supplied for business type uk-property" in new Test {
        override protected val requestBodyForController: JsValue = requestBodyForProperty

        private val mtdResponseJson = mtdResponseJs

        MockedBsasConfig.secondaryAgentEndpointsAccessControlConfig.returns(MockedBsasConfig.bsasSecondaryAgentConfig)
        willUseValidator(returningSuccess(requestForProperty))

        MockTriggerBsasService
          .triggerBsas(requestForProperty)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseObj))))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseJson),
          maybeAuditRequestBody = Some(requestBodyForProperty),
          maybeAuditResponseBody = Some(mtdResponseJson)
        )
      }
    }

    "return the error as per spec" when {

      "the parser validation fails" in new Test {
        MockedBsasConfig.secondaryAgentEndpointsAccessControlConfig.returns(MockedBsasConfig.bsasSecondaryAgentConfig)
        willUseValidator(returning(NinoFormatError))
        runErrorTestWithAudit(NinoFormatError, maybeAuditRequestBody = Some(requestBody))
      }

      "the service returns an error" in new Test {
        MockedBsasConfig.secondaryAgentEndpointsAccessControlConfig.returns(MockedBsasConfig.bsasSecondaryAgentConfig)
        willUseValidator(returningSuccess(requestData))

        MockTriggerBsasService
          .triggerBsas(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RulePeriodicDataIncompleteError))))

        runErrorTestWithAudit(RulePeriodicDataIncompleteError, maybeAuditRequestBody = Some(requestBody))
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking {

    val controller = new TriggerBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockTriggerBsasValidatorFactory,
      service = mockService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator,
      bsasConfig = mockBsasConfig
    )

    protected val requestBodyForController: JsValue = requestBody

    protected def callController(): Future[Result] =
      controller.triggerBsas(validNino)(fakePostRequest(requestBodyForController))

    protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "TriggerBusinessSourceAdjustableSummary",
        transactionName = "trigger-business-source-adjustable-summary",
        detail = GenericAuditDetail(
          versionNumber = apiVersion.name,
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino),
          requestBody = maybeRequestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
