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

package v2.services

import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v2.controllers.EndpointLogContext
import v2.mocks.connectors.MockSubmitForeignPropertyBsasConnector
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.submitBsas.foreignProperty._
import v2.models.response.SubmitForeignPropertyBsasResponse

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SubmitForeignPropertyBsasServiceSpec extends UnitSpec {

  private val nino = Nino("AA123456A")
  private val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val correlationId = "X-123"

  private val fhlEeaBody =
    SubmitForeignPropertyBsasRequestBody(
      None,
      Some(FhlEea(
        Some(Income(
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12)
        )),
        Some(Expenses(
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12)
        ))
      ))
    )

  private val foreignPropertyBody =
    SubmitForeignPropertyBsasRequestBody(
      Some(ForeignProperty(
        Some(Income(
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12)
        )),
        Some(Expenses(
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12)
        ))
      )),
      None
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

      "des return success response with invalid type of business as `self-employment`" in new Test {

        MockSubmitForeignPropertyBsasConnector.submitForeignPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response.copy(typeOfBusiness = TypeOfBusiness.`self-employment`)))))

        await(service.submitForeignPropertyBsas(request)) shouldBe Left(ErrorWrapper(Some(correlationId), RuleSelfEmploymentAdjustedError))
      }

      "des return success response with invalid type of business as `uk-property-fhl`" in new Test {

        MockSubmitForeignPropertyBsasConnector.submitForeignPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response.copy(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`)))))

        await(service.submitForeignPropertyBsas(request)) shouldBe Left(ErrorWrapper(Some(correlationId), RuleSelfEmploymentAdjustedError))
      }

      "des return success response with invalid type of business as `uk-property-non-fhl`" in new Test {

        MockSubmitForeignPropertyBsasConnector.submitForeignPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response.copy(typeOfBusiness = TypeOfBusiness.`uk-property-non-fhl`)))))

        await(service.submitForeignPropertyBsas(request)) shouldBe Left(ErrorWrapper(Some(correlationId), RuleSelfEmploymentAdjustedError))
      }

      "des return success response with invalid type of business as `foreign-property` where foreign-property-fhl-eea is expected" in new Test {

        MockSubmitForeignPropertyBsasConnector.submitForeignPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response.copy(typeOfBusiness = TypeOfBusiness.`foreign-property`)))))

        await(service.submitForeignPropertyBsas(request)) shouldBe Left(ErrorWrapper(Some(correlationId), RuleIncorrectPropertyAdjusted))
      }

      "des return success response with invalid type of business as `foreign-property-fhl-eea` where foreign-property is expected" in new Test {

        MockSubmitForeignPropertyBsasConnector.submitForeignPropertyBsas(request.copy(body = foreignPropertyBody))
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response.copy(typeOfBusiness = TypeOfBusiness.`foreign-property-fhl-eea`)))))

        await(service.submitForeignPropertyBsas(request.copy(body = foreignPropertyBody))) shouldBe Left(ErrorWrapper(Some(correlationId), RuleIncorrectPropertyAdjusted))
      }

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockSubmitForeignPropertyBsasConnector.submitForeignPropertyBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.submitForeignPropertyBsas(request)) shouldBe Left(ErrorWrapper(Some(correlationId), error))
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
  }

}

