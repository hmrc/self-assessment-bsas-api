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

package v2.connectors

import mocks.MockAppConfig
import utils.DesTaxYear
import v2.mocks.MockHttpClient
import v2.models.request.submitBsas.foreignProperty.{ForeignProperty, ForeignPropertyExpenses, ForeignPropertyIncome, SubmitForeignPropertyBsasRequestBody}

import scala.concurrent.Future

class SubmitForeignPropertyBsasNrsProxyConnectorSpec extends ConnectorSpec {

  val nino: String = "AA111111A"

  val taxYear: DesTaxYear = DesTaxYear.fromMtd("2021-22")

  val request: SubmitForeignPropertyBsasRequestBody = {
    SubmitForeignPropertyBsasRequestBody(
      Some(Seq(ForeignProperty(
        "FRA",
        Some(ForeignPropertyIncome(
          Some(123.12),
          Some(123.12),
          Some(123.12)
        )),
        Some(ForeignPropertyExpenses(
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          consolidatedExpenses = None
        ))
      ))),
      foreignFhlEea = None
    )
  }

  class Test extends MockHttpClient with MockAppConfig {

    val connector: SubmitForeignPropertyBsasNrsProxyConnector = new SubmitForeignPropertyBsasNrsProxyConnector(
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
            config = dummyDesHeaderCarrierConfig,
            body = request
          ).returns(Future.successful((): Unit))

        await(connector.submit(nino, request)) shouldBe ((): Unit)
      }
    }
  }
}

