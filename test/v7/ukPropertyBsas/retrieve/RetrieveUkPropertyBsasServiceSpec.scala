/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.ukPropertyBsas.retrieve

import common.errors.RuleTypeOfBusinessIncorrectError
import shared.controllers.EndpointLogContext
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v7.ukPropertyBsas.retrieve.def1.model.request.Def1_RetrieveUkPropertyBsasRequestData
import v7.ukPropertyBsas.retrieve.def1.model.response.RetrieveUkPropertyBsasFixtures._
import v7.ukPropertyBsas.retrieve.model.request.RetrieveUkPropertyBsasRequestData
import v7.ukPropertyBsas.retrieve.model.response.RetrieveUkPropertyBsasResponse

import scala.concurrent.Future

class RetrieveUkPropertyBsasServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")
  private val id   = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")

  val request: RetrieveUkPropertyBsasRequestData = Def1_RetrieveUkPropertyBsasRequestData(nino, id, taxYear = TaxYear.fromMtd("2023-24"))

  trait Test extends MockRetrieveUkPropertyBsasConnector {
    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("RetrieveUkPropertyBsasConnector", "retrieve")

    val service = new RetrieveUkPropertyBsasService(mockConnector)
  }

  "retrieve" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockRetrievePropertyBsasConnector
          .retrievePropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveBsasResponseFhl))))

        await(service.retrieve(request)) shouldBe Right(ResponseWrapper(correlationId, retrieveBsasResponseFhl))
      }
    }

    "return error response" when {
      "downstream returns a success response with invalid IncomeSourceType" should {
        List("01", "03", "05").foreach(incomeSourceType =>
          s"return an error for $incomeSourceType" in new Test {
            val response: RetrieveUkPropertyBsasResponse = retrieveBsasResponseInvalidIncomeSourceType(incomeSourceType = incomeSourceType)

            MockRetrievePropertyBsasConnector
              .retrievePropertyBsas(request)
              .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

            await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, RuleTypeOfBusinessIncorrectError))
          })
      }
      "downstream returns a Tax Year different from that in the pre-TYS request" should {
        s"return an error matching resource not found" in new Test {
          val request: RetrieveUkPropertyBsasRequestData = Def1_RetrieveUkPropertyBsasRequestData(nino, id, taxYear = TaxYear.fromMtd("2019-20"))

          MockRetrievePropertyBsasConnector
            .retrievePropertyBsas(request)
            .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveBsasResponseUkProperty))))

          await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, NotFoundError))
        }
      }

      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"$downstreamErrorCode is returned from the service" in new Test {

          MockRetrievePropertyBsasConnector
            .retrievePropertyBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", CalculationIdFormatError),
        ("INVAlID_CORRELATIONID", InternalError),
        ("INVALID_RETURN", InternalError),
        ("UNPROCESSABLE_ENTITY", InternalError),
        ("NO_DATA_FOUND", NotFoundError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError)
      )

      val extraTysErrors = List(
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("NOT_FOUND", NotFoundError),
        ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
      )

      (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
    }
  }

}
