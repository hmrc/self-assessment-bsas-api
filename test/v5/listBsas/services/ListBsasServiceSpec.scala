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

package v5.listBsas.services

import shared.controllers.EndpointLogContext
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors.{BusinessIdFormatError, DownstreamErrorCode, DownstreamErrors, ErrorWrapper, InternalError, MtdError, NinoFormatError, NotFoundError, RuleTaxYearNotSupportedError, TaxYearFormatError}
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v5.listBsas.connectors.MockListBsasConnector
import v5.listBsas.fixtures.def1.ListBsasFixture
import v5.listBsas.models.def1.Def1_ListBsasRequestData
import v5.listBsas.models.{BsasSummary, ListBsasRequestData, ListBsasResponse}

import scala.concurrent.Future

class ListBsasServiceSpec extends ServiceSpec with ListBsasFixture {

  private val nino                   = Nino("AA123456A")
  private val taxYear                = TaxYear.fromMtd("2019-20")
  private val incomeSourceIdentifier = "IncomeSourceType"
  private val identifierValue        = BusinessId("01")

  val request: ListBsasRequestData            = Def1_ListBsasRequestData(nino, taxYear, Some(identifierValue), Some(incomeSourceIdentifier))
  val response: ListBsasResponse[BsasSummary] = listBsasResponseModel

  trait Test extends MockListBsasConnector {
    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "listBsas")

    val service = new ListBsasService(mockConnector)
  }

  "ListBsas" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockListBsasConnector
          .listBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.listBsas(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }

    "return error response" when {

      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"$downstreamErrorCode is returned from the service" in new Test {

          MockListBsasConnector
            .listBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.listBsas(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = Seq(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("NO_DATA_FOUND", NotFoundError),
        ("INVALID_TAXYEAR", TaxYearFormatError),
        ("INVALID_INCOMESOURCEID", BusinessIdFormatError),
        ("INVALID_INCOMESOURCE_TYPE", InternalError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError)
      )

      val extraTysErrors = Seq(
        ("INVALID_CORRELATION_ID", InternalError),
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("INVALID_INCOMESOURCE_ID", BusinessIdFormatError),
        ("NOT_FOUND", NotFoundError),
        ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
      )

      (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
    }
  }

}
