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

package v4.connectors

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v4.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures.retrieveBsasResponseFhlModel
import v4.models.request.retrieveBsas.RetrieveUkPropertyBsasRequestData
import v4.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse

import scala.concurrent.Future

class RetrieveUkPropertyBsasConnectorSpec extends ConnectorSpec {

  private val nino          = Nino("AA123456A")
  private val calculationId = CalculationId("717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4")

  trait Test {
    _: ConnectorTest =>
    val connector: RetrieveUkPropertyBsasConnector = new RetrieveUkPropertyBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)
  }

  "retrieve" should {
    "return a valid response" when {
      val outcome = Right(ResponseWrapper(correlationId, retrieveBsasResponseFhlModel))

      "a valid request is supplied for a non-TYS year" in new IfsTest with Test {
        private val request     = RetrieveUkPropertyBsasRequestData(nino, calculationId, taxYear = None)
        private val expectedUrl = s"$baseUrl/income-tax/adjustable-summary-calculation/$nino/$calculationId"
        willGet(expectedUrl) returns Future.successful(outcome)

        val result: DownstreamOutcome[RetrieveUkPropertyBsasResponse] = await(connector.retrieve(request))
        result shouldBe outcome
      }

      "a valid request with queryParams is supplied for a TYS year" in new TysIfsTest with Test {
        private def taxYear: TaxYear = TaxYear.fromMtd("2023-24")
        private val request          = RetrieveUkPropertyBsasRequestData(nino, calculationId, Some(taxYear))
        willGet(s"$baseUrl/income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino/$calculationId") returns Future
          .successful(outcome)

        val result: DownstreamOutcome[RetrieveUkPropertyBsasResponse] = await(connector.retrieve(request))
        result shouldBe outcome
      }
    }
  }

}
