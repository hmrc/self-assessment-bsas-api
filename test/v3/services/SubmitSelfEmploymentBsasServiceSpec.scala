/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.services

import domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v3.controllers.EndpointLogContext
import v3.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._
import v3.mocks.connectors.MockSubmitSelfEmploymentBsasConnector
import v3.models.domain.TypeOfBusiness
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestData
import v3.models.response.SubmitSelfEmploymentBsasResponse

import scala.concurrent.Future

class SubmitSelfEmploymentBsasServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")
  val id           = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val request: SubmitSelfEmploymentBsasRequestData = SubmitSelfEmploymentBsasRequestData(nino, id, submitSelfEmploymentBsasRequestBodyModel)

  val response: SubmitSelfEmploymentBsasResponse = SubmitSelfEmploymentBsasResponse(id, TypeOfBusiness.`self-employment`)

  trait Test extends MockSubmitSelfEmploymentBsasConnector {
    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "submitSelfEmploymentBsas")

    val service = new SubmitSelfEmploymentBsasService(mockConnector)
  }

  "submitSelfEmploymentBsas" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockSubmitSelfEmploymentBsasConnector
          .submitSelfEmploymentBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.submitSelfEmploymentBsas(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }

    "return error response" when {

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockSubmitSelfEmploymentBsasConnector
            .submitSelfEmploymentBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.submitSelfEmploymentBsas(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", BsasIdFormatError),
        ("INVALID_PAYLOAD", DownstreamError),
        ("ASC_ID_INVALID", RuleSummaryStatusInvalid),
        ("ASC_ALREADY_SUPERSEDED", RuleSummaryStatusSuperseded),
        ("ASC_ALREADY_ADJUSTED", RuleBsasAlreadyAdjusted),
        ("UNALLOWABLE_VALUE", RuleResultingValueNotPermitted),
        ("INCOMESOURCE_TYPE_NOT_MATCHED", RuleNotSelfEmployment),
        ("BVR_FAILURE_C55316", RuleOverConsolidatedExpensesThreshold),
        ("BVR_FAILURE_C15320", RuleTradingIncomeAllowanceClaimed),
        ("BVR_FAILURE_C55503", DownstreamError),
        ("BVR_FAILURE_C55508", DownstreamError),
        ("BVR_FAILURE_C55509", DownstreamError),
        ("NO_DATA_FOUND", NotFoundError),
        ("INVALID_CORRELATIONID", DownstreamError),
        ("SERVER_ERROR", DownstreamError),
        ("SERVICE_UNAVAILABLE", DownstreamError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }

}