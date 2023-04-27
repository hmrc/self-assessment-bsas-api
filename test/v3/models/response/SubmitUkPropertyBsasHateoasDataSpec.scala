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

package v3.models.response

import api.hateoas.Method.GET
import api.hateoas.{ HateoasFactory, HateoasWrapper, Link }
import api.models.domain.TaxYear
import mocks.MockAppConfig
import play.api.Configuration
import support.UnitSpec

class SubmitUkPropertyBsasHateoasDataSpec extends UnitSpec {

  "HateoasFactory" must {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino           = "someNino"
      val calcId         = "anId"
      val taxYear        = Some(TaxYear.fromMtd("2023-24"))
      val context        = "individuals/self-assessment/adjustable-summary"

      MockedAppConfig.apiGatewayContext.returns(context).anyNumberOfTimes
    }

    class TysDisabledTest extends Test {
      MockedAppConfig.featureSwitches.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()
    }

    class TysEnabledTest extends Test {
      MockedAppConfig.featureSwitches.returns(Configuration("tys-api.enabled" -> true)).anyNumberOfTimes()
    }

    "return the correct links without tax year" in new TysDisabledTest {
      private val result       = hateoasFactory.wrap((), SubmitUkPropertyBsasHateoasData(nino, calcId, None))
      private val expectedLink = Link(s"/$context/$nino/uk-property/$calcId", GET, "self")

      result shouldBe HateoasWrapper((), Seq(expectedLink))
    }

    "return the correct links with TYS enabled and the tax year is TYS" in new TysEnabledTest {
      private val result       = hateoasFactory.wrap((), SubmitUkPropertyBsasHateoasData(nino, calcId, taxYear))
      private val expectedLink = Link(s"/$context/$nino/uk-property/$calcId?taxYear=2023-24", GET, "self")

      result shouldBe HateoasWrapper((), Seq(expectedLink))
    }
  }

}
