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
import utils.DesTaxYear
import v1.mocks.MockHttpClient
import v1.models.request.submitBsas.selfEmployment.{Additions, Expenses, Income, SubmitSelfEmploymentBsasRequestBody}

import scala.concurrent.Future

class SubmitSelfEmploymentBsasNrsProxyConnectorSpec extends ConnectorSpec {

  val nino: String = "AA111111A"

  val taxYear: DesTaxYear = DesTaxYear.fromMtd("2021-22")

  val income: Income =  Income(Some(100.99), Some(100.99))

  val additions: Additions = Additions(Some(100.99),
    Some(100.99), Some(100.99), Some(100.99),
    Some(100.99), Some(100.99), Some(100.99),
    Some(100.99), Some(100.99), Some(100.99),
    Some(100.99), Some(100.99), Some(100.99),
    Some(100.99), Some(100.99))

  val expenses: Expenses = Expenses(Some(100.99),
    Some(100.99), Some(100.99), Some(100.99),
    Some(100.99), Some(100.99), Some(100.99),
    Some(100.99), Some(100.99), Some(100.99),
    Some(100.99), Some(100.99), Some(100.99),
    Some(100.99), Some(100.99), Some(100.99))

  val request: SubmitSelfEmploymentBsasRequestBody =
    SubmitSelfEmploymentBsasRequestBody(Some(income), Some(additions), Some(expenses))

  class Test extends MockHttpClient with MockAppConfig {

    val connector: SubmitSelfEmploymentBsasNrsProxyConnector = new SubmitSelfEmploymentBsasNrsProxyConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockedAppConfig.mtdNrsProxyBaseUrl returns baseUrl
  }

  "NrsproxyConnector" when {
    "submit with valid data" should {
      "be successful" in new Test {


        MockedHttpClient
          .post(
            url = s"$baseUrl/mtd-api-nrs-proxy/$nino/itsa-annual-adjustment",
            body = request
          ).returns(Future.successful((): Unit))

        await(connector.submit(nino, request)) shouldBe ((): Unit)
      }
    }
  }
}
