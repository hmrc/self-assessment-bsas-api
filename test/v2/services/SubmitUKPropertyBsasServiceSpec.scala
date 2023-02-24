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

import api.models.errors._
import api.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import api.controllers.EndpointLogContext
import v2.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._
import v2.mocks.connectors.MockSubmitUkPropertyBsasConnector
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import api.models.ResponseWrapper
import api.models.domain.Nino
import v2.models.request.submitBsas.ukProperty.SubmitUkPropertyBsasRequestData
import v2.models.response.SubmitUkPropertyBsasResponse

import scala.concurrent.Future

class SubmitUKPropertyBsasServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")
  val id           = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val request: SubmitUkPropertyBsasRequestData = SubmitUkPropertyBsasRequestData(nino, id, fhlBody)

  val response: SubmitUkPropertyBsasResponse = SubmitUkPropertyBsasResponse(id, TypeOfBusiness.`uk-property-fhl`)

  trait Test extends MockSubmitUkPropertyBsasConnector {
    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "submitUKPropertyBsas")

    val service = new SubmitUkPropertyBsasService(mockConnector)
  }

  "submitUKPropertyBsas" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockSubmitUKPropertyBsasConnector
          .submitUKPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.submitPropertyBsas(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }

    "return error response" when {

      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"$downstreamErrorCode is returned from the service" in new Test {

          MockSubmitUKPropertyBsasConnector
            .submitUKPropertyBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.submitPropertyBsas(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", BsasIdFormatError),
        ("INVALID_PAYLOAD", InternalError),
        ("ASC_ID_INVALID", RuleSummaryStatusInvalid),
        ("ASC_ALREADY_SUPERSEDED", RuleSummaryStatusSuperseded),
        ("ASC_ALREADY_ADJUSTED", RuleBsasAlreadyAdjusted),
        ("UNALLOWABLE_VALUE", RuleResultingValueNotPermitted),
        ("INCOMESOURCE_TYPE_NOT_MATCHED", RuleTypeOfBusinessError),
        ("BVR_FAILURE_C55316", InternalError),
        ("BVR_FAILURE_C15320", InternalError),
        ("BVR_FAILURE_C55503", RuleOverConsolidatedExpensesThreshold),
        ("BVR_FAILURE_C55508", RulePropertyIncomeAllowanceClaimed),
        ("BVR_FAILURE_C55509", RulePropertyIncomeAllowanceClaimed),
        ("NO_DATA_FOUND", NotFoundError),
        ("INVALID_CORRELATIONID", InternalError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }

}
