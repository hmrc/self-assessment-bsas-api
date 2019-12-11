/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.connectors

import mocks.MockAppConfig
import uk.gov.hmrc.domain.Nino
import v1.fixtures.RetrievePropertyBsasFixtures._
import v1.mocks.MockHttpClient
import v1.models.outcomes.ResponseWrapper
import v1.models.request.RetrieveUkPropertyRequest

import scala.concurrent.Future

class RetrievePropertyBsasConnectorSpec extends ConnectorSpec {

  val nino = Nino("AA123456A")

  val queryParams: Map[String, String] = Map("return" -> "03")

  class Test extends MockHttpClient with MockAppConfig {
    val connector: RetrievePropertyBsasConnector = new RetrievePropertyBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)

    val desRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnvironment returns "des-environment"
  }

  "retrievePropertyBsas" should {
    "return a valid response" when {
      val outcome = Right(ResponseWrapper(correlationId, mtdResponse))

      "a valid request with queryParams is supplied" in new Test {
        val request = RetrieveUkPropertyRequest(nino, "incomeSourceId", Some("03"))

        MockedHttpClient.parameterGet(
          url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/incomeSourceId",
          queryParams.toSeq,
          requiredHeaders = "Environment" -> "des-environment", "Authorization" -> s"Bearer des-token"
        ).returns(Future.successful(outcome))

        await(connector.retrievePropertyBsas(request)) shouldBe outcome
      }

      "a valid request without queryParams is supplied" in new Test {
        val request = RetrieveUkPropertyRequest(nino, "incomeSourceId", None)

        MockedHttpClient.parameterGet(
          url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/incomeSourceId",
          Seq.empty,
          requiredHeaders = "Environment" -> "des-environment", "Authorization" -> s"Bearer des-token"
        ).returns(Future.successful(outcome))

        await(connector.retrievePropertyBsas(request)) shouldBe outcome
      }
    }
  }
}
