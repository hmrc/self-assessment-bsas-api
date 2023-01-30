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

package v2.services

import domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v2.controllers.EndpointLogContext
import v2.fixtures.TriggerBsasRequestBodyFixtures._
import v2.mocks.connectors.MockTriggerBsasConnector
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.triggerBsas.TriggerBsasRequest
import v2.models.response.TriggerBsasResponse

import scala.concurrent.Future

class TriggerBsasServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")
  val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val request = TriggerBsasRequest(nino, model)

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

          await(service.triggerBsas(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(

        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("ACCOUNTING_PERIOD_NOT_ENDED", RuleAccountingPeriodNotEndedError),
        ("OBLIGATIONS_NOT_MET", RulePeriodicDataIncompleteError),
        ("NO_ACCOUNTING_PERIOD", RuleNoAccountingPeriodError),
        ("NO_DATA_FOUND", NotFoundError),
        ("INVALID_PAYLOAD", DownstreamError),
        ("SERVER_ERROR", DownstreamError),
        ("SERVICE_UNAVAILABLE", DownstreamError),
        ("INCOME_SOURCEID_NOT_PROVIDED", DownstreamError),
        ("INVALID_CORRELATIONID", DownstreamError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}
