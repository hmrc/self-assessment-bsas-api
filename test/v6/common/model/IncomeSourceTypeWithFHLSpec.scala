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
import v6.common.model.IncomeSourceTypeWithFHL._

class IncomeSourceTypeWithFHLSpec extends UnitSpec with EnumJsonSpecSupport {

  testRoundTrip[IncomeSourceTypeWithFHL](
    ("01", `01`),
    ("02", `02`),
    ("04", `04`),
    ("03", `03`),
    ("15", `15`)
  )

  "toIdentifierValue" should {
    "return the correct identifier value" in {
      IncomeSourceTypeWithFHL.`01`.toTypeOfBusiness shouldBe TypeOfBusinessWithFHL.`self-employment`
      IncomeSourceTypeWithFHL.`02`.toTypeOfBusiness shouldBe TypeOfBusinessWithFHL.`uk-property-non-fhl`
      IncomeSourceTypeWithFHL.`04`.toTypeOfBusiness shouldBe TypeOfBusinessWithFHL.`uk-property-fhl`
      IncomeSourceTypeWithFHL.`03`.toTypeOfBusiness shouldBe TypeOfBusinessWithFHL.`foreign-property-fhl-eea`
      IncomeSourceTypeWithFHL.`15`.toTypeOfBusiness shouldBe TypeOfBusinessWithFHL.`foreign-property`
    }
  }

}
