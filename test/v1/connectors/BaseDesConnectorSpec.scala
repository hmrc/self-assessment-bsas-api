/*
 * Copyright 2021 HM Revenue & Customs
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

import config.AppConfig
import mocks.MockAppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}
import v1.mocks.MockHttpClient
import v1.models.outcomes.ResponseWrapper

import scala.concurrent.Future

class BaseDesConnectorSpec extends ConnectorSpec {

  // WLOG
  case class Result(value: Int)

  // WLOG
  val body = "body"

  val outcome = Right(ResponseWrapper(correlationId, Result(2)))

  val url = "some/url?param=value"
  val absoluteUrl = s"$baseUrl/$url"

  implicit val httpReads: HttpReads[DesOutcome[Result]] = mock[HttpReads[DesOutcome[Result]]]

  class Test extends MockHttpClient with MockAppConfig {
    val connector: BaseDesConnector = new BaseDesConnector {
      val http: HttpClient = mockHttpClient
      val appConfig: AppConfig = mockAppConfig
    }

    val desRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")

    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnv returns "des-environment"
    MockedAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
  }

  "post" must {
    "posts with the required des headers and returns the result" in new Test {
      MockedHttpClient
        .post(absoluteUrl, dummyDesHeaderCarrierConfig, body, desRequestHeaders)
        .returns(Future.successful(outcome))

      await(connector.post(body, DesUri[Result](url))) shouldBe outcome
    }
  }

  "get" must {
    "get with the required des headers and return the result" in new Test {
      MockedHttpClient
        .get(absoluteUrl,  dummyDesHeaderCarrierConfig, desRequestHeaders)
        .returns(Future.successful(outcome))

      await(connector.get(DesUri[Result](url))) shouldBe outcome
    }
  }

  "parameterGet" must {
    "get with the required des headers and return the result" in new Test {
      MockedHttpClient
        .parameterGet(absoluteUrl, dummyDesHeaderCarrierConfig, Seq("param" -> "value"), desRequestHeaders)
        .returns(Future.successful(outcome))

      await(connector.get(DesUri[Result](url), Seq("param" -> "value"))) shouldBe outcome
    }
  }
}
