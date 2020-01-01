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

package v1.services

import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.controllers.EndpointLogContext
import v1.fixtures.TriggerBsasRequestBodyFixtures._
import v1.mocks.connectors.MockTriggerBsasConnector
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.triggerBsas.TriggerBsasRequest
import v1.models.response.TriggerBsasResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TriggerBsasServiceSpec extends UnitSpec {

  private val nino = Nino("AA123456A")
  val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val correlationId = "X-123"

  val request = TriggerBsasRequest(nino, seBody)

  val response = TriggerBsasResponse(id)

  trait Test extends MockTriggerBsasConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "triggerBsas")

    val service = new TriggerBsasService(mockConnector)
  }

  "triggerBsas" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockTriggerBsasConnector.triggerBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.triggerBsas(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }

    "return error response" when {

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockTriggerBsasConnector.triggerBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.triggerBsas(request)) shouldBe Left(ErrorWrapper(Some(correlationId), error))
        }

      val input = Seq(

        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("ACCOUNTING_PERIOD_NOT_ENDED", RuleAccountingPeriodNotEndedError),
        ("OBLIGATIONS_NOT_MET", RulePeriodicDataIncompleteError),
        ("NO_ACCOUNTING_PERIOD", RuleNoAccountingPeriodError),
        ("NOT_FOUND", NotFoundError),
        ("INVALID_PAYLOAD", DownstreamError),
        ("SERVER_ERROR", DownstreamError),
        ("SERVICE_UNAVAILABLE", DownstreamError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}
