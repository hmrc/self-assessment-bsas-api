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

import mocks.MockAppConfig
import domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v2.fixtures.TriggerBsasRequestBodyFixtures._
import v2.mocks.MockHttpClient
import v2.models.outcomes.ResponseWrapper
import v2.models.request.triggerBsas.TriggerBsasRequest
import v2.models.response.TriggerBsasResponse

import scala.concurrent.Future

class TriggerBsasConnectorSpec extends ConnectorSpec {

  val nino = Nino("AA123456A")
  val id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  class Test extends MockHttpClient with MockAppConfig {
    val connector: TriggerBsasConnector = new TriggerBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)

    val desRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnv returns "des-environment"
    MockedAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
    MockedAppConfig.ifsEnabled returns false
  }

  "triggerBsas" must {
    val request = TriggerBsasRequest(nino, model)

    "post a TriggerBsasRequest body and return the result" in new Test {
      val outcome = Right(ResponseWrapper(correlationId, TriggerBsasResponse(id)))
      implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))
      val requiredHeadersPost: Seq[(String, String)] = desRequestHeaders ++ Seq("Content-Type" -> "application/json")

      MockedHttpClient.post(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}",
        config = dummyDesHeaderCarrierConfig,
        body = model,
        requiredHeaders = requiredHeadersPost,
        excludedHeaders = Seq("AnotherHeader" -> s"HeaderValue")
      ).returns(Future.successful(outcome))

      await(connector.triggerBsas(request)) shouldBe outcome
    }
  }
}