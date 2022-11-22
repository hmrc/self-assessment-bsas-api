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
import v3.fixtures.TriggerBsasRequestBodyFixtures._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.triggerBsas.TriggerBsasRequest
import v3.models.response.TriggerBsasResponse
import v3.models.domain.TaxYear

import scala.concurrent.Future

class TriggerBsasConnectorSpec extends ConnectorSpec {

  val nino: Nino    = Nino("AA123456A")
  val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  trait Test {
    _: ConnectorTest =>
    val connector: TriggerBsasConnector = new TriggerBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)
  }

  "triggerBsas" must {

    "post a TriggerBsasRequest body and return the result" in new IfsTest with Test {
      val request = TriggerBsasRequest(nino, model)
      val outcome = Right(ResponseWrapper(correlationId, TriggerBsasResponse(calculationId)))

      willPost(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}",
        body = model
      ).returns(Future.successful(outcome))

      await(connector.triggerBsas(request)) shouldBe outcome
    }

    "post a TriggerBsasRequest body and return the result given a TYS tax year" in new TysIfsTest with Test {
      val request = TriggerBsasRequest(nino, tysModel)
      val outcome = Right(ResponseWrapper(correlationId, TriggerBsasResponse(calculationId)))

      val taxYear: TaxYear = TaxYear.fromIso("2023-05-02")

      willPost(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/${nino.nino}",
        body = tysModel
      ).returns(Future.successful(outcome))

      await(connector.triggerBsas(request)) shouldBe outcome
    }

  }
}
