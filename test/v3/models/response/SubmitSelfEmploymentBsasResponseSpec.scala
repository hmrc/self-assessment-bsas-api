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

package v3.models.response

import mocks.MockAppConfig
import support.UnitSpec
import v3.models.hateoas.Link
import v3.models.hateoas.Method

class SubmitSelfEmploymentBsasResponseSpec extends UnitSpec with MockAppConfig {

  "LinksFactory" should {
    "produce the correct links" when {
      "called" in {
        val data: SubmitSelfEmploymentBsasHateoasData = SubmitSelfEmploymentBsasHateoasData("mynino", "mycalcid")

        MockedAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        SubmitSelfEmploymentBsasResponse.SubmitSelfEmploymentAdjustmentHateoasFactory.links(mockAppConfig, data) shouldBe Seq(
          Link(href = s"/my/context/${data.nino}/self-employment/${data.calculationId}", method = Method.GET, rel = "self")
        )
      }
    }
  }
}