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

package v6.foreignPropertyBsas.submit

import common.errors.*
import shared.controllers.EndpointLogContext
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v6.foreignPropertyBsas.submit.def3.model.request.*

import scala.concurrent.Future

class SubmitForeignPropertyBsasServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")
  private val id   = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")

  private val foreignPropertyBody =
    Def3_SubmitForeignPropertyBsasRequestBody(
      foreignProperty = Some(
        List(ForeignProperty(
          "FRA",
          Some(ForeignPropertyIncome(Some(123.12), Some(123.12), Some(123.12))),
          Some(
            ForeignPropertyExpenses(
              Some(123.12),
              Some(123.12),
              Some(123.12),
              Some(123.12),
              Some(123.12),
              Some(123.12),
              Some(123.12),
              Some(123.12),
              None
            ))
        )))
    )

  private val request = Def3_SubmitForeignPropertyBsasRequestData(nino, id, TaxYear.fromMtd("2023-24"), foreignPropertyBody)

  trait Test extends MockSubmitForeignPropertyBsasConnector {
    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "submitForeignPropertyBsas")

    val service = new SubmitForeignPropertyBsasService(mockConnector)
  }

  "submitForeignPropertyBsas" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockSubmitForeignPropertyBsasConnector
          .submitForeignPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        await(service.submitForeignPropertyBsas(request)) shouldBe Right(ResponseWrapper(correlationId, ()))
      }
    }

    "return error response" when {

      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockSubmitForeignPropertyBsasConnector
            .submitForeignPropertyBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.submitForeignPropertyBsas(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", CalculationIdFormatError),
        ("INVALID_CORRELATIONID", InternalError),
        ("INVALID_PAYLOAD", InternalError),
        ("BVR_FAILURE_C15320", InternalError),
        ("BVR_FAILURE_C55508", InternalError),
        ("BVR_FAILURE_C55509", InternalError),
        ("BVR_FAILURE_C559107", RulePropertyIncomeAllowanceClaimed),
        ("BVR_FAILURE_C559103", RulePropertyIncomeAllowanceClaimed),
        ("BVR_FAILURE_C559099", RuleOverConsolidatedExpensesThreshold),
        ("BVR_FAILURE_C55503", InternalError),
        ("BVR_FAILURE_C55316", InternalError),
        ("NO_DATA_FOUND", NotFoundError),
        ("ASC_ALREADY_SUPERSEDED", RuleSummaryStatusSuperseded),
        ("ASC_ALREADY_ADJUSTED", RuleAlreadyAdjusted),
        ("UNALLOWABLE_VALUE", RuleResultingValueNotPermitted),
        ("ASC_ID_INVALID", RuleSummaryStatusInvalid),
        ("INCOMESOURCE_TYPE_NOT_MATCHED", RuleTypeOfBusinessIncorrectError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError)
      )

      val extraTysErrors = List(
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("NOT_FOUND", NotFoundError),
        ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError),
        ("INCOME_SOURCE_TYPE_NOT_MATCHED", RuleTypeOfBusinessIncorrectError)
      )

      (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
    }
  }

}
