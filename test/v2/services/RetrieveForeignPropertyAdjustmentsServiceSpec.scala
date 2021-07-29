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

import domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v2.controllers.EndpointLogContext
import v2.fixtures.foreignProperty.RetrieveForeignPropertyAdjustmentsFixtures.{foreignPropertyMetaDataModel, nonFhlBsasDetailModel}
import v2.mocks.connectors.MockRetrieveForeignPropertyAdjustmentsConnector
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.RetrieveAdjustmentsRequestData
import v2.models.response.retrieveBsasAdjustments.foreignProperty._

import scala.concurrent.Future

class RetrieveForeignPropertyAdjustmentsServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")

  val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val request = RetrieveAdjustmentsRequestData(nino, id)
  val response = RetrieveForeignPropertyAdjustmentsResponse(foreignPropertyMetaDataModel, Seq(nonFhlBsasDetailModel))

  trait Test extends MockRetrieveForeignPropertyAdjustmentsConnector {

    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("RetrieveForeignPropertyAdjustmentsController", "RetrieveForeignPropertyAdjustments")

    val service = new RetrieveForeignPropertyAdjustmentsService(mockConnector)
  }

  "retrieveForeignPropertyAdjustments" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockRetrieveForeignPropertyAdjustmentsConnector.retrieveForeignPropertyAdjustments(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieveForeignPropertyAdjustments(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockRetrieveForeignPropertyAdjustmentsConnector.retrieveForeignPropertyAdjustments(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.retrieveForeignPropertyAdjustments(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(
        "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
        "INVALID_CALCULATION_ID" -> BsasIdFormatError,
        "INVALID_CORRELATION_ID" -> DownstreamError,
        "INVALID_RETURN" -> DownstreamError,
        "UNPROCESSABLE_ENTITY" -> RuleNoAdjustmentsMade,
        "NO_DATA_FOUND" -> NotFoundError,
        "SERVER_ERROR" -> DownstreamError,
        "SERVICE_UNAVAILABLE" -> DownstreamError
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}
