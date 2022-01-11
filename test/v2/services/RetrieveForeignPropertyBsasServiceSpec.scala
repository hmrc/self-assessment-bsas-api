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

package v2.services

import domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v2.controllers.EndpointLogContext
import v2.fixtures.foreignProperty.RetrieveForeignPropertyBsasFixtures._
import v2.mocks.connectors.MockRetrieveForeignPropertyBsasConnector
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasRequestData
import v2.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasResponse

import scala.concurrent.Future

class RetrieveForeignPropertyBsasServiceSpec extends ServiceSpec{

  private val nino = Nino("AA123456A")
  val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val adjustedStatus = Some("03")

  val request = RetrieveForeignPropertyBsasRequestData(nino, id, adjustedStatus)

  val response = RetrieveForeignPropertyBsasResponse(metadataModel, Some(bsasDetailModel))

  trait Test extends MockRetrieveForeignPropertyBsasConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("RetrieveUkPropertyBsasConnector", "retrieve")

    val service = new RetrieveForeignPropertyBsasService(mockConnector)
  }

  "retrieve" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockRetrieveForeignPropertyBsasConnector.retrieveForeignPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieveForeignPropertyBsas(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }


    "return error response" when {

      "des return success response with invalid type of business" in new Test {
        val response = RetrieveForeignPropertyBsasResponse(metadataModel.copy(typeOfBusiness = TypeOfBusiness.`self-employment`),
          Some(bsasDetailModel))
        MockRetrieveForeignPropertyBsasConnector.retrieveForeignPropertyBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieveForeignPropertyBsas(request)) shouldBe Left(ErrorWrapper(correlationId, RuleNotForeignProperty))
      }

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockRetrieveForeignPropertyBsasConnector.retrieveForeignPropertyBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.retrieveForeignPropertyBsas(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(


        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("UNPROCESSABLE_ENTITY", RuleNoAdjustmentsMade),
        ("INVAlID_CORRELATION_ID", DownstreamError),
        ("INVALID_RETURN", AdjustedStatusFormatError),
        ("INVALID_CALCULATION_ID", BsasIdFormatError),
        ("NO_DATA_FOUND", NotFoundError),
        ("SERVER_ERROR", DownstreamError),
        ("SERVICE_UNAVAILABLE", DownstreamError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}
