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

package v2.connectors

import config.AppConfig
import mocks.MockAppConfig
import uk.gov.hmrc.http.{HttpClient, HttpReads}
import v2.mocks.MockHttpClient
import v2.models.outcomes.ResponseWrapper

import scala.concurrent.Future

class BaseDownstreamConnectorSpec extends ConnectorSpec {

  // WLOG
  case class Result(value: Int)

  // WLOG
  val body = "body"

  val outcome = Right(ResponseWrapper(correlationId, Result(2)))

  val url = "some/url?param=value"
  val absoluteUrl = s"$baseUrl/$url"

  implicit val httpReads: HttpReads[DownstreamOutcome[Result]] = mock[HttpReads[DownstreamOutcome[Result]]]

  class DesTest extends MockHttpClient with MockAppConfig {
    val connector: BaseDownstreamConnector = new BaseDownstreamConnector {
      val http: HttpClient = mockHttpClient
      val appConfig: AppConfig = mockAppConfig
    }

    val desRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")

    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnv returns "des-environment"
    MockedAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
    MockedAppConfig.ifsEnabled returns false
  }

  class IFSTest extends MockHttpClient with MockAppConfig {
    val connector: BaseDownstreamConnector = new BaseDownstreamConnector {
      val http: HttpClient = mockHttpClient
      val appConfig: AppConfig = mockAppConfig
    }

    val ifsRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "ifs-environment", "Authorization" -> s"Bearer ifs-token")

    MockedAppConfig.ifsBaseUrl returns baseUrl
    MockedAppConfig.ifsToken returns "ifs-token"
    MockedAppConfig.ifsEnv returns "ifs-environment"
    MockedAppConfig.ifsEnvironmentHeaders returns Some(allowedDesHeaders)
    MockedAppConfig.ifsEnabled returns true
  }

  "post" must {
    "posts with the required des headers and returns the result" in new DesTest {
      MockedHttpClient
        .post(absoluteUrl, dummyDesHeaderCarrierConfig, body, desRequestHeaders)
        .returns(Future.successful(outcome))

      await(connector.post(body, DownstreamUri[Result](url))) shouldBe outcome
    }

    "posts with the required ifs headers and returns the result" in new IFSTest {
      MockedHttpClient
        .post(absoluteUrl, dummyDesHeaderCarrierConfig, body, ifsRequestHeaders)
        .returns(Future.successful(outcome))

      await(connector.post(body, DownstreamUri[Result](url))) shouldBe outcome
    }
  }

  "get" must {
    "get with the required des headers and return the result" in new DesTest {
      MockedHttpClient
        .get(absoluteUrl,  dummyDesHeaderCarrierConfig, desRequestHeaders)
        .returns(Future.successful(outcome))

      await(connector.get(DownstreamUri[Result](url))) shouldBe outcome
    }

    "get with the required ifs headers and return the result" in new IFSTest {
      MockedHttpClient
        .get(absoluteUrl, dummyDesHeaderCarrierConfig, ifsRequestHeaders)
        .returns(Future.successful(outcome))

      await(connector.get(DownstreamUri[Result](url))) shouldBe outcome
    }
  }

  "parameterGet" must {
    "get with the required des headers and return the result" in new DesTest {
      MockedHttpClient
        .parameterGet(absoluteUrl, dummyDesHeaderCarrierConfig, Seq("param" -> "value"), desRequestHeaders)
        .returns(Future.successful(outcome))

      await(connector.get(DownstreamUri[Result](url), Seq("param" -> "value"))) shouldBe outcome
    }

    "get with the required ifs headers and return the result" in new IFSTest {
      MockedHttpClient
        .parameterGet(absoluteUrl, dummyDesHeaderCarrierConfig, Seq("param" -> "value"), ifsRequestHeaders)
        .returns(Future.successful(outcome))

      await(connector.get(DownstreamUri[Result](url), Seq("param" -> "value"))) shouldBe outcome
    }
  }
}
