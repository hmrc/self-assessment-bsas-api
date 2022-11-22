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

import domain.Nino
import v3.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._
import v3.models.domain.TaxYear
import v3.models.outcomes.ResponseWrapper
import v3.models.request.submitBsas.ukProperty.SubmitUkPropertyBsasRequestData

import scala.concurrent.Future

class SubmitUkPropertyBsasConnectorSpec extends ConnectorSpec {

  val nino: String   = "AA123456A"
  val bsasId: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  def makeRequest(taxYear: Option[String]): SubmitUkPropertyBsasRequestData = {
    SubmitUkPropertyBsasRequestData(
      nino = Nino(nino),
      calculationId = bsasId,
      body = nonFHLBody,
      taxYear = taxYear.map(TaxYear.fromMtd)
    )
  }

  val nonTysRequest = makeRequest(None)
  val tysRequest    = makeRequest(Some("2023-24"))

  trait Test {
    _: ConnectorTest =>

    protected val connector: SubmitUkPropertyBsasConnector = new SubmitUkPropertyBsasConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )
  }

  "SubmitUKPropertyBsasConnector" when {
    "SubmitUKPropertyBsas" must {
      "post a SubmitBsasRequest body and return the result for the non-TYS scenario" in new IfsTest with Test {

        val outcome = Right(ResponseWrapper(correlationId, ()))
        val url     = s"$baseUrl/income-tax/adjustable-summary-calculation/$nino/$bsasId"

        willPut(url = url, body = nonFHLBody) returns Future.successful(outcome)

        await(connector.submitPropertyBsas(nonTysRequest)) shouldBe outcome
      }

      "post a SubmitBsasRequest body and return the result for the TYS scenario" in new TysIfsTest with Test {

        val outcome = Right(ResponseWrapper(correlationId, ()))
        val url     = s"$baseUrl/income-tax/adjustable-summary-calculation/23-24/$nino/$bsasId"

        willPut(url = url, body = nonFHLBody) returns Future.successful(outcome)

        await(connector.submitPropertyBsas(tysRequest)) shouldBe outcome
      }

    }
  }
}
