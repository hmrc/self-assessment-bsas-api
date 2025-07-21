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

package v6.bsas.list

import shared.controllers.EndpointLogContext
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v6.bsas.list.def2.model.Def2_ListBsasFixtures
import v6.bsas.list.def2.model.request.Def2_ListBsasRequestData
import v6.bsas.list.model.request.ListBsasRequestData

import scala.concurrent.Future

class ListBsasServiceSpec extends ServiceSpec {

  private val nino                   = Nino("AA123456A")
  private val taxYear                = TaxYear.fromMtd("2019-20")
  private val postFHLRemovalTaxYear  = TaxYear.fromMtd("2025-26")
  private val incomeSourceIdentifier = "IncomeSourceType"
  private val identifierValue        = BusinessId("01")

  val fhlRequest: ListBsasRequestData = Def2_ListBsasRequestData(nino, taxYear, Some(identifierValue), Some(incomeSourceIdentifier))
  val request: ListBsasRequestData    = Def2_ListBsasRequestData(nino, postFHLRemovalTaxYear, Some(identifierValue), Some(incomeSourceIdentifier))

  trait Test extends MockListBsasConnector {
    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "listBsas")

    val service = new ListBsasService(mockConnector)
  }

  "ListBsas" should {
    "return a valid response with" when {
      "a valid request is supplied" in new Test with Def2_ListBsasFixtures {
        MockListBsasConnector
          .listBsas(fhlRequest)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, listBsasResponse))))

        await(service.listBsas(fhlRequest)) shouldBe Right(ResponseWrapper(correlationId, listBsasResponse))
      }
    }

    "return error response" when {

      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"$downstreamErrorCode is returned from the service" in new Test with Def2_ListBsasFixtures {

          MockListBsasConnector
            .listBsas(fhlRequest)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.listBsas(fhlRequest)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("NO_DATA_FOUND", NotFoundError),
        ("INVALID_TAXYEAR", TaxYearFormatError),
        ("INVALID_INCOMESOURCEID", BusinessIdFormatError),
        ("INVALID_INCOMESOURCE_TYPE", InternalError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError)
      )

      val extraTysErrors = List(
        ("INVALID_CORRELATION_ID", InternalError),
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("INVALID_INCOMESOURCE_ID", BusinessIdFormatError),
        ("NOT_FOUND", NotFoundError),
        ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
      )

      (errors ++ extraTysErrors).foreach(args => serviceError.tupled(args))
    }
  }

}
