/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.common.model

import shared.utils.UnitSpec
import shared.utils.enums.EnumJsonSpecSupport
import v7.common.model.TypeOfBusiness.{`foreign-property`, `self-employment`, `uk-property`}

class TypeOfBusinessSpec extends UnitSpec with EnumJsonSpecSupport {

  testRoundTrip[TypeOfBusiness](
    ("self-employment", `self-employment`),
    ("uk-property", `uk-property`),
    ("foreign-property", `foreign-property`)
  )

  "toIdentifierValue" should {
    "return the correct identifier value" in {
      TypeOfBusiness.`self-employment`.asDownstreamValue shouldBe "01"
      TypeOfBusiness.`uk-property`.asDownstreamValue shouldBe "02"
      TypeOfBusiness.`foreign-property`.asDownstreamValue shouldBe "15"
    }
  }

}
