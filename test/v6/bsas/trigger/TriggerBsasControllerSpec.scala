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

package v6.bsas.trigger

import common.errors.*
import play.api.Configuration
import play.api.libs.json.JsValue
import play.api.mvc.Result
import shared.config.MockSharedAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.*
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v6.bsas.trigger.def1.model.Def1_TriggerBsasFixtures.*
import v6.bsas.trigger.def1.model.request.Def1_TriggerBsasRequestData
import v6.bsas.trigger.def2.model.Def2_TriggerBsasFixtures.{mtdResponseJs, requestBody, requestBodyForProperty, responseObj, _}
import v6.bsas.trigger.def2.model.request.Def2_TriggerBsasRequestData
import v6.common.model.{TypeOfBusiness, TypeOfBusinessWithFHL}

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
    with MockSharedAppConfig {

  private val requestDataDef1 = Def1_TriggerBsasRequestData(
    parsedNino,
    triggerBsasRequestDataBody()
  )

  private val requestDataDef2 = Def2_TriggerBsasRequestData(
    parsedNino,
    triggerBsasRequestDataBodyDef2()
  )

  private val requestForPropertywithFHL = Def1_TriggerBsasRequestData(
    parsedNino,
    triggerBsasRequestDataBody(typeOfBusiness = TypeOfBusinessWithFHL.`uk-property-fhl`)
  )

  private val requestForProperty = Def2_TriggerBsasRequestData(
    parsedNino,
    triggerBsasRequestDataBodyDef2(typeOfBusiness = TypeOfBusiness.`uk-property`)
  )

  "triggerBsas with Def1" should {
    "return OK" when {
      "a valid request is supplied for business type self-employment" in new Test {
        private val mtdResponseJson = mtdResponseJs

        willUseValidator(returningSuccess(requestDataDef1))

        MockTriggerBsasService
          .triggerBsas(requestDataDef1)
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

        willUseValidator(returningSuccess(requestForPropertywithFHL))

        MockTriggerBsasService
          .triggerBsas(requestForPropertywithFHL)
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
        willUseValidator(returning(NinoFormatError))
        runErrorTestWithAudit(NinoFormatError, maybeAuditRequestBody = Some(requestBody))
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestDataDef1))

        MockTriggerBsasService
          .triggerBsas(requestDataDef1)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleObligationsNotMet))))

        runErrorTestWithAudit(RuleObligationsNotMet, maybeAuditRequestBody = Some(requestBody))
      }
    }
  }

  "triggerBsas with Def2" should {
    "return OK" when {
      "a valid request is supplied for business type self-employment" in new Test {
        private val mtdResponseJson = mtdResponseJs

        willUseValidator(returningSuccess(requestDataDef2))

        MockTriggerBsasService
          .triggerBsas(requestDataDef2)
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
        willUseValidator(returning(NinoFormatError))
        runErrorTestWithAudit(NinoFormatError, maybeAuditRequestBody = Some(requestBody))
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestDataDef2))

        MockTriggerBsasService
          .triggerBsas(requestDataDef2)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleObligationsNotMet))))

        runErrorTestWithAudit(RuleObligationsNotMet, maybeAuditRequestBody = Some(requestBody))
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller = new TriggerBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockTriggerBsasValidatorFactory,
      service = mockService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected val requestBodyForController: JsValue = requestBody

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

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
