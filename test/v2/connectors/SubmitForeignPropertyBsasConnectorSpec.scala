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

package v2.connectors

import mocks.MockAppConfig
import uk.gov.hmrc.domain.Nino
import v2.fixtures.selfEmployment.AdditionsFixture.additionsModel
import v2.fixtures.selfEmployment.ExpensesFixture.expensesModel
import v2.fixtures.selfEmployment.IncomeFixture.incomeModel
import v2.mocks.MockHttpClient
import v2.models.domain.TypeOfBusiness
import v2.models.outcomes.ResponseWrapper
import v2.models.request.submitBsas.foreignProperty.{Expenses, FhlEea, ForeignProperty, Income, SubmitForeignPropertyBsasRequestBody, SubmitForeignPropertyBsasRequestData}
import v2.models.response.SubmitForeignPropertyBsasResponse

import scala.concurrent.Future

class SubmitForeignPropertyBsasConnectorSpec extends ConnectorSpec {

  val nino = Nino("AA123456A")
  val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val submitForeignPropertyBsasRequestBodyModel: SubmitForeignPropertyBsasRequestBody = {
    SubmitForeignPropertyBsasRequestBody(
      Some(ForeignProperty(
        Some(Income(
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12)
        )),
        Some(Expenses(
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12)
        ))
      )),
      Some(FhlEea(
        Some(Income(
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12)
        )),
        Some(Expenses(
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12),
          Some(123.12)
        ))
      ))
    )
  }

  class Test extends MockHttpClient with MockAppConfig {
    val connector: SubmitForeignPropertyBsasConnector = new SubmitForeignPropertyBsasConnector(http = mockHttpClient, appConfig = mockAppConfig)


    val desRequestHeaders: Seq[(String,String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnvironment returns "des-environment"
  }

  "submitBsas" must {
    val request = SubmitForeignPropertyBsasRequestData(nino, bsasId, submitForeignPropertyBsasRequestBodyModel)

    "post a SubmitBsasRequest body and return the result" in new Test {
      val outcome = Right(ResponseWrapper(correlationId, SubmitForeignPropertyBsasResponse(bsasId, TypeOfBusiness.`foreign-property`)))

      MockedHttpClient.post(
        url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$bsasId",
        body = submitForeignPropertyBsasRequestBodyModel,
        requiredHeaders = "Environment" -> "des-environment", "Authorization" -> s"Bearer des-token"
      ).returns(Future.successful(outcome))

      await(connector.submitForeignPropertyBsas(request)) shouldBe outcome
    }
  }
}
