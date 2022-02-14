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
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasFixtures._
import v3.mocks.connectors.MockRetrieveForeignPropertyBsasConnector
import v3.models.domain.TypeOfBusiness
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasRequestData
import v3.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasResponse

import scala.concurrent.Future

class RetrieveForeignPropertyBsasServiceSpec extends ServiceSpec{

  private val nino = Nino("AA123456A")
  val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val adjustedStatus: Some[String] = Some("03")

  val request: RetrieveForeignPropertyBsasRequestData = RetrieveForeignPropertyBsasRequestData(nino, id, adjustedStatus)

  val response: RetrieveForeignPropertyBsasResponse = RetrieveForeignPropertyBsasResponse(metadataModel, Some(bsasDetailModel))

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
      "downstream returns a success response with invalid type of business" should {
        import TypeOfBusiness._
        Seq(`self-employment`, `uk-property-fhl`, `uk-property-non-fhl`).foreach(typeOfBusiness =>
          s"return an error for $typeOfBusiness" in new Test {
            val response: RetrieveForeignPropertyBsasResponse = RetrieveForeignPropertyBsasResponse(metadataModel.copy(typeOfBusiness),
              Some(bsasDetailModel))

            MockRetrieveForeignPropertyBsasConnector
              .retrieveForeignPropertyBsas(request)
              .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

            await(service.retrieveForeignPropertyBsas(request)) shouldBe Left(ErrorWrapper(correlationId, RuleTypeOfBusinessIncorrectError))
          })
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
