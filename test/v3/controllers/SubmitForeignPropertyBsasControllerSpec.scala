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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.config.MockAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.Method.GET
import shared.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{CalculationId, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v3.controllers.validators.MockSubmitForeignPropertyBsasValidatorFactory
import v3.mocks.services._
import v3.models.errors._
import v3.models.request.submitBsas.foreignProperty._
import v3.models.response.SubmitForeignPropertyBsasHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitForeignPropertyBsasControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitForeignPropertyBsasValidatorFactory
    with MockSubmitForeignPropertyBsasService
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator
    with MockAppConfig {

  private val calculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")
  private val rawTaxYear    = "2023-24"
  private val taxYear       = TaxYear.fromMtd(rawTaxYear)

  private val testHateoasLink =
    Link(href = s"individuals/self-assessment/adjustable-summary/$validNino/foreign-property/$calculationId/adjust", method = GET, rel = "self")

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
      Some(ForeignPropertyIncome(Some(123.12), Some(123.12), Some(123.12))),
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
    SubmitForeignPropertyBsasRequestBody(Some(List(foreignProperty)), None)

  private val requestData =
    SubmitForeignPropertyBsasRequestData(parsedNino, calculationId, Some(taxYear), requestBody)

  val mtdResponseJson: JsValue = Json.parse(s"""
       |{
       |  "links":[
       |    {
       |      "href":"individuals/self-assessment/adjustable-summary/$validNino/foreign-property/$calculationId/adjust",
       |      "rel":"self",
       |      "method":"GET"
       |    }
       |  ]
       |}
       |""".stripMargin)

  "handleRequest" should {
    "return OK" when {
      "the request is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockSubmitForeignPropertyBsasService
          .submitForeignPropertyBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), SubmitForeignPropertyBsasHateoasData(validNino, calculationId.calculationId, requestData.taxYear))
          .returns(HateoasWrapper((), List(testHateoasLink)))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseJson),
          maybeAuditRequestBody = Some(requestJson),
          maybeAuditResponseBody = Some(mtdResponseJson)
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

  private trait Test extends ControllerTest with AuditEventChecking {

    val controller = new SubmitForeignPropertyBsasController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockSubmitForeignPropertyBsasValidatorFactory,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    override protected def callController(): Future[Result] =
      controller.handleRequest(validNino, calculationId.calculationId, Some(rawTaxYear))(fakePostRequest(requestJson))

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
