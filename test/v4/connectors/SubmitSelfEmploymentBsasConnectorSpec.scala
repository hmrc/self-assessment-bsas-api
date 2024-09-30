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
import v4.fixtures.selfEmployment.AdditionsFixture.additionsModel
import v4.fixtures.selfEmployment.ExpensesFixture.expensesModel
import v4.fixtures.selfEmployment.IncomeFixture.incomeModel
import v4.models.request.submitBsas.selfEmployment.{SubmitSelfEmploymentBsasRequestBody, SubmitSelfEmploymentBsasRequestData}

import scala.concurrent.Future

class SubmitSelfEmploymentBsasConnectorSpec extends ConnectorSpec {

  val submitSelfEmploymentBsasRequestBodyModel: SubmitSelfEmploymentBsasRequestBody =
    SubmitSelfEmploymentBsasRequestBody(
      income = Some(incomeModel),
      additions = Some(additionsModel),
      expenses = Some(expensesModel)
    )

  private val nino          = Nino("AA123456A")
  private val calculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")

  trait Test {
    _: ConnectorTest =>
    val connector: SubmitSelfEmploymentBsasConnector = new SubmitSelfEmploymentBsasConnector(http = mockHttpClient, appConfig = mockSharedAppConfig)
  }

  "submitBsas" must {

    "post a SubmitBsasRequest body and return the result" in new IfsTest with Test {
      val request: SubmitSelfEmploymentBsasRequestData =
        SubmitSelfEmploymentBsasRequestData(nino, calculationId, None, submitSelfEmploymentBsasRequestBodyModel)

      val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

      willPut(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/$nino/$calculationId",
        body = submitSelfEmploymentBsasRequestBodyModel
      ).returns(Future.successful(outcome))

      val result: DownstreamOutcome[Unit] = await(connector.submitSelfEmploymentBsas(request))
      result shouldBe outcome
    }

    "post a SubmitBsasRequest body and return the result for a TYS tax year" in new TysIfsTest with Test {
      val request: SubmitSelfEmploymentBsasRequestData =
        SubmitSelfEmploymentBsasRequestData(nino, calculationId, Some(TaxYear.fromMtd("2023-24")), submitSelfEmploymentBsasRequestBodyModel)

      val outcome = Right(ResponseWrapper(correlationId, ()))

      willPut(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/23-24/$nino/$calculationId",
        body = submitSelfEmploymentBsasRequestBodyModel
      ).returns(Future.successful(outcome))

      val result: DownstreamOutcome[Unit] = await(connector.submitSelfEmploymentBsas(request))
      result shouldBe outcome
    }
  }

}
