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

import api.mocks.MockHttpClient
import config.{ConfidenceLevelConfig, MockAppConfig}
import definition.APIStatus.{ALPHA, BETA}
import routing.{Version3, Version4}
import support.UnitSpec
import uk.gov.hmrc.auth.core.ConfidenceLevel

class ApiDefinitionFactorySpec extends UnitSpec with MockAppConfig {

  private val confidenceLevel: ConfidenceLevel = ConfidenceLevel.L200

  class Test extends MockHttpClient with MockAppConfig {
    val apiDefinitionFactory = new ApiDefinitionFactory(mockAppConfig)
    MockedAppConfig.apiGatewayContext returns "individuals/self-assessment/adjustable-summary"
  }

  "definition" when {
    "called" should {
      "return a valid Definition case class" in new Test {
        MockedAppConfig.apiStatus(Version3) returns "BETA"
        MockedAppConfig.apiStatus(Version4) returns "BETA"
        MockedAppConfig.endpointsEnabled(version = Version3).returns(true).anyNumberOfTimes()
        MockedAppConfig.endpointsEnabled(version = Version4).returns(true).anyNumberOfTimes()

        MockedAppConfig.confidenceLevelCheckEnabled
          .returns(ConfidenceLevelConfig(confidenceLevel = confidenceLevel, definitionEnabled = true, authValidationEnabled = true))
          .anyNumberOfTimes()

        private val readScope  = "read:self-assessment"
        private val writeScope = "write:self-assessment"

        apiDefinitionFactory.definition shouldBe
          Definition(
            scopes = List(
              Scope(
                key = readScope,
                name = "View your Self Assessment information",
                description = "Allow read access to self assessment data",
                confidenceLevel
              ),
              Scope(
                key = writeScope,
                name = "Change your Self Assessment information",
                description = "Allow write access to self assessment data",
                confidenceLevel
              )
            ),
            api = APIDefinition(
              name = "Business Source Adjustable Summary (MTD)",
              description = "An API for providing business source adjustable summary data",
              context = "individuals/self-assessment/adjustable-summary",
              categories = Seq("INCOME_TAX_MTD"),
              versions = Seq(
                APIVersion(
                  Version3,
                  status = BETA,
                  endpointsEnabled = true
                ),
                APIVersion(
                  Version4,
                  status = BETA,
                  endpointsEnabled = true
                )
              ),
              requiresTrust = None
            )
          )
      }
    }
  }

  "confidenceLevel" when {
    Seq(
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
          apiDefinitionFactory.buildAPIStatus(version) shouldBe status
        }
      }
    }

    "the 'apiStatus' parameter is present and invalid" should {
      Seq(Version3, Version4).foreach { version =>
        s"default to alpha for $version " in new Test {
          MockedAppConfig.apiStatus(version) returns "ALPHO"
          apiDefinitionFactory.buildAPIStatus(version) shouldBe ALPHA
        }
      }
    }
  }

}
