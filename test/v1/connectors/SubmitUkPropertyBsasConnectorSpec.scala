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

package v1.connectors

import v1.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._
import mocks.MockAppConfig
import uk.gov.hmrc.domain.Nino
import v1.mocks.MockHttpClient
import v1.models.domain.TypeOfBusiness
import v1.models.outcomes.ResponseWrapper
import v1.models.request.submitBsas.SubmitUkPropertyBsasRequestData
import v1.models.response.SubmitUkPropertyBsasResponse

import scala.concurrent.Future

class SubmitUkPropertyBsasConnectorSpec  extends ConnectorSpec {

  val nino = Nino("AA123456A")
  val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  class Test extends MockHttpClient with MockAppConfig {
    val connector: SubmitUkPropertyBsasConnector = new SubmitUkPropertyBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)

    val desRequestHeaders: Seq[(String,String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnvironment returns "des-environment"
  }

  "submitBsas" must {
    val request = SubmitUkPropertyBsasRequestData(nino, bsasId, nonFHLBody)

    "post a SubmitBsasRequest body and return the result" in new Test {
      val outcome = Right(ResponseWrapper(correlationId, SubmitUkPropertyBsasResponse(bsasId, TypeOfBusiness.`uk-property-fhl`)))

      MockedHttpClient.post(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$bsasId",
        body = nonFHLBody,
        requiredHeaders = "Environment" -> "des-environment", "Authorization" -> s"Bearer des-token"
      ).returns(Future.successful(outcome))

      await(connector.submitPropertyBsas(request)) shouldBe outcome
    }
  }
}
