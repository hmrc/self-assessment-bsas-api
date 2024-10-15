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

package v6.selfEmploymentBsas.retrieve

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v6.selfEmploymentBsas.retrieve.def1.model.Def1_RetrieveSelfEmploymentBsasFixtures._
import v6.selfEmploymentBsas.retrieve.def1.model.request.Def1_RetrieveSelfEmploymentBsasRequestData
import v6.selfEmploymentBsas.retrieve.model.request.RetrieveSelfEmploymentBsasRequestData
import v6.selfEmploymentBsas.retrieve.model.response.RetrieveSelfEmploymentBsasResponse

import scala.concurrent.Future

class RetrieveSelfEmploymentBsasConnectorSpec extends ConnectorSpec {

  private val nino          = Nino("AA123456A")
  private val calculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")

  trait Test {
    _: ConnectorTest =>

    val connector: RetrieveSelfEmploymentBsasConnector =
      new RetrieveSelfEmploymentBsasConnector(http = mockHttpClient, appConfig = mockSharedAppConfig)

    def requestWith(taxYear: TaxYear): RetrieveSelfEmploymentBsasRequestData =
      Def1_RetrieveSelfEmploymentBsasRequestData(nino, calculationId, taxYear)

  }

  "RetrieveSelfEmploymentBsasConnectorSpec" when {
    "retrieveSelfEmploymentBsas is called for Non-TYS" must {
      "a valid request is supplied" in {
        new IfsTest with Test {
          val outcome     = Right(ResponseWrapper(correlationId, mtdRetrieveBsasResponseJson("2019-20")))
          val expectedUrl = s"$baseUrl/income-tax/adjustable-summary-calculation/$nino/$calculationId"
          willGet(url = expectedUrl) returns Future.successful(outcome)

          val result: DownstreamOutcome[RetrieveSelfEmploymentBsasResponse] =
            await(connector.retrieveSelfEmploymentBsas(requestWith(TaxYear.fromMtd("2019-20"))))
          result shouldBe outcome
        }
      }
    }

    "retrieveSelfEmploymentBsas is called for a TaxYearSpecific tax year" must {
      "a valid request is supplied" in {
        new TysIfsTest with Test {
          val taxYear: TaxYear = TaxYear.fromMtd("2023-24")
          val outcome          = Right(ResponseWrapper(correlationId, mtdRetrieveBsasResponseJson("2023-24")))
          val expectedUrl      = s"$baseUrl/income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino/$calculationId"
          willGet(url = expectedUrl) returns Future.successful(outcome)

          val result: DownstreamOutcome[RetrieveSelfEmploymentBsasResponse] = await(connector.retrieveSelfEmploymentBsas(requestWith(taxYear)))
          result shouldBe outcome
        }
      }
    }
  }

}
