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

package definition

import config.{ConfidenceLevelConfig, MockAppConfig}
import definition.APIStatus.{ALPHA, BETA}
import play.api.Configuration
import routing.{Version2, Version3}
import support.UnitSpec
import uk.gov.hmrc.auth.core.ConfidenceLevel

class ApiDefinitionFactorySpec extends UnitSpec with MockAppConfig {

  class Test {
    val factory = new ApiDefinitionFactory(mockAppConfig)
  }

  private val confidenceLevel: ConfidenceLevel = ConfidenceLevel.L200

  "definition" when {
    "there is no appConfig.apiStatus" should {
      "default apiStatus to ALPHA" in new Test {
        MockedAppConfig.apiGatewayContext.returns("my/context")
        MockedAppConfig.featureSwitches.returns(Configuration.empty).anyNumberOfTimes()
        MockedAppConfig.apiStatus(Version2).returns("").anyNumberOfTimes()
        MockedAppConfig.apiStatus(Version3).returns("").anyNumberOfTimes()
        MockedAppConfig.endpointsEnabled(version = Version2.configName).returns(true).anyNumberOfTimes()
        MockedAppConfig.endpointsEnabled(version = Version3.configName).returns(true).anyNumberOfTimes()
        MockedAppConfig.confidenceLevelCheckEnabled
          .returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = true))
          .anyNumberOfTimes()

        factory.definition shouldBe Definition(
          scopes = List(
            Scope(
              key = "read:self-assessment",
              name = "View your Self Assessment information",
              description = "Allow read access to self assessment data",
              confidenceLevel
            ),
            Scope(
              key = "write:self-assessment",
              name = "Change your Self Assessment information",
              description = "Allow write access to self assessment data",
              confidenceLevel
            )
          ),
          api = APIDefinition(
            name = "Individual Losses (MTD)",
            description = "An API for providing individual losses data",
            context = "my/context",
            versions = List(
              APIVersion(Version2, status = ALPHA, endpointsEnabled = true),
              APIVersion(Version3, status = ALPHA, endpointsEnabled = true)
            ),
            requiresTrust = None
          )
        )
      }
    }
  }

  "confidenceLevel" when {
    Seq(
      (true, ConfidenceLevel.L200),
      (false, ConfidenceLevel.L50)
    ).foreach {
      case (definitionEnabled, cl) =>
        s"confidence-level-check.definition.enabled is $definitionEnabled in config" should {
          s"return $cl" in new Test {
            MockedAppConfig.confidenceLevelCheckEnabled returns ConfidenceLevelConfig(definitionEnabled = definitionEnabled,
                                                                                      authValidationEnabled = true)
            factory.confidenceLevel shouldBe cl
          }
        }
    }
  }

  "buildAPIStatus" when {
    val anyVersion = Version3
    "the 'apiStatus' parameter is present and valid" should {
      "return the correct status" in new Test {
        MockedAppConfig.apiStatus(version = anyVersion) returns "BETA"
        factory.buildAPIStatus(version = anyVersion) shouldBe BETA
      }
    }

    "the 'apiStatus' parameter is present and invalid" should {
      "default to alpha" in new Test {
        MockedAppConfig.apiStatus(version = anyVersion) returns "ALPHO"
        factory.buildAPIStatus(version = anyVersion) shouldBe ALPHA
      }
    }
  }
}
