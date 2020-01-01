/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.services

import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.controllers.EndpointLogContext
import v1.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures._
import v1.models.errors._
import v1.mocks.connectors.MockRetrieveSelfEmploymentBsasConnector
import v1.models.domain.TypeOfBusiness
import v1.models.outcomes.ResponseWrapper
import v1.models.request.RetrieveSelfEmploymentBsasRequestData
import v1.models.response.retrieveBsas.selfEmployment.RetrieveSelfEmploymentBsasResponse

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class RetrieveSelfEmploymentBsasServiceSpec extends UnitSpec{

  private val nino = Nino("AA123456A")
  val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val adjustedStatus = Some("true")
  private val correlationId = "X-123"
  
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

        await(service.retrieveSelfEmploymentBsas(request)) shouldBe Left(ErrorWrapper(Some(correlationId), RuleNotSelfEmployment))
      }

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockRetrieveSelfEmploymentBsasConnector.retrieveSelfEmploymentBsas(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.retrieveSelfEmploymentBsas(request)) shouldBe Left(ErrorWrapper(Some(correlationId), error))
        }

      val input = Seq(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", BsasIdFormatError),
        ("INVALID_RETURN", DownstreamError),
        ("UNPROCESSABLE_ENTITY", RuleNoAdjustmentsMade),
        ("NOT_FOUND", NotFoundError),
        ("SERVER_ERROR", DownstreamError),
        ("SERVICE_UNAVAILABLE", DownstreamError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}
