/*
 * Copyright 2024 HM Revenue & Customs
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

package shared.connectors

import org.scalatest.concurrent.ScalaFutures
import shared.config.{ClientAuthConfig, DownstreamConfig, SimpleDownstreamConfig}
import shared.utils.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global

class DownstreamStrategySpec extends UnitSpec with ScalaFutures {

  "StandardStrategy" must {
    "use the supplied DownstreamConfig" in {
      val downstreamConfig =
        DownstreamConfig(baseUrl = "someBaseUrl", env = "someEnv", token = "someToken", environmentHeaders = Some(Seq("header1", "header2")))

      val strategy = DownstreamStrategy.standardStrategy(downstreamConfig)

      strategy.baseUrl shouldBe "someBaseUrl"
      strategy.contractHeaders("someCorrelationId").futureValue should contain theSameElementsAs
        Seq(
          "Authorization" -> "Bearer someToken",
          "Environment"   -> "someEnv",
          "CorrelationId" -> "someCorrelationId"
        )
      strategy.environmentHeaders should contain theSameElementsAs Seq("header1", "header2")
    }
  }

  "BasicAuthStrategy" must {
    "use the supplied SimpleDownstreamConfig and ClientAuthConfig" in {
      val downstreamConfig =
        SimpleDownstreamConfig(baseUrl = "someBaseUrl", env = "someEnv", environmentHeaders = Some(Seq("header1", "header2")))
      val basicAuthConfig = ClientAuthConfig(clientId = "someClient", clientSecret = "someSecret")

      val strategy = DownstreamStrategy.basicAuthStrategy(downstreamConfig, basicAuthConfig)

      strategy.baseUrl shouldBe "someBaseUrl"
      strategy.contractHeaders("someCorrelationId").futureValue should contain theSameElementsAs
        Seq(
          "Authorization" -> "Basic c29tZUNsaWVudDpzb21lU2VjcmV0",
          "Environment"   -> "someEnv",
          "CorrelationId" -> "someCorrelationId"
        )
      strategy.environmentHeaders should contain theSameElementsAs Seq("header1", "header2")

    }
  }

}
