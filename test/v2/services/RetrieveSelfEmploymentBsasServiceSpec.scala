/*
 * Copyright 2021 HM Revenue & Customs
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

import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v2.controllers.EndpointLogContext
import v2.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures._
import v2.mocks.connectors.MockRetrieveSelfEmploymentBsasConnector
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.RetrieveSelfEmploymentBsasRequestData
import v2.models.response.retrieveBsas.selfEmployment.RetrieveSelfEmploymentBsasResponse

import scala.concurrent.Future

class RetrieveSelfEmploymentBsasServiceSpec extends ServiceSpec{

  private val nino = Nino("AA123456A")
  val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val adjustedStatus = Some("true")

  val request = RetrieveSelfEmploymentBsasRequestData(nino, id, adjustedStatus)
  
  val response = RetrieveSelfEmploymentBsasResponse(metadataModel(true), Some(bsasDetailModel))

  trait Test extends MockRetrieveSelfEmploymentBsasConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("RetrieveSelfEmploymentBsasConnector", "retrieveSelfEmploymentBsas")

    val service = new RetrieveSelfEmploymentBsasService(mockConnector)
  }

  "retrieveSelfEmploymentBsas" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test{
        MockRetrieveSelfEmploymentBsasConnector.retrieveSelfEmploymentBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieveSelfEmploymentBsas(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }

    "return an error response" when {
      "des return success response with invalid type of business" in new Test {
        val response = RetrieveSelfEmploymentBsasResponse(metadataModel(true).copy(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`),
          Some(bsasDetailModel))
        MockRetrieveSelfEmploymentBsasConnector.retrieveSelfEmploymentBsas(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieveSelfEmploymentBsas(request)) shouldBe Left(ErrorWrapper(correlationId, RuleNotSelfEmployment))
      }

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockRetrieveSelfEmploymentBsasConnector.retrieveSelfEmploymentBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.retrieveSelfEmploymentBsas(request)) shouldBe Left(ErrorWrapper(correlationId, error))
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
