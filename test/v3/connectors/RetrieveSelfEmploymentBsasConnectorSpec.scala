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

package v3.connectors

import api.connectors.ConnectorSpec
import api.models.domain.{ CalculationId, Nino, TaxYear }
import api.models.outcomes.ResponseWrapper
import v3.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures._
import v3.models.request.retrieveBsas.RetrieveSelfEmploymentBsasRequestData

import scala.concurrent.Future

class RetrieveSelfEmploymentBsasConnectorSpec extends ConnectorSpec {

  private val nino          = Nino("AA123456A")
  private val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  trait Test {
    _: ConnectorTest =>

    val connector: RetrieveSelfEmploymentBsasConnector = new RetrieveSelfEmploymentBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)

    def requestWith(taxYear: Option[TaxYear]): RetrieveSelfEmploymentBsasRequestData =
      RetrieveSelfEmploymentBsasRequestData(nino, CalculationId(calculationId), taxYear)
  }

  "RetrieveSelfEmploymentBsasConnectorSpec" when {
    "retrieveSelfEmploymentBsas is called" must {
      "a valid request is supplied" in {
        new IfsTest with Test {
          val outcome     = Right(ResponseWrapper(correlationId, mtdRetrieveBsasResponseJson))
          val expectedUrl = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$calculationId"
          willGet(url = expectedUrl) returns Future.successful(outcome)
          await(connector.retrieveSelfEmploymentBsas(requestWith(None))) shouldBe outcome
        }
      }
    }

    "retrieveSelfEmploymentBsas is called for a TaxYearSpecific tax year" must {
      "a valid request is supplied" in {
        new TysIfsTest with Test {
          val taxYear     = TaxYear.fromMtd("2023-24")
          val outcome     = Right(ResponseWrapper(correlationId, mtdRetrieveBsasResponseJson))
          val expectedUrl = s"$baseUrl/income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/${nino.nino}/$calculationId"

          willGet(url = expectedUrl) returns Future.successful(outcome)

          await(connector.retrieveSelfEmploymentBsas(requestWith(Some(taxYear)))) shouldBe outcome
        }
      }
    }
  }
}
