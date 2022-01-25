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
import v3.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures._
import v3.mocks.MockHttpClient
import v3.models.outcomes.ResponseWrapper
import v3.models.request.retrieveBsas.ukProperty.RetrieveUkPropertyBsasRequestData

import scala.concurrent.Future

class RetrieveUkPropertyBsasConnectorSpec extends ConnectorSpec {

  val nino = Nino("AA123456A")
<<<<<<< HEAD
=======
  val calculationId: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
>>>>>>> 220b5eb (MTDSA-10866 UK Property BSAS V3 Connector and Service)

  class Test extends MockHttpClient with MockAppConfig {
    val connector: RetrieveUkPropertyBsasConnector = new RetrieveUkPropertyBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)

    val ifsRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "ifs-environment", "Authorization" -> s"Bearer ifs-token")
    MockedAppConfig.ifsBaseUrl returns baseUrl
    MockedAppConfig.ifsToken returns "ifs-token"
    MockedAppConfig.ifsEnv returns "ifs-environment"
    MockedAppConfig.ifsEnvironmentHeaders returns Some(allowedDesHeaders)
    MockedAppConfig.ifsEnabled returns true
  }

  "retrieve" should {
    "return a valid response" when {
      val outcome = Right(ResponseWrapper(correlationId, retrieveBsasResponseFhlModel))
      implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders)
      "a valid request is supplied" in new Test {
<<<<<<< HEAD
        val request = RetrieveUkPropertyBsasRequestData(nino, "incomeSourceId")

        MockedHttpClient.get(
          url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/incomeSourceId",
          config = dummyDesHeaderCarrierConfig,
          requiredHeaders = desRequestHeaders,
=======
        val request: RetrieveUkPropertyBsasRequestData = RetrieveUkPropertyBsasRequestData(nino, calculationId, Some("03"))

        MockedHttpClient.get(
          url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$calculationId",
          config = dummyDesHeaderCarrierConfig,
          requiredHeaders = ifsRequestHeaders,
>>>>>>> 220b5eb (MTDSA-10866 UK Property BSAS V3 Connector and Service)
          excludedHeaders = Seq("AnotherHeader" -> s"HeaderValue")
        ).returns(Future.successful(outcome))

        await(connector.retrieve(request)) shouldBe outcome
      }
    }
  }
}
