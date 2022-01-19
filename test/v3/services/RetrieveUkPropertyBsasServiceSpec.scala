/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.services

import domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v3.controllers.EndpointLogContext
import v3.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures._
import v3.mocks.connectors.MockRetrieveUkPropertyBsasConnector
import v3.models.domain.TypeOfBusiness
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.RetrieveUkPropertyBsasRequestData
import v3.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse

import scala.concurrent.Future

class RetrieveUkPropertyBsasServiceSpec extends ServiceSpec{

  private val nino = Nino("AA123456A")
  val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val adjustedStatus = Some("03")

  val request = RetrieveUkPropertyBsasRequestData(nino, id, adjustedStatus)

  val response = RetrieveUkPropertyBsasResponse(metadataModel, Some(bsasDetailModel))

  trait Test extends MockRetrieveUkPropertyBsasConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("RetrieveUkPropertyBsasConnector", "retrieve")

    val service = new RetrieveUkPropertyBsasService(mockConnector)
  }

  "retrieve" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockRetrievePropertyBsasConnector.retrievePropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieve(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }


    "return error response" when {

      "des return success response with invalid type of business" in new Test {
        val response = RetrieveUkPropertyBsasResponse(metadataModel.copy(typeOfBusiness = TypeOfBusiness.`self-employment`),
          Some(bsasDetailModel))
        MockRetrievePropertyBsasConnector.retrievePropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, RuleNotUkProperty))
      }

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockRetrievePropertyBsasConnector.retrievePropertyBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(


        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", BsasIdFormatError),
        ("INVAlID_CORRELATION_ID", DownstreamError),
        ("INVALID_RETURN", DownstreamError),
        ("UNPROCESSABLE_ENTITY", RuleNoAdjustmentsMade),
        ("NO_DATA_FOUND", NotFoundError),
        ("SERVER_ERROR", DownstreamError),
        ("SERVICE_UNAVAILABLE", DownstreamError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}