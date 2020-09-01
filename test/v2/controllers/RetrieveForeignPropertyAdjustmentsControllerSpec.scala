/*
 * Copyright 2020 HM Revenue & Customs
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

package v2.controllers

import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v2.fixtures.foreignProperty.RetrieveForeignPropertyAdjustmentsFixtures._
import v1.models.outcomes.ResponseWrapper
import v2.mocks.hateoas.MockHateoasFactory
import v2.mocks.requestParsers.MockRetrieveAdjustmentsRequestParser
import v2.mocks.services._
import v2.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import v2.models.hateoas.{HateoasWrapper, Link}
import v2.models.hateoas.Method.GET
import v2.models.request.{RetrieveAdjustmentsRawData, RetrieveAdjustmentsRequestData}
import v2.models.response.retrieveBsasAdjustments.foreignProperty.{RetrieveForeignPropertyAdjustmentsHateoasData, RetrieveForeignPropertyAdjustmentsResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveForeignPropertyAdjustmentsControllerSpec extends ControllerBaseSpec
  with MockEnrolmentsAuthService
  with MockMtdIdLookupService
  with MockRetrieveAdjustmentsRequestParser
  with MockRetrieveForeignPropertyAdjustmentsService
  with MockHateoasFactory
  with MockAuditService  {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new RetrieveForeignPropertyAdjustmentsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockRequestParser,
      service = mockService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful((Right("test-mtd-id"))))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  private val nino = "AA123456A"
  private val correlationId = "X-123"
  private val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val request = RetrieveAdjustmentsRequestData(Nino(nino), bsasId)
  private val requestRawData = RetrieveAdjustmentsRawData(nino, bsasId)

  val testHateoasLinkRetrieveBsas = Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId?adjustedStatus=true",
    method = GET, rel = "retrieve-adjustable-summary")

  val testHateoasLinkAdjustSelf = Link(href = s"/individuals/self-assessment/adjustable-summary/$nino/foreign-property/$bsasId/adjust",
    method = GET, rel = "self")

  def event(auditResponse: AuditResponse): AuditEvent[GenericAuditDetail] =
    AuditEvent(
      auditType = "retrieveBusinessSourceAccountingAdjustments",
      transactionName = "retrieve-a-foreign-property-business-accounting-adjustments",
      detail = GenericAuditDetail(
        userType = "Individual",
        agentReferenceNumber = None,
        params = Map("nino" -> nino, "bsasId" -> bsasId),
        requestBody = None,
        `X-CorrelationId` = correlationId,
        auditResponse = auditResponse
      )
    )

  "retrieve" should {
    "return successful hateoas response for self-assessment with status OK" when {
      "a valid request is supplied" in new Test {

        MockRetrieveAdjustmentsRequestParser
          .parse(requestRawData)
          .returns(Right(request))

        MockRetrieveForeignPropertyAdjustmentsService
          .retrieveAdjustments(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, foreignPropertyRetrieveForeignPropertyAdjustmentResponseModel))))

        MockHateoasFactory
          .wrap(foreignPropertyRetrieveForeignPropertyAdjustmentResponseModel, RetrieveForeignPropertyAdjustmentsHateoasData(nino, bsasId))
          .returns(HateoasWrapper(foreignPropertyRetrieveForeignPropertyAdjustmentResponseModel, Seq(testHateoasLinkRetrieveBsas, testHateoasLinkAdjustSelf))
          )

        val result: Future[Result] = controller.retrieve(nino, bsasId)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.parse(hateoasResponseForForeignPropertyAdjustments(nino, bsasId))
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse = AuditResponse(OK, None, Some(Json.parse(hateoasResponseForForeignPropertyAdjustments(nino, bsasId))))
        MockedAuditService.verifyAuditEvent(event(auditResponse)).once
      }
    }
  }
}
