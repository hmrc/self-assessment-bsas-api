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
import v3.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.submitBsas.ukProperty.SubmitUkPropertyBsasRequestData

import scala.concurrent.Future

class SubmitUkPropertyBsasConnectorSpec  extends ConnectorSpec {

  val nino = Nino("AA123456A")
  val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  trait Test {
    _: ConnectorTest =>
    val connector: SubmitUkPropertyBsasConnector = new SubmitUkPropertyBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)
  }

  "submitBsas" must {
    val request = SubmitUkPropertyBsasRequestData(nino, bsasId, nonFHLBody)

    "post a SubmitBsasRequest body and return the result" in new DesTest with Test {
      val outcome = Right(ResponseWrapper(correlationId, ()))

      val expectedUrl = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$bsasId"
      willPut(url = expectedUrl, nonFHLBody) returns Future.successful(outcome)

      await(connector.submitPropertyBsas(request)) shouldBe outcome
    }
  }
}
