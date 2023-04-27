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
import api.mocks.services.{ MockEnrolmentsAuthService, MockMtdIdLookupService }
import api.models.audit.{ AuditEvent, AuditResponse, GenericAuditDetail }
import api.models.domain.{ Nino, TaxYear }
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.MockAuditService
import mocks.MockAppConfig
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.Result
import routing.Version3
import v3.mocks.requestParsers.MockSubmitForeignPropertyBsasRequestParser
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
    with MockSubmitForeignPropertyBsasService
    with MockSubmitForeignPropertyBsasRequestParser
    with MockSubmitForeignPropertyBsasNrsProxyService
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator
    with MockAppConfig {

  private val version = Version3

  private val bsasId     = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val rawTaxYear = "2023-24"
  private val taxYear    = TaxYear.fromMtd(rawTaxYear)

  private val testHateoasLink =
    Link(href = s"individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId/adjust", method = GET, rel = "self")

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
    SubmitForeignPropertyBsasRequestBody(Some(Seq(foreignProperty)), None)

  private val rawData     = SubmitForeignPropertyRawData(nino, bsasId, Some(rawTaxYear), requestJson)
  private val requestData = SubmitForeignPropertyBsasRequestData(Nino(nino), bsasId, Some(taxYear), requestBody)

  val mtdResponseJson: JsValue = Json.parse(s"""
       |{
       |  "links":[
       |    {
       |      "href":"individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId/adjust",
       |      "rel":"self",
       |      "method":"GET"
       |    }
       |  ]
       |}
       |""".stripMargin)

  "handleRequest" should {
    "return OK" when {
      "the request is valid" in new Test {
        MockSubmitForeignPropertyBsasRequestParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockSubmitForeignPropertyBsasNrsProxyService
          .submit(nino)
          .returns(Future.successful(Unit))

        MockSubmitForeignPropertyBsasService
          .submitForeignPropertyBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), SubmitForeignPropertyBsasHateoasData(nino, bsasId, requestData.taxYear))
          .returns(HateoasWrapper((), Seq(testHateoasLink)))

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
        MockSubmitForeignPropertyBsasRequestParser
          .parseRequest(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError, None)))

        runErrorTestWithAudit(NinoFormatError, maybeAuditRequestBody = Some(requestJson))
      }

      "the service returns an error" in new Test {
        MockSubmitForeignPropertyBsasRequestParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockSubmitForeignPropertyBsasNrsProxyService
          .submit(nino)
          .returns(Future.successful(Unit))

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
      parser = mockRequestParser,
      service = mockService,
      nrsService = mockSubmitForeignPropertyBsasNrsProxyService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    override protected def callController(): Future[Result] =
      controller.handleRequest(nino, bsasId, Some(rawTaxYear))(fakePostRequest(requestJson))

    override protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "SubmitForeignPropertyAccountingAdjustments",
        transactionName = "submit-foreign-property-accounting-adjustments",
        detail = GenericAuditDetail(
          versionNumber = "3.0",
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> nino, "calculationId" -> bsasId),
          requestBody = maybeRequestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

    MockedAppConfig.isApiDeprecated(version) returns false
  }
}
