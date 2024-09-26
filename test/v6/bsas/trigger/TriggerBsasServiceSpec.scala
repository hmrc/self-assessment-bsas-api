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

import common.errors._
import shared.controllers.EndpointLogContext
import shared.models.domain.Nino
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v6.bsas.trigger.def1.model.Def1_TriggerBsasFixtures._
import v6.bsas.trigger.def1.model.request.Def1_TriggerBsasRequestData
import v6.bsas.trigger.def1.model.response.Def1_TriggerBsasResponse
import v6.bsas.trigger.model.TriggerBsasRequestData

import scala.concurrent.Future

class TriggerBsasServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")

  val calculationId                      = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val request: TriggerBsasRequestData    = Def1_TriggerBsasRequestData(nino, triggerBsasRequestBody)
  val response: Def1_TriggerBsasResponse = Def1_TriggerBsasResponse(calculationId)

  trait Test extends MockTriggerBsasConnector {
    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "triggerBsas")

    val service = new TriggerBsasService(mockConnector)
  }

  "triggerBsas" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockTriggerBsasConnector
          .triggerBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.triggerBsas(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }

    "return error response" when {

      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockTriggerBsasConnector
            .triggerBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.triggerBsas(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("ACCOUNTING_PERIOD_NOT_ENDED", RuleAccountingPeriodNotEndedError),
        ("OBLIGATIONS_NOT_MET", RuleObligationsNotMet),
        ("NO_ACCOUNTING_PERIOD", RuleNoAccountingPeriodError),
        ("NO_DATA_FOUND", TriggerNotFoundError),
        ("INVALID_PAYLOAD", InternalError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError),
        ("INVALID_CORRELATIONID", InternalError)
      )

      val extraTysErrors = List(
        "INVALID_CORRELATION_ID" -> InternalError,
        "INVALID_TAX_YEAR"       -> InternalError,
        "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
      )

      (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
    }
  }

}
