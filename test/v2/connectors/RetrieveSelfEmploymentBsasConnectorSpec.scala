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

package v2.connectors

import mocks.MockAppConfig
import uk.gov.hmrc.domain.Nino
import v2.mocks.MockHttpClient
import v2.models.outcomes.ResponseWrapper
import v2.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures._
import v2.models.request.RetrieveSelfEmploymentBsasRequestData

import scala.concurrent.Future

class RetrieveSelfEmploymentBsasConnectorSpec extends ConnectorSpec {

  val nino = Nino("AA123456A")
  val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val queryParams: Map[String, String] = Map("return" -> "01")

  class Test extends MockHttpClient with MockAppConfig {
    val connector: RetrieveSelfEmploymentBsasConnector = new RetrieveSelfEmploymentBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)

    val desRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnvironment returns "des-environment"
  }

  "retrievePropertyBsas" should {
    "return a valid response" when {
      val outcome = Right(ResponseWrapper(correlationId, mtdRetrieveBsasResponseJson))

      "a valid request with queryParams is supplied" in new Test {
        val request = RetrieveSelfEmploymentBsasRequestData(nino, bsasId, Some("01"))

        MockedHttpClient.parameterGet(
          url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$bsasId",
          queryParams.toSeq,
          requiredHeaders = "Environment" -> "des-environment", "Authorization" -> s"Bearer des-token"
        ).returns(Future.successful(outcome))

        await(connector.retrieveSelfEmploymentBsas(request)) shouldBe outcome
      }

      "a valid request without queryParams is supplied" in new Test {
        val request = RetrieveSelfEmploymentBsasRequestData(nino, bsasId, None)

        MockedHttpClient.parameterGet(
          url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$bsasId",
          Seq.empty,
          requiredHeaders = "Environment" -> "des-environment", "Authorization" -> s"Bearer des-token"
        ).returns(Future.successful(outcome))

        await(connector.retrieveSelfEmploymentBsas(request)) shouldBe outcome
      }
    }
  }
}
