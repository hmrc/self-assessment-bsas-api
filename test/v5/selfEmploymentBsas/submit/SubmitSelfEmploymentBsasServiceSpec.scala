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

package v5.selfEmploymentBsas.submit

import common.errors.*
import shared.controllers.EndpointLogContext
import shared.models.domain.{CalculationId, Nino}
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v5.selfEmploymentBsas.submit.def1.model.request.Def1_SubmitSelfEmploymentBsasRequestData
import v5.selfEmploymentBsas.submit.def1.model.request.fixtures.SubmitSelfEmploymentBsasFixtures.submitSelfEmploymentBsasRequestBody
import v5.selfEmploymentBsas.submit.model.request.SubmitSelfEmploymentBsasRequestData

import scala.concurrent.Future

class SubmitSelfEmploymentBsasServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")
  private val id   = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")

  val request: SubmitSelfEmploymentBsasRequestData =
    Def1_SubmitSelfEmploymentBsasRequestData(nino, id, None, submitSelfEmploymentBsasRequestBody)

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
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        await(service.submitSelfEmploymentBsas(request)) shouldBe Right(ResponseWrapper(correlationId, ()))
      }
    }

    "return error response" when {

      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"$downstreamErrorCode is returned from the service" in new Test {

          MockSubmitSelfEmploymentBsasConnector
            .submitSelfEmploymentBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.submitSelfEmploymentBsas(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = List(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", CalculationIdFormatError),
        ("INVALID_PAYLOAD", InternalError),
        ("ASC_ID_INVALID", RuleSummaryStatusInvalid),
        ("ASC_ALREADY_SUPERSEDED", RuleSummaryStatusSuperseded),
        ("ASC_ALREADY_ADJUSTED", RuleAlreadyAdjusted),
        ("UNALLOWABLE_VALUE", RuleResultingValueNotPermitted),
        ("INCOMESOURCE_TYPE_NOT_MATCHED", RuleTypeOfBusinessIncorrectError),
        ("BVR_FAILURE_C55316", RuleOverConsolidatedExpensesThreshold),
        ("BVR_FAILURE_C15320", RuleTradingIncomeAllowanceClaimed),
        ("BVR_FAILURE_C55503", InternalError),
        ("BVR_FAILURE_C55508", InternalError),
        ("BVR_FAILURE_C55509", InternalError),
        ("BVR_FAILURE_C559107", InternalError),
        ("BVR_FAILURE_C559103", InternalError),
        ("BVR_FAILURE_C559099", InternalError),
        ("NO_DATA_FOUND", NotFoundError),
        ("INVALID_CORRELATIONID", InternalError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError)
      )

      val extraTysErrors = List(
        ("INCOME_SOURCE_TYPE_NOT_MATCHED", RuleTypeOfBusinessIncorrectError),
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("NOT_FOUND", NotFoundError),
        ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
      )

      (input ++ extraTysErrors).foreach(args => serviceError.tupled(args))
    }
  }

}
