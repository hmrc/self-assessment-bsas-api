/*
 * Copyright 2019 HM Revenue & Customs
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

class TypeOfBusinessSpec extends UnitSpec {

  "isTypeOfBusiness" when {
    "provided with a valid type of business" should {
      "return 'true'" in {
        TypeOfBusiness.isTypeOfBusiness("self-employment") shouldBe true
        TypeOfBusiness.isTypeOfBusiness("uk-property-fhl") shouldBe true
        TypeOfBusiness.isTypeOfBusiness("uk-property-non-fhl") shouldBe true
      }
    }
    "provided with an invalid type of business" should {
      "return 'false'" in {
        TypeOfBusiness.isTypeOfBusiness("not-a-business-type") shouldBe false
      }
    }
  }

  "apply" when {
    "provided with a valid type of business" should {
      "return the corresponding case object" in {
        TypeOfBusiness("self-employment") shouldBe TypeOfBusiness.SelfEmployment
        TypeOfBusiness("uk-property-fhl") shouldBe TypeOfBusiness.UkPropertyFhl
        TypeOfBusiness("uk-property-non-fhl") shouldBe TypeOfBusiness.UkPropertyNonFhl
      }
    }
  }

  "toIdentifierValue" must {
    "return the correct values" in {
      TypeOfBusiness.SelfEmployment.toIdentifierValue shouldBe "N/A"
      TypeOfBusiness.UkPropertyFhl.toIdentifierValue shouldBe "04"
      TypeOfBusiness.UkPropertyNonFhl.toIdentifierValue shouldBe "02"
    }
  }
}
