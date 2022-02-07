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
import v3.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._
import v3.mocks.connectors.MockSubmitUkPropertyBsasConnector
import v3.models.domain.TypeOfBusiness
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.submitBsas.ukProperty.SubmitUkPropertyBsasRequestData
import v3.models.response.SubmitUkPropertyBsasResponse

import scala.concurrent.Future

class SubmitUKPropertyBsasServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")
  val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val request: SubmitUkPropertyBsasRequestData = SubmitUkPropertyBsasRequestData(nino, id, fhlBody)

  val response: SubmitUkPropertyBsasResponse = SubmitUkPropertyBsasResponse(id, TypeOfBusiness.`uk-property-fhl`)

  trait Test extends MockSubmitUkPropertyBsasConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "submitUKPropertyBsas")

    val service = new SubmitUkPropertyBsasService(mockConnector)
  }

  "submitUKPropertyBsas" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockSubmitUKPropertyBsasConnector.submitUKPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.submitPropertyBsas(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }

    "return error response" when {


      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockSubmitUKPropertyBsasConnector.submitUKPropertyBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.submitPropertyBsas(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(

        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", CalculationIdFormatError),
        ("INVALID_PAYLOAD", DownstreamError),
        ("ASC_ID_INVALID", RuleSummaryStatusInvalid),
        ("ASC_ALREADY_SUPERSEDED", RuleSummaryStatusSuperseded),
        ("ASC_ALREADY_ADJUSTED", RuleAlreadyAdjusted),
        ("UNALLOWABLE_VALUE", RuleResultingValueNotPermitted),
        ("INCOMESOURCE_TYPE_NOT_MATCHED", RuleTypeOfBusinessIncorrectError),
        ("BVR_FAILURE_C55316", DownstreamError),
        ("BVR_FAILURE_C15320", DownstreamError),
        ("BVR_FAILURE_C55503", RuleOverConsolidatedExpensesThreshold),
        ("BVR_FAILURE_C55508", RulePropertyIncomeAllowanceClaimed),
        ("BVR_FAILURE_C55509", RulePropertyIncomeAllowanceClaimed),
        ("NO_DATA_FOUND", NotFoundError),
        ("INVALID_CORRELATIONID", DownstreamError),
        ("SERVER_ERROR", DownstreamError),
        ("SERVICE_UNAVAILABLE", DownstreamError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
    }

}
