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

import domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v2.controllers.EndpointLogContext
import v2.fixtures.ukProperty.RetrieveUkPropertyAdjustmentsFixtures._
import v2.mocks.connectors.MockRetrieveUkPropertyAdjustmentsConnector
import v2.models.outcomes.ResponseWrapper
import v2.models.request.RetrieveAdjustmentsRequestData
import v2.models.response.retrieveBsasAdjustments.ukProperty.RetrieveUkPropertyAdjustmentsResponse

import scala.concurrent.Future

class RetrieveUkPropertyAdjustmentsServiceSpec extends ServiceSpec {

  private val nino = Nino("AA123456A")

  val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val request = RetrieveAdjustmentsRequestData(nino, id)
  val response = RetrieveUkPropertyAdjustmentsResponse(metaDataModel, bsasDetailModel)

  trait Test extends MockRetrieveUkPropertyAdjustmentsConnector {

    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("RetrieveUkPropertyAdjustmentsController", "RetrieveUkPropertyAdjustments")

    val service = new RetrieveUkPropertyAdjustmentsService(mockConnector)
  }

  "retrieveUkPropertyAdjustments" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockRetrieveUkPropertyAdjustmentsConnector.retrieveUkPropertyAdjustments(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieveUkPropertyAdjustments(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }
  }
}
