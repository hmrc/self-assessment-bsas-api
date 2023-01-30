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
import v2.mocks.connectors.MockSubmitForeignPropertyBsasConnector
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.submitBsas.foreignProperty._
import v2.models.response.SubmitForeignPropertyBsasResponse

import scala.concurrent.Future

class SubmitForeignPropertyBsasServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")
  private val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val fhlEeaBody =
    SubmitForeignPropertyBsasRequestBody(
      foreignProperty = None,
      foreignFhlEea = Some(FhlEea(
        Some(FhlIncome(
          Some(123.12)
        )),
        Some(FhlEeaExpenses(
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          consolidatedExpenses = None
        ))
      ))
    )

  private val request = SubmitForeignPropertyBsasRequestData(nino, id, fhlEeaBody)

  private val response = SubmitForeignPropertyBsasResponse(id, TypeOfBusiness.`foreign-property-fhl-eea`)

  trait Test extends MockSubmitForeignPropertyBsasConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "submitForeignPropertyBsas")

    val service = new SubmitForeignPropertyBsasService(mockConnector)
  }

  "submitForeignPropertyBsas" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockSubmitForeignPropertyBsasConnector.submitForeignPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.submitForeignPropertyBsas(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }

    "return error response" when {

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockSubmitForeignPropertyBsasConnector.submitForeignPropertyBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.submitForeignPropertyBsas(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(

        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", BsasIdFormatError),
        ("INVALID_PAYLOAD", DownstreamError),
        ("INCOMESOURCE_TYPE_NOT_MATCHED", RuleTypeOfBusinessError),
        ("ASC_ID_INVALID", RuleSummaryStatusInvalid),
        ("ASC_ALREADY_SUPERSEDED", RuleSummaryStatusSuperseded),
        ("ASC_ALREADY_ADJUSTED", RuleBsasAlreadyAdjusted),
        ("UNALLOWABLE_VALUE", RuleResultingValueNotPermitted),
        ("BVR_FAILURE_C55316", DownstreamError),
        ("BVR_FAILURE_C15320", DownstreamError),
        ("BVR_FAILURE_C55503", RuleOverConsolidatedExpensesThreshold),
        ("BVR_FAILURE_C55508", RulePropertyIncomeAllowanceClaimed),
        ("BVR_FAILURE_C55509", RulePropertyIncomeAllowanceClaimed),
        ("NO_DATA_FOUND", NotFoundError),
        ("SERVER_ERROR", DownstreamError),
        ("INVALID_CORRELATION_ID", DownstreamError),
        ("SERVICE_UNAVAILABLE", DownstreamError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }

}

