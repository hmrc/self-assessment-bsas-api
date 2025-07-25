/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.foreignPropertyBsas.submit

import common.errors.*
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.config.MockSharedAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{CalculationId, TaxYear}
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v7.foreignPropertyBsas.submit.def3.model.request.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitForeignPropertyBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitForeignPropertyBsasValidatorFactory
    with MockSubmitForeignPropertyBsasService
    with MockAuditService
    with MockIdGenerator
    with MockSharedAppConfig {

  private val calculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")
  private val rawTaxYear    = "2025-26"
  private val taxYear       = TaxYear.fromMtd(rawTaxYear)

  private val requestJson = Json.parse(
    """
      |{
      |    "foreignProperty": {
      |        "countryLevelDetail": [
      |            {
      |                "countryCode": "FRA",
      |                "income": {
      |                    "totalRentsReceived": 123.12,
      |                    "premiumsOfLeaseGrant": 123.12,
      |                    "otherPropertyIncome": 123.12
      |                },
      |                "expenses": {
      |                    "premisesRunningCosts": 123.12,
      |                    "repairsAndMaintenance": 123.12,
      |                    "financialCosts": 123.12,
      |                    "professionalFees": 123.12,
      |                    "travelCosts": 123.12,
      |                    "costOfServices": 123.12,
      |                    "residentialFinancialCost": 123.12,
      |                    "other": 123.12
      |                }
      |            }
      |        ]
      |    }
      |}
    """.stripMargin
  )

  private val foreignProperty: ForeignProperty =
    ForeignProperty(
      countryLevelDetail = Some(
        Seq(
          CountryLevelDetail(
            countryCode = "FRA",
            income = Some(
              ForeignPropertyIncome(
                totalRentsReceived = Some(123.12),
                premiumsOfLeaseGrant = Some(123.12),
                otherPropertyIncome = Some(123.12)
              )
            ),
            expenses = Some(
              ForeignPropertyExpenses(
                premisesRunningCosts = Some(123.12),
                repairsAndMaintenance = Some(123.12),
                financialCosts = Some(123.12),
                professionalFees = Some(123.12),
                costOfServices = Some(123.12),
                residentialFinancialCost = Some(123.12),
                other = Some(123.12),
                travelCosts = Some(123.12),
                consolidatedExpenses = None
              )
            )
          )
        )
      ),
      zeroAdjustments = None
    )

  val requestBody: Def3_SubmitForeignPropertyBsasRequestBody =
    Def3_SubmitForeignPropertyBsasRequestBody(Some(foreignProperty))

  private val requestData =
    Def3_SubmitForeignPropertyBsasRequestData(parsedNino, calculationId, taxYear, requestBody)

  "handleRequest" should {
    "return OK" when {
      "the request is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockSubmitForeignPropertyBsasService
          .submitForeignPropertyBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = None,
          maybeAuditRequestBody = Some(requestJson),
          maybeAuditResponseBody = None
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))
        runErrorTestWithAudit(NinoFormatError, maybeAuditRequestBody = Some(requestJson))
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockSubmitForeignPropertyBsasService
          .submitForeignPropertyBsas(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleSummaryStatusSuperseded))))

        runErrorTestWithAudit(RuleSummaryStatusSuperseded, maybeAuditRequestBody = Some(requestJson))
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller: SubmitForeignPropertyBsasController = new SubmitForeignPropertyBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockSubmitForeignPropertyBsasValidatorFactory,
      service = mockService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    override protected def callController(): Future[Result] =
      controller.handleRequest(validNino, calculationId.calculationId, rawTaxYear)(fakePostRequest(requestJson))

    override protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "SubmitForeignPropertyAccountingAdjustments",
        transactionName = "submit-foreign-property-accounting-adjustments",
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
