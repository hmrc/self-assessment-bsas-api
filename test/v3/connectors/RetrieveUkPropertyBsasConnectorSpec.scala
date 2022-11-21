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

package v3.connectors

import domain.Nino
import v3.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.retrieveBsas.ukProperty.RetrieveUkPropertyBsasRequestData

import scala.concurrent.Future

class RetrieveUkPropertyBsasConnectorSpec extends ConnectorSpec {

  val nino = Nino("AA123456A")

  trait Test {
    _: ConnectorTest =>
    val connector: RetrieveUkPropertyBsasConnector = new RetrieveUkPropertyBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)
  }

  "retrieve" should {
    "return a valid response" when {
      val outcome = Right(ResponseWrapper(correlationId, retrieveBsasResponseFhlModel))

      "a valid request is supplied" in new IfsTest with Test {
        val request = RetrieveUkPropertyBsasRequestData(nino, "incomeSourceId")

        val expectedUrl =  s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/incomeSourceId"

        willGet(url = expectedUrl) returns Future.successful(outcome)

        await(connector.retrieve(request)) shouldBe outcome
      }
    }
  }
}
