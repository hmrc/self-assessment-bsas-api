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

package shared.definition

import shared.UnitSpec
import shared.config.{AppConfig, ConfidenceLevelConfig, MockAppConfig}
import shared.definition.APIStatus.{ALPHA, BETA}
import shared.mocks.MockHttpClient
import shared.routing.{Version, Version1, Version3, Version4}
import uk.gov.hmrc.auth.core.ConfidenceLevel

class ApiDefinitionFactorySpec extends UnitSpec with MockAppConfig {

  private[definition] class MyApiDefinitionFactory extends ApiDefinitionFactory {
    protected val appConfig: AppConfig = mockAppConfig

    val definition: Definition = Definition(
      Nil,
      APIDefinition(
        "test API definition",
        "description",
        "context",
        List("category"),
        List(APIVersion(Version1, APIStatus.BETA, endpointsEnabled = true)),
        None)
    )

    def checkBuildApiStatus(version: Version): APIStatus = buildAPIStatus(version)
  }

  class Test extends MockHttpClient with MockAppConfig {
    MockedAppConfig.apiGatewayContext returns "individuals/self-assessment/adjustable-summary"

    protected val apiDefinitionFactory = new MyApiDefinitionFactory

    "confidenceLevel" when {
      List(
        (true, ConfidenceLevel.L250, ConfidenceLevel.L250),
        (true, ConfidenceLevel.L200, ConfidenceLevel.L200),
        (false, ConfidenceLevel.L200, ConfidenceLevel.L50)
      ).foreach { case (definitionEnabled, configCL, expectedDefinitionCL) =>
        s"confidence-level-check.definition.enabled is $definitionEnabled and confidence-level = $configCL" should {
          s"return confidence level $expectedDefinitionCL" in new Test {
            MockedAppConfig.confidenceLevelCheckEnabled returns ConfidenceLevelConfig(
              confidenceLevel = configCL,
              definitionEnabled = definitionEnabled,
              authValidationEnabled = true)
            apiDefinitionFactory.confidenceLevel shouldBe expectedDefinitionCL
          }
        }
      }
    }

    "buildAPIStatus" when {
      "the 'apiStatus' parameter is present and valid" should {
        Seq(
          (Version3, BETA),
          (Version4, BETA)
        ).foreach { case (version, status) =>
          s"return the correct $status for $version " in new Test {
            MockedAppConfig.apiStatus(version) returns status.toString
            apiDefinitionFactory.checkBuildApiStatus(version) shouldBe status
          }
        }
      }

      "the 'apiStatus' parameter is present and invalid" should {
        Seq(Version3, Version4).foreach { version =>
          s"default to alpha for $version " in new Test {
            MockedAppConfig.apiStatus(version) returns "ALPHO"
            apiDefinitionFactory.checkBuildApiStatus(version) shouldBe ALPHA
          }
        }
      }
    }

  }

}
