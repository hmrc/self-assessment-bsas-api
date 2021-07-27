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

package v1.connectors

import mocks.MockAppConfig
import domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.fixtures.ukProperty.RetrieveUkPropertyAdjustmentsFixtures._
import v1.mocks.MockHttpClient
import v1.models.outcomes.ResponseWrapper
import v1.models.request.RetrieveAdjustmentsRequestData

import scala.concurrent.Future

class RetrieveUkPropertyAdjustmentsConnectorSpec extends ConnectorSpec {

  val nino = Nino("AA123456A")
  val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val queryParams: Map[String, String] = Map("return" -> "2")

  class Test extends MockHttpClient with MockAppConfig {
    val connector: RetrieveUkPropertyAdjustmentsConnector =
      new RetrieveUkPropertyAdjustmentsConnector( http = mockHttpClient, appConfig = mockAppConfig)

    val desRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnv returns "des-environment"
    MockedAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
  }

  "RetrieveUkPropertyAdjustments" should {
    "return a valid response" when {
      val outcome = Right(ResponseWrapper(correlationId, mtdJson))
      implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders)
      "a valid request with queryParams is supplied" in new Test {
        val request = RetrieveAdjustmentsRequestData(nino, bsasId)

        MockedHttpClient.parameterGet(
          url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$bsasId",
          config = dummyDesHeaderCarrierConfig,
          queryParams.toSeq,
          requiredHeaders = desRequestHeaders,
          excludedHeaders = Seq("AnotherHeader" -> s"HeaderValue")
        ).returns(Future.successful(outcome))

        await(connector.retrieveUkPropertyAdjustments(request)) shouldBe outcome
      }
    }
  }
}
