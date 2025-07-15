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
import shared.config.MockSharedAppConfig
import shared.definition.APIStatus.BETA
import shared.definition.*
import shared.mocks.MockHttpClient
import shared.routing.{Version5, Version6, Version7}
import shared.utils.UnitSpec

class BsasApiDefinitionFactorySpec extends UnitSpec with MockSharedAppConfig {


  "definition" when {
    "called" should {
      "return a valid Definition case class" in {
        MockedSharedAppConfig.apiGatewayContext returns "individuals/reliefs"

        List(Version5, Version6, Version7).foreach { version =>
          MockedSharedAppConfig.apiStatus(version) returns "BETA"
          MockedSharedAppConfig.endpointsEnabled(version).returns(true).anyNumberOfTimes()
          MockedSharedAppConfig.deprecationFor(version).returns(NotDeprecated.valid).anyNumberOfTimes()
        }

        val apiDefinitionFactory = new BsasApiDefinitionFactory(mockSharedAppConfig)

        apiDefinitionFactory.definition shouldBe
          Definition(
            api = APIDefinition(
              name = "Business Source Adjustable Summary (MTD)",
              description = "An API for providing business source adjustable summary data",
              context = "individuals/self-assessment/adjustable-summary",
              categories = List("INCOME_TAX_MTD"),
              versions = List(
                APIVersion(
                  Version5,
                  status = BETA,
                  endpointsEnabled = true
                ),
                APIVersion(
                  Version6,
                  status = BETA,
                  endpointsEnabled = true
                ),
                APIVersion(
                  Version7,
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
