/*
 * Copyright 2021 HM Revenue & Customs
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

import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.controllers.EndpointLogContext
import v1.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._
import v1.mocks.connectors.MockSubmitUkPropertyBsasConnector
import v1.models.domain.TypeOfBusiness
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.submitBsas.SubmitUkPropertyBsasRequestData
import v1.models.response.SubmitUkPropertyBsasResponse

import scala.concurrent.Future

class SubmitUKPropertyBsasServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")
  val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val request = SubmitUkPropertyBsasRequestData(nino, id, fhlBody)

  val response = SubmitUkPropertyBsasResponse(id, TypeOfBusiness.`uk-property-fhl`)

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

    "return a valid response for v1r5" when {
      "a valid request is supplied" in new Test {
        MockSubmitUKPropertyBsasConnector.submitUKPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.submitPropertyBsasV1R5(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }

    "return error response" when {

      "des return success response with invalid type of business as `self-employment`" in new Test {

        MockSubmitUKPropertyBsasConnector.submitUKPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response.copy(typeOfBusiness = TypeOfBusiness.`self-employment`)))))

        await(service.submitPropertyBsas(request)) shouldBe Left(ErrorWrapper(correlationId, RuleSelfEmploymentAdjustedError))
      }

      "des return success response with invalid type of business as `uk-property-non-fhl` where fhl is expected" in new Test {

        MockSubmitUKPropertyBsasConnector.submitUKPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response.copy(typeOfBusiness = TypeOfBusiness.`uk-property-non-fhl`)))))

        await(service.submitPropertyBsas(request)) shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectPropertyAdjusted))
      }

      "des return success response with invalid type of business as `uk-property-fhl` where non-fhl is expected" in new Test {

        MockSubmitUKPropertyBsasConnector.submitUKPropertyBsas(request.copy(body = nonFHLBody))
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response.copy(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`)))))

        await(service.submitPropertyBsas(request.copy(body = nonFHLBody))) shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectPropertyAdjusted))
      }

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockSubmitUKPropertyBsasConnector.submitUKPropertyBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.submitPropertyBsas(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(

        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", BsasIdFormatError),
        ("INVALID_PAYLOAD", DownstreamError),
        ("INVALID_PAYLOAD_REMOTE", DownstreamError),
        ("INVALID_FIELD", RuleTypeOfBusinessError),
        ("INVALID_MONETARY_FORMAT", DownstreamError),
        ("ASC_ID_INVALID", RuleSummaryStatusInvalid),
        ("ASC_ALREADY_SUPERSEDED", RuleSummaryStatusSuperseded),
        ("ASC_ALREADY_ADJUSTED", RuleBsasAlreadyAdjusted),
        ("UNALLOWABLE_VALUE", RuleResultingValueNotPermitted),
        ("BVR_FAILURE_C55316", RuleTypeOfBusinessError),
        ("BVR_FAILURE_C15320", RuleTypeOfBusinessError),
        ("BVR_FAILURE_C55503", RuleOverConsolidatedExpensesThreshold),
        ("BVR_FAILURE_C55508", RulePropertyIncomeAllowanceClaimed),
        ("BVR_FAILURE_C55509", RulePropertyIncomeAllowanceClaimed),
        ("NOT_FOUND", NotFoundError),
        ("SERVER_ERROR", DownstreamError),
        ("SERVICE_UNAVAILABLE", DownstreamError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }

    "return error response for v1r5" when {

      "des return success response with invalid type of business as `self-employment`" in new Test {

        MockSubmitUKPropertyBsasConnector.submitUKPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response.copy(typeOfBusiness = TypeOfBusiness.`self-employment`)))))

        await(service.submitPropertyBsasV1R5(request)) shouldBe Left(ErrorWrapper(correlationId, RuleSelfEmploymentAdjustedError))
      }

      "des return success response with invalid type of business as `uk-property-non-fhl` where fhl is expected" in new Test {

        MockSubmitUKPropertyBsasConnector.submitUKPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response.copy(typeOfBusiness = TypeOfBusiness.`uk-property-non-fhl`)))))

        await(service.submitPropertyBsasV1R5(request)) shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectPropertyAdjusted))
      }

      "des return success response with invalid type of business as `uk-property-fhl` where non-fhl is expected" in new Test {

        MockSubmitUKPropertyBsasConnector.submitUKPropertyBsas(request.copy(body = nonFHLBody))
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response.copy(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`)))))

        await(service.submitPropertyBsasV1R5(request.copy(body = nonFHLBody))) shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectPropertyAdjusted))
      }

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockSubmitUKPropertyBsasConnector.submitUKPropertyBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.submitPropertyBsasV1R5(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(

        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", BsasIdFormatError),
        ("INVALID_PAYLOAD", DownstreamError),
        ("ASC_ID_INVALID", RuleSummaryStatusInvalid),
        ("ASC_ALREADY_SUPERSEDED", RuleSummaryStatusSuperseded),
        ("ASC_ALREADY_ADJUSTED", RuleBsasAlreadyAdjusted),
        ("UNALLOWABLE_VALUE", RuleResultingValueNotPermitted),
        ("INCOMESOURCE_TYPE_NOT_MATCHED", RuleTypeOfBusinessError),
        ("BVR_FAILURE_C55316", RuleTypeOfBusinessError),
        ("BVR_FAILURE_C15320", RuleTypeOfBusinessError),
        ("BVR_FAILURE_C55503", RuleOverConsolidatedExpensesThreshold),
        ("BVR_FAILURE_C55508", RulePropertyIncomeAllowanceClaimed),
        ("BVR_FAILURE_C55509", RulePropertyIncomeAllowanceClaimed),
        ("NO_DATA_FOUND", NotFoundError),
        ("SERVER_ERROR", DownstreamError),
        ("SERVICE_UNAVAILABLE", DownstreamError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}
