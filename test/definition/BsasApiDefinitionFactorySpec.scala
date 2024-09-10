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

import cats.implicits.catsSyntaxValidatedId
import shared.config.Deprecation.NotDeprecated
import shared.config.MockAppConfig
import shared.definition.APIStatus.BETA
import shared.definition._
import shared.mocks.MockHttpClient
import shared.routing.{Version3, Version4, Version5, Version6}
import shared.utils.UnitSpec

class BsasApiDefinitionFactorySpec extends UnitSpec with MockAppConfig {

  class Test extends MockHttpClient with MockAppConfig {
    MockedAppConfig.apiGatewayContext returns "individuals/self-assessment/adjustable-summary"
    val apiDefinitionFactory = new BsasApiDefinitionFactory(mockAppConfig)
  }

  "definition" when {
    "called" should {
      "return a valid Definition case class" in new Test {
        List(Version3, Version4, Version5, Version6).foreach { version =>
          MockedAppConfig.apiStatus(version) returns "BETA"
          MockedAppConfig.endpointsEnabled(version).returns(true).anyNumberOfTimes()
          MockedAppConfig.deprecationFor(version).returns(NotDeprecated.valid).anyNumberOfTimes()
        }

        apiDefinitionFactory.definition shouldBe
          Definition(
            api = APIDefinition(
              name = "Business Source Adjustable Summary (MTD)",
              description = "An API for providing business source adjustable summary data",
              context = "individuals/self-assessment/adjustable-summary",
              categories = List("INCOME_TAX_MTD"),
              versions = List(
                APIVersion(
                  Version4,
                  status = BETA,
                  endpointsEnabled = true
                ),
                APIVersion(
                  Version5,
                  status = BETA,
                  endpointsEnabled = true
                ),
                APIVersion(
                  Version6,
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

}
