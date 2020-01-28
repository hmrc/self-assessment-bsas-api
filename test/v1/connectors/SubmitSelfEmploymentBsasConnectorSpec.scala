/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.connectors

import mocks.MockAppConfig
import uk.gov.hmrc.domain.Nino
import v1.fixtures.selfEmployment.AdditionsFixture.additionsModel
import v1.fixtures.selfEmployment.ExpensesFixture.expensesModel
import v1.fixtures.selfEmployment.IncomeFixture.incomeModel
import v1.mocks.MockHttpClient
import v1.models.domain.TypeOfBusiness
import v1.models.outcomes.ResponseWrapper
import v1.models.request.submitBsas.selfEmployment.{SubmitSelfEmploymentBsasRequestBody, SubmitSelfEmploymentBsasRequestData}
import v1.models.response.SubmitSelfEmploymentBsasResponse

import scala.concurrent.Future

class SubmitSelfEmploymentBsasConnectorSpec extends ConnectorSpec {

  val submitSelfEmploymentBsasRequestBodyModel: SubmitSelfEmploymentBsasRequestBody =
    SubmitSelfEmploymentBsasRequestBody(
      income = Some(incomeModel),
      additions = Some(additionsModel),
      expenses = Some(expensesModel)
    )

  val nino = Nino("AA123456A")
  val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  class Test extends MockHttpClient with MockAppConfig {
    val connector: SubmitSelfEmploymentBsasConnector = new SubmitSelfEmploymentBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)


    val desRequestHeaders: Seq[(String,String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnvironment returns "des-environment"
  }

  "submitBsas" must {
    val request = SubmitSelfEmploymentBsasRequestData(nino, bsasId, submitSelfEmploymentBsasRequestBodyModel)

    "post a SubmitBsasRequest body and return the result" in new Test {
      val outcome = Right(ResponseWrapper(correlationId, SubmitSelfEmploymentBsasResponse(bsasId, TypeOfBusiness.`self-employment`)))

      MockedHttpClient.put(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$bsasId",
        body = submitSelfEmploymentBsasRequestBodyModel,
        requiredHeaders = "Environment" -> "des-environment", "Authorization" -> s"Bearer des-token"
      ).returns(Future.successful(outcome))

      await(connector.submitSelfEmploymentBsas(request)) shouldBe outcome
    }
  }
}
