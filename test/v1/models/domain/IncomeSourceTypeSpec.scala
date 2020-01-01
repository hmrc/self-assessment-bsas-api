/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.domain

import support.UnitSpec
import utils.enums.EnumJsonSpecSupport
import v1.models.domain.IncomeSourceType._

class IncomeSourceTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testRoundTrip[IncomeSourceType](
    ("01", `01`),
    ("02", `02`),
    ("04", `04`)
  )

  "toIdentifierValue" should {
    "return the correct identifier value" in {
      IncomeSourceType.`01`.toTypeOfBusiness shouldBe TypeOfBusiness.`self-employment`
      IncomeSourceType.`02`.toTypeOfBusiness shouldBe TypeOfBusiness.`uk-property-non-fhl`
      IncomeSourceType.`04`.toTypeOfBusiness shouldBe TypeOfBusiness.`uk-property-fhl`
    }
  }
}

