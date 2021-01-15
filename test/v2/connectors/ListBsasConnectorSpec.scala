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

package v2.connectors

import mocks.MockAppConfig
import uk.gov.hmrc.domain.Nino
import utils.DesTaxYear
import v2.fixtures.ListBsasFixtures._
import v2.mocks.MockHttpClient
import v2.models.outcomes.ResponseWrapper
import v2.models.request.ListBsasRequest

import scala.concurrent.Future

class ListBsasConnectorSpec extends ConnectorSpec {

  val nino = Nino("AA123456A")

  val queryParams: Map[String, String] = Map(
    "taxYear" -> "2019",
    "incomeSourceId" -> "incomeSourceId",
    "incomeSourceType" -> "02"
  )

  class Test extends MockHttpClient with MockAppConfig {
    val connector: ListBsasConnector = new ListBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)

    val desRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnvironment returns "des-environment"
  }

  "listBsas" when {
    "provided with a valid request" must {
      val request = ListBsasRequest(nino, DesTaxYear("2019"), Some("incomeSourceId"), Some("02"))

      "return a ListBsasResponse" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, summaryModel))

        MockedHttpClient.parameterGet(
          url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}",
          queryParams.toSeq,
          requiredHeaders = "Environment" -> "des-environment", "Authorization" -> s"Bearer des-token"
        ).returns(Future.successful(outcome))

        await(connector.listBsas(request)) shouldBe outcome
      }
    }
  }
}
