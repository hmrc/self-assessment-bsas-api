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

package v4.models.response

import play.api.Configuration
import shared.config.MockSharedAppConfig
import shared.hateoas.Link
import shared.hateoas.Method.GET
import shared.models.domain.TaxYear
import shared.utils.UnitSpec

class SubmitSelfEmploymentBsasResponseSpec extends UnitSpec with MockSharedAppConfig {

  class Test extends MockSharedAppConfig {
    val nino          = "someNino"
    val calculationId = "anId"
    val taxYear       = Some(TaxYear.fromMtd("2023-24"))
    val context       = "individuals/self-assessment/adjustable-summary"

    MockedSharedAppConfig.apiGatewayContext.returns(context).anyNumberOfTimes()
  }

  class TysDisabledTest extends Test {
    MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()
  }

  class TysEnabledTest extends Test {
    MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("tys-api.enabled" -> true)).anyNumberOfTimes()
  }

  "LinksFactory" should {
    "return the correct links without tax year" in new TysDisabledTest {
      private val data         = SubmitSelfEmploymentBsasHateoasData(nino, calculationId, None)
      private val expectedLink = Link(href = s"/$context/$nino/self-employment/$calculationId", method = GET, rel = "self")

      val result: Seq[Link] =
        SubmitSelfEmploymentBsasResponse.SubmitSelfEmploymentAdjustmentHateoasFactory.links(mockSharedAppConfig, data)

      result shouldBe Seq(expectedLink)
    }

    "return the correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
      private val data         = SubmitSelfEmploymentBsasHateoasData(nino, calculationId, taxYear)
      private val result       = SubmitSelfEmploymentBsasResponse.SubmitSelfEmploymentAdjustmentHateoasFactory.links(mockSharedAppConfig, data)
      private val expectedLink = Link(href = s"/$context/$nino/self-employment/$calculationId?taxYear=2023-24", method = GET, rel = "self")

      result shouldBe Seq(expectedLink)
    }
  }

}
