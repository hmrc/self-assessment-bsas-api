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
import api.models.domain.{CalculationId, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.MockAuditService
import mocks.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import routing.Version3
import v3.controllers.validators.MockSubmitUkPropertyBsasValidatorFactory
import v3.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._
import v3.mocks.services._
import v3.models.errors._
import v3.models.request.submitBsas.ukProperty.SubmitUkPropertyBsasRequestData
import v3.models.response.SubmitUkPropertyBsasHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitUkPropertyBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitUkPropertyBsasValidatorFactory
    with MockSubmitUkPropertyBsasService
    with MockSubmitUKPropertyBsasNrsProxyService
    with MockHateoasFactory
    with MockIdGenerator
    with MockAuditService
    with MockAppConfig {

  private val calculationId = CalculationId("c75f40a6-a3df-4429-a697-471eeec46435")
  private val rawTaxYear    = "2023-24"
  private val taxYear       = TaxYear.fromMtd(rawTaxYear)

  private val requestData = SubmitUkPropertyBsasRequestData(
    nino = nino,
    calculationId = calculationId,
    body = nonFHLBody,
    taxYear = Some(taxYear)
  )

  val testHateoasLinks: Seq[Link] = Seq(
    Link(
      href = s"/individuals/self-assessment/adjustable-summary/$nino/uk-property/$calculationId",
      method = GET,
      rel = "self"
    )
  )

  private val mtdResponseJson = Json.parse(hateoasResponse(nino.nino, calculationId.calculationId))

  "submitUkPropertyBsas" should {

    "return OK" when {
      "the request is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockSubmitUKPropertyBsasNrsProxyService
          .submit(nino.nino)
          .returns(Future.successful(()))

        MockSubmitUkPropertyBsasService
          .submitPropertyBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), SubmitUkPropertyBsasHateoasData(nino.nino, calculationId.calculationId, Some(taxYear)))
          .returns(HateoasWrapper((), testHateoasLinks))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseJson),
          maybeAuditRequestBody = Some(validNonFHLInputJson),
          maybeAuditResponseBody = Some(mtdResponseJson)
        )
      }
    }

    "return parser errors as per spec" when {

      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))
        runErrorTestWithAudit(NinoFormatError, maybeAuditRequestBody = Some(validNonFHLInputJson))
      }

      "multiple parser errors occur" in new Test {
        private val errors = List(NinoFormatError, CalculationIdFormatError)
        willUseValidator(returningErrors(errors))
        runMultipleErrorsTestWithAudit(errors, maybeAuditRequestBody = Some(validNonFHLInputJson))
      }
    }

    "the service returns an error" in new Test {
      willUseValidator(returningSuccess(requestData))

      MockSubmitUKPropertyBsasNrsProxyService
        .submit(nino.nino)
        .returns(Future.successful(()))

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
      nrsService = mockSubmitUKPropertyBsasNrsProxyService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] =
      controller.handleRequest(nino.nino, calculationId.calculationId, Some(rawTaxYear))(fakePostRequest(validNonFHLInputJson))

    protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "SubmitUKPropertyAccountingAdjustments",
        transactionName = "submit-uk-property-accounting-adjustments",
        detail = GenericAuditDetail(
          versionNumber = "3.0",
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> nino.nino, "calculationId" -> calculationId.calculationId),
          requestBody = maybeRequestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

    MockedAppConfig.isApiDeprecated(Version3) returns false
  }

}
