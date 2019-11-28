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

package v1.controllers.requestParsers.validators.validations

import support.UnitSpec
import v1.models.errors.TypeOfBusinessFormatError
import v1.models.utils.JsonErrorValidators

class TypeOfBusinessValidationSpec extends UnitSpec with JsonErrorValidators {

  case class SetUp(typeOfBusiness: String)

  "validate" should {
    "return no errors" when {
      "a valid type of business uk property fhl is provided" in new SetUp("uk-property-fhl") {

        TypeOfBusinessValidation.validate(typeOfBusiness).isEmpty shouldBe true
      }
      "a valid type of business uk property is provided" in new SetUp("uk-property-non-fhl") {

        TypeOfBusinessValidation.validate(typeOfBusiness).isEmpty shouldBe true
      }

      "a valid type of business self assessment is provided" in new SetUp("self-employment") {

        TypeOfBusinessValidation.validate(typeOfBusiness).isEmpty shouldBe true
      }
      "an invalid type of business is provided" in new SetUp("selfemployment") {

        val validationResult = TypeOfBusinessValidation.validate(typeOfBusiness)
        validationResult.length shouldBe 1
        validationResult.head shouldBe TypeOfBusinessFormatError
      }
    }
  }
}
