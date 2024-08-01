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

package v6.common.model

import shared.utils.UnitSpec
import shared.utils.enums.EnumJsonSpecSupport
import v6.common.model.TypeOfBusinessWithFHL._

class TypeOfBusinessWithFHLSpec extends UnitSpec with EnumJsonSpecSupport {

  testRoundTrip[TypeOfBusinessWithFHL](
    ("uk-property-fhl", `uk-property-fhl`),
    ("uk-property-non-fhl", `uk-property-non-fhl`),
    ("foreign-property-fhl-eea", `foreign-property-fhl-eea`)
  )

  "toIdentifierValue" should {
    "return the correct identifier value" in {
      TypeOfBusinessWithFHL.`uk-property-non-fhl`.asDownstreamValue shouldBe "02"
      TypeOfBusinessWithFHL.`uk-property-fhl`.asDownstreamValue shouldBe "04"
      TypeOfBusinessWithFHL.`foreign-property-fhl-eea`.asDownstreamValue shouldBe "03"
    }
  }

}
