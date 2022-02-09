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
import v3.hateoas.HateoasFactory
import v3.models.hateoas.Method.GET
import v3.models.hateoas.{HateoasWrapper, Link}

class SubmitUkPropertyBsasHateoasDataSpec extends UnitSpec {

  "HateoasFactory" must {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino           = "someNino"
      val calcId         = "anId"
      MockedAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary").anyNumberOfTimes
    }

    "expose the correct links for a response from Submit a Property Summary Adjustment" in new Test {
      hateoasFactory.wrap((), SubmitUkPropertyBsasHateoasData(nino, calcId)) shouldBe
        HateoasWrapper((),
                       Seq(
                         Link(s"/individuals/self-assessment/adjustable-summary/$nino/property/$calcId", GET, "self")
                       ))
    }
  }

}
