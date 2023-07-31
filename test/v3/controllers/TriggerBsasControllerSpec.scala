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

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.hateoas.Method.GET
import api.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import api.mocks.MockIdGenerator
import api.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import api.models.domain.TaxYear
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.MockAuditService
import mocks.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import v3.controllers.validators.MockTriggerBsasValidatorFactory
import v3.fixtures.TriggerBsasRequestBodyFixtures._
import v3.mocks.services.MockTriggerBsasService
import v3.models.domain.TypeOfBusiness
import v3.models.errors._
import v3.models.request.triggerBsas.TriggerBsasRequestData
import v3.models.response.TriggerBsasHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TriggerBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockTriggerBsasValidatorFactory
    with MockTriggerBsasService
    with MockHateoasFactory
    with MockIdGenerator
    with MockAuditService
    with MockAppConfig {

  private val requestData = TriggerBsasRequestData(
    nino,
    triggerBsasRequestDataBody()
  )

  private val requestForProperty = TriggerBsasRequestData(
    nino,
    triggerBsasRequestDataBody(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`)
  )

  val testHateoasLinkSE: Link = Link(
    href = s"/individuals/self-assessment/adjustable-summary/$nino/self-employment/c75f40a6-a3df-4429-a697-471eeec46435",
    method = GET,
    rel = "self"
  )

  val testHateoasLinkProperty: Link = Link(
    href = s"/individuals/self-assessment/adjustable-summary/$nino/uk-property/c75f40a6-a3df-4429-a697-471eeec46435",
    method = GET,
    rel = "self"
  )

  "triggerBsas" should {
    "return OK" when {
      "a valid request is supplied for business type self-employment" in new Test {
        private val mtdResponseJson = Json.parse(hateoasResponseForSE(nino.nino))

        willUseValidator(returningSuccess(requestData))

        MockTriggerBsasService
          .triggerBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseObj))))

        MockHateoasFactory
          .wrap(
            responseObj,
            TriggerBsasHateoasData(nino.nino, TypeOfBusiness.`self-employment`, responseObj.calculationId, Some(TaxYear.fromMtd("2020-21"))))
          .returns(HateoasWrapper(responseObj, Seq(testHateoasLinkSE)))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseJson),
          maybeAuditRequestBody = Some(requestBody),
          maybeAuditResponseBody = Some(mtdResponseJson)
        )
      }

      "a valid request is supplied for business type uk-property" in new Test {
        override protected val requestBodyForController: JsValue = requestBodyForProperty

        private val mtdResponseJson = Json.parse(hateoasResponseForProperty(nino.nino))

        willUseValidator(returningSuccess(requestForProperty))

        MockTriggerBsasService
          .triggerBsas(requestForProperty)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseObj))))

        MockHateoasFactory
          .wrap(
            responseObj,
            TriggerBsasHateoasData(nino.nino, TypeOfBusiness.`uk-property-fhl`, responseObj.calculationId, Some(TaxYear.fromMtd("2020-21"))))
          .returns(HateoasWrapper(responseObj, Seq(testHateoasLinkProperty)))

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
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected val requestBodyForController: JsValue = requestBody

    protected def callController(): Future[Result] =
      controller.triggerBsas(nino.nino)(fakePostRequest(requestBodyForController))

    protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "TriggerBusinessSourceAdjustableSummary",
        transactionName = "trigger-business-source-adjustable-summary",
        detail = GenericAuditDetail(
          versionNumber = "3.0",
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> nino.nino),
          requestBody = maybeRequestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
