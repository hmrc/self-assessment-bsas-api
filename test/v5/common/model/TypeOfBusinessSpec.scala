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

package v5.common.model

import shared.utils.UnitSpec
import shared.utils.enums.EnumJsonSpecSupport
import v5.common.model.TypeOfBusiness.*

class TypeOfBusinessSpec extends UnitSpec with EnumJsonSpecSupport {

  testRoundTrip[TypeOfBusiness](
    ("self-employment", `self-employment`),
    ("uk-property-fhl", `uk-property-fhl`),
    ("uk-property-non-fhl", `uk-property-non-fhl`),
    ("foreign-property-fhl-eea", `foreign-property-fhl-eea`),
    ("foreign-property", `foreign-property`)
  )

  "toIdentifierValue" should {
    "return the correct identifier value" in {
      TypeOfBusiness.`self-employment`.asDownstreamValue shouldBe "01"
      TypeOfBusiness.`uk-property-non-fhl`.asDownstreamValue shouldBe "02"
      TypeOfBusiness.`uk-property-fhl`.asDownstreamValue shouldBe "04"
      TypeOfBusiness.`foreign-property-fhl-eea`.asDownstreamValue shouldBe "03"
      TypeOfBusiness.`foreign-property`.asDownstreamValue shouldBe "15"
    }
  }

}
