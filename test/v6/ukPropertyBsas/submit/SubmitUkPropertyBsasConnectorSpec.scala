/*
 * Copyright 2023 HM Revenue & Customs
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

package v6.ukPropertyBsas.submit

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v6.ukPropertyBsas.submit.def3.model.request.Def3_SubmitUkPropertyBsasRequestData
import v6.ukPropertyBsas.submit.def3.model.request.SubmitUKPropertyBsasRequestBodyFixtures._
import v6.ukPropertyBsas.submit.model.request.SubmitUkPropertyBsasRequestData

import scala.concurrent.Future

class SubmitUkPropertyBsasConnectorSpec extends ConnectorSpec {

  private val nino          = Nino("AA123456A")
  private val calculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")
  private val nonTysRequest = makeRequest("2022-23")
  private val tysRequest    = makeRequest("2023-24")

  def makeRequest(taxYear: String): SubmitUkPropertyBsasRequestData = {
    Def3_SubmitUkPropertyBsasRequestData(
      nino = nino,
      calculationId = calculationId,
      taxYear = TaxYear.fromMtd(taxYear),
      body = requestFullParsed
    )
  }

  trait Test {
    _: ConnectorTest =>

    protected val connector: SubmitUkPropertyBsasConnector = new SubmitUkPropertyBsasConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

  }

  "SubmitUKPropertyBsasConnector" when {
    "SubmitUKPropertyBsas" must {

      "post a SubmitBsasRequest body and return the result for the non-TYS scenario" in new IfsTest with Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))
        val url     = s"$baseUrl/income-tax/adjustable-summary-calculation/$nino/$calculationId"
        willPut(url = url, body = requestFullParsed) returns Future.successful(outcome)

        val result: DownstreamOutcome[Unit] = await(connector.submitPropertyBsas(nonTysRequest))
        result shouldBe outcome
      }

      "post a SubmitBsasRequest body and return the result for the TYS scenario" in new TysIfsTest with Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))
        val url     = s"$baseUrl/income-tax/adjustable-summary-calculation/23-24/$nino/$calculationId"
        willPut(url = url, body = requestFullParsed) returns Future.successful(outcome)

        val result: DownstreamOutcome[Unit] = await(connector.submitPropertyBsas(tysRequest))
        result shouldBe outcome
      }

      "post a SubmitBsasRequest body and return the result for the TYS scenario on HIP" in new HipTest with Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))
        val url     = s"$baseUrl/income-tax/v1/23-24/adjustable-summary-calculation/$nino/$calculationId"
        willPut(url = url, body = requestFullParsed) returns Future.successful(outcome)

        val result: DownstreamOutcome[Unit] = await(connector.submitPropertyBsas(tysRequest))
        result shouldBe outcome
      }

    }
  }

}
