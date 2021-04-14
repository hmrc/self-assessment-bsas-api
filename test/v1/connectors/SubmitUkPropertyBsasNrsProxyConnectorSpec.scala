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
import v1.models.request.submitBsas._

import scala.concurrent.Future

class SubmitUkPropertyBsasNrsProxyConnectorSpec extends ConnectorSpec {

  class SubmitUKPropertyBsasNrsProxyConnectorSpec extends ConnectorSpec {

    val nino: String = "AA111111A"

    val taxYear: DesTaxYear = DesTaxYear.fromMtd("2021-22")

    val nonFurnishedHolidayLet: NonFurnishedHolidayLet = NonFurnishedHolidayLet(Some(NonFHLIncome(Some(100.99),
      Some(100.99), Some(100.99), Some(100.99))), Some(NonFHLExpenses(Some(100.99),
      Some(100.99), Some(100.99), Some(100.99),
      Some(100.99), Some(100.99), Some(100.99),
      Some(100.99), Some(100.99))))

    val furnishedHolidayLet: FurnishedHolidayLet = FurnishedHolidayLet(Some(FHLIncome(Some(100.99))), Some(FHLExpenses(Some(100.99),
      Some(100.99), Some(100.99), Some(100.99),
      Some(100.99), Some(100.99), Some(100.99),
      Some(100.99))))

    val request: SubmitUKPropertyBsasRequestBody =
      SubmitUKPropertyBsasRequestBody(Some(nonFurnishedHolidayLet), Some(furnishedHolidayLet))

    class Test extends MockHttpClient with MockAppConfig {
      val connector: SubmitUKPropertyBsasNrsProxyConnector = new SubmitUKPropertyBsasNrsProxyConnector(
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
}