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
import v2.fixtures.ListBsasFixtures._
import v2.mocks.connectors.MockListBsasConnector
import v2.models.domain.DownstreamTaxYear
import api.models.ResponseWrapper
import api.models.domain.Nino
import v2.models.request.ListBsasRequest
import v2.models.response.listBsas.{BsasEntries, ListBsasResponse}

import scala.concurrent.Future

class ListBsasServiceSpec extends ServiceSpec {

  private val nino                   = Nino("AA123456A")
  private val taxYear                = DownstreamTaxYear("2019-20")
  private val incomeSourceIdentifier = Some("IncomeSourceType")
  private val identifierValue        = Some("01")

  val request: ListBsasRequest                = ListBsasRequest(nino, taxYear, incomeSourceIdentifier, identifierValue)
  val response: ListBsasResponse[BsasEntries] = summaryModel

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

      val input = Seq(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("NO_DATA_FOUND", NotFoundError),
        ("INVALID_TAXYEAR", InternalError),
        ("INVALID_INCOMESOURCEID", InternalError),
        ("INVALID_INCOMESOURCE_TYPE", InternalError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}
