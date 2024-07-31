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

package v6.foreignPropertyBsas.retrieve

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v5.foreignPropertyBsas.retrieve.def1.model.request.Def1_RetrieveForeignPropertyBsasRequestData
import v5.foreignPropertyBsas.retrieve.def1.model.response.RetrieveForeignPropertyBsasBodyFixtures._
import v5.foreignPropertyBsas.retrieve.model.request.RetrieveForeignPropertyBsasRequestData
import v5.foreignPropertyBsas.retrieve.model.response.RetrieveForeignPropertyBsasResponse

import scala.concurrent.Future

class RetrieveForeignPropertyBsasConnectorSpec extends ConnectorSpec {

  private val nino          = Nino("AA123456A")
  private val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  trait Test {
    _: ConnectorTest =>
    val connector: RetrieveForeignPropertyBsasConnector = new RetrieveForeignPropertyBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)
  }

  "retrieveForeignPropertyBsas" should {
    "return a valid response" when {
      val outcome = Right(ResponseWrapper(correlationId, parsedNonFhlRetrieveForeignPropertyBsasResponse))

      "a valid request is supplied for a non-TYS year" in new IfsTest with Test {
        val request: RetrieveForeignPropertyBsasRequestData =
          Def1_RetrieveForeignPropertyBsasRequestData(nino, CalculationId(calculationId), taxYear = None)

        val expectedUrl = s"$baseUrl/income-tax/adjustable-summary-calculation/$nino/$calculationId"
        willGet(url = expectedUrl) returns Future.successful(outcome)

        await(connector.retrieveForeignPropertyBsas(request)) shouldBe outcome
      }

      "a valid request with queryParams is supplied for a TYS year" in new TysIfsTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2023-24")

        val request: RetrieveForeignPropertyBsasRequestData =
          Def1_RetrieveForeignPropertyBsasRequestData(nino, CalculationId(calculationId), Some(taxYear))

        willGet(s"$baseUrl/income-tax/adjustable-summary-calculation/23-24/$nino/$calculationId") returns Future.successful(outcome)

        val result: DownstreamOutcome[RetrieveForeignPropertyBsasResponse] = await(connector.retrieveForeignPropertyBsas(request))
        result shouldBe outcome
      }
    }
  }

}
