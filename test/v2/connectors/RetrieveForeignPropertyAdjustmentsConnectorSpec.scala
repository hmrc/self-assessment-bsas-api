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

package v2.connectors

import mocks.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v2.mocks.MockHttpClient
import v2.models.outcomes.ResponseWrapper
import v2.models.request.RetrieveAdjustmentsRequestData

import scala.concurrent.Future

class RetrieveForeignPropertyAdjustmentsConnectorSpec extends ConnectorSpec {

  val nino = Nino("AA123456A")
  val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val queryParams: Map[String, String] = Map("return" -> "2")

  val responseBody: JsValue = Json.parse(
    """
      |{
      |   "metadata": {
      |      "typeOfBusiness": "foreign-property",
      |      "accountingPeriod": {
      |         "StartDate": "2020-10-11",
      |         "EndDate": "2021-10-10"
      |      },
      |      "taxYear": "2020-21",
      |      "requestedDateTime": "2020-10-14T11:33:27Z",
      |      "bsasId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce5",
      |      "summaryStatus": "valid",
      |      "adjustedSummary": "true"
      |   },
      |   "adjustments": {
      |      "incomes": {
      |         "rentIncome": 100.49,
      |         "premiumsOFLeaseGrant": 100.49,
      |         "otherPropertyIncome": 100.49,
      |         "foreignTaxTakenOff": 100.49
      |      },
      |      "expenses": {
      |         "premisesRunningCosts": 100.49,
      |         "repairsAndMaintenance": 100.49,
      |         "financialCosts": 100.49,
      |         "professionalFees": 100.49,
      |         "travelCosts": 100.49,
      |         "costOfServices": 100.49,
      |         "residentialFinancialCost": 100.49,
      |         "other": 100.49,
      |         "consolidatedExpenses": 100.49
      |      }
      |   }
      |}
    """.stripMargin
  )

  class Test extends MockHttpClient with MockAppConfig {
    val connector: RetrieveForeignPropertyAdjustmentsConnector =
      new RetrieveForeignPropertyAdjustmentsConnector( http = mockHttpClient, appConfig = mockAppConfig)

    val desRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnv returns "des-environment"
    MockedAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
    MockedAppConfig.ifsEnabled returns false
  }

  "RetrieveForeignPropertyAdjustments" should {
    "return a valid response" when {
      val outcome = Right(ResponseWrapper(correlationId, responseBody))
      implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders)
      "a valid request with queryParams is supplied" in new Test {
        val request = RetrieveAdjustmentsRequestData(nino, bsasId)

        MockedHttpClient.parameterGet(
          url = s"$baseUrl/income-tax/adjustable-summary-calculation/${nino.nino}/$bsasId",
          config = dummyDesHeaderCarrierConfig,
          queryParams.toSeq,
          requiredHeaders = desRequestHeaders,
          excludedHeaders = Seq("AnotherHeader" -> s"HeaderValue")
        ).returns(Future.successful(outcome))

        await(connector.retrieveForeignPropertyAdjustments(request)) shouldBe outcome
      }
    }
  }
}
