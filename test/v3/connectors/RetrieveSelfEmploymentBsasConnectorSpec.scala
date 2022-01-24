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
import v3.mocks.MockHttpClient
import v3.models.outcomes.ResponseWrapper
import v3.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures._
import v3.models.request.retrieveBsas.selfEmployment.RetrieveSelfEmploymentBsasRequestData

import scala.concurrent.Future

class RetrieveSelfEmploymentBsasConnectorSpec extends ConnectorSpec {

  val nino: Nino = Nino("AA123456A")
  val calculationId: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  class Test extends MockHttpClient with MockAppConfig {
    val connector: RetrieveSelfEmploymentBsasConnector = new RetrieveSelfEmploymentBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)

    val ifsRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "ifs-environment", "Authorization" -> s"Bearer ifs-token")
    MockedAppConfig.ifsBaseUrl returns baseUrl
    MockedAppConfig.ifsToken returns "ifs-token"
    MockedAppConfig.ifsEnv returns "ifs-environment"
    MockedAppConfig.ifsEnvironmentHeaders returns Some(allowedDesHeaders)
    MockedAppConfig.ifsEnabled returns true
  }

  "retrieveSEBsas" should {
    "return a valid response" when {
      val outcome = Right(ResponseWrapper(correlationId, mtdRetrieveBsasResponseJson))
      implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders)
      "a valid request with queryParams is supplied" in new Test {
        val request: RetrieveSelfEmploymentBsasRequestData = RetrieveSelfEmploymentBsasRequestData(nino, calculationId)

        MockedHttpClient.get(
          url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$calculationId",
          config = dummyDesHeaderCarrierConfig,
          requiredHeaders = ifsRequestHeaders,
          excludedHeaders = Seq("AnotherHeader" -> s"HeaderValue")
        ).returns(Future.successful(outcome))

        await(connector.retrieveSelfEmploymentBsas(request)) shouldBe outcome
      }
    }
  }
}
