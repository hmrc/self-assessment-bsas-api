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
import api.models.ResponseWrapper
import api.models.domain.{Nino, TaxYear}
import v3.fixtures.selfEmployment.AdditionsFixture.additionsModel
import v3.fixtures.selfEmployment.ExpensesFixture.expensesModel
import v3.fixtures.selfEmployment.IncomeFixture.incomeModel
import v3.models.request.submitBsas.selfEmployment.{SubmitSelfEmploymentBsasRequestBody, SubmitSelfEmploymentBsasRequestData}

import scala.concurrent.Future

class SubmitSelfEmploymentBsasConnectorSpec extends ConnectorSpec {

  val submitSelfEmploymentBsasRequestBodyModel: SubmitSelfEmploymentBsasRequestBody =
    SubmitSelfEmploymentBsasRequestBody(
      income = Some(incomeModel),
      additions = Some(additionsModel),
      expenses = Some(expensesModel)
    )

  val nino: Nino = Nino("AA123456A")
  val bsasId     = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  trait Test { _: ConnectorTest =>
    val connector: SubmitSelfEmploymentBsasConnector = new SubmitSelfEmploymentBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)
  }

  "submitBsas" must {

    "post a SubmitBsasRequest body and return the result" in new IfsTest with Test {
      val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))
      val request: SubmitSelfEmploymentBsasRequestData =
        SubmitSelfEmploymentBsasRequestData(nino, bsasId, None, submitSelfEmploymentBsasRequestBodyModel)

      willPut(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$bsasId",
        body = submitSelfEmploymentBsasRequestBodyModel
      ).returns(Future.successful(outcome))

      await(connector.submitSelfEmploymentBsas(request)) shouldBe outcome
    }

    "post a SubmitBsasRequest body and return the result for a TYS tax year" in new TysIfsTest with Test {
      val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))
      val request: SubmitSelfEmploymentBsasRequestData =
        SubmitSelfEmploymentBsasRequestData(nino, bsasId, Some(TaxYear.fromMtd("2023-24")), submitSelfEmploymentBsasRequestBodyModel)

      willPut(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/23-24/${nino.nino}/$bsasId",
        body = submitSelfEmploymentBsasRequestBodyModel
      ).returns(Future.successful(outcome))

      await(connector.submitSelfEmploymentBsas(request)) shouldBe outcome
    }
  }
}
