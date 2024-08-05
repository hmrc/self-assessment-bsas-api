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

package v6.ukPropertyBsas.submit

import common.errors._
import shared.controllers.EndpointLogContext
import shared.models.domain.{CalculationId, Nino}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v6.ukPropertyBsas.submit.def3.model.request.Def3_SubmitUkPropertyBsasRequestData
import v6.ukPropertyBsas.submit.def3.model.request.SubmitUKPropertyBsasRequestBodyFixtures._
import v6.ukPropertyBsas.submit.model.request.SubmitUkPropertyBsasRequestData

import scala.concurrent.Future

class SubmitUKPropertyBsasServiceSpec extends ServiceSpec {

  private val nino          = Nino("AA123456A")
  private val calculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")

  val requestData: SubmitUkPropertyBsasRequestData = Def3_SubmitUkPropertyBsasRequestData(
    nino = nino,
    calculationId = calculationId,
    taxYear = None,
    body = fhlBody
  )

  trait Test extends MockSubmitUkPropertyBsasConnector {

    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "submitUKPropertyBsas")

    val service = new SubmitUkPropertyBsasService(connector = mockConnector)
  }

  "submitUKPropertyBsas" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockSubmitUKPropertyBsasConnector
          .submitUKPropertyBsas(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        await(service.submitPropertyBsas(requestData)) shouldBe Right(ResponseWrapper(correlationId, ()))
      }
    }

    "return error response" when {

      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockSubmitUKPropertyBsasConnector
            .submitUKPropertyBsas(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.submitPropertyBsas(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        "INVALID_TAXABLE_ENTITY_ID"     -> NinoFormatError,
        "INVALID_CALCULATION_ID"        -> CalculationIdFormatError,
        "INVALID_CORRELATIONID"         -> InternalError,
        "INVALID_PAYLOAD"               -> InternalError,
        "BVR_FAILURE_C55316"            -> InternalError,
        "BVR_FAILURE_C15320"            -> InternalError,
        "BVR_FAILURE_C55508"            -> RulePropertyIncomeAllowanceClaimed,
        "BVR_FAILURE_C55509"            -> RulePropertyIncomeAllowanceClaimed,
        "BVR_FAILURE_C55503"            -> RuleOverConsolidatedExpensesThreshold,
        "BVR_FAILURE_C559107"           -> InternalError,
        "BVR_FAILURE_C559103"           -> InternalError,
        "BVR_FAILURE_C559099"           -> InternalError,
        "NO_DATA_FOUND"                 -> NotFoundError,
        "ASC_ALREADY_SUPERSEDED"        -> RuleSummaryStatusSuperseded,
        "ASC_ALREADY_ADJUSTED"          -> RuleAlreadyAdjusted,
        "UNALLOWABLE_VALUE"             -> RuleResultingValueNotPermitted,
        "ASC_ID_INVALID"                -> RuleSummaryStatusInvalid,
        "INCOMESOURCE_TYPE_NOT_MATCHED" -> RuleTypeOfBusinessIncorrectError,
        "SERVER_ERROR"                  -> InternalError,
        "SERVICE_UNAVAILABLE"           -> InternalError
      )

      val extraTysErrors = List(
        "INVALID_TAX_YEAR"               -> TaxYearFormatError,
        "NOT_FOUND"                      -> NotFoundError,
        "TAX_YEAR_NOT_SUPPORTED"         -> RuleTaxYearNotSupportedError,
        "INCOME_SOURCE_TYPE_NOT_MATCHED" -> RuleTypeOfBusinessIncorrectError
      )

      (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
    }
  }

}
