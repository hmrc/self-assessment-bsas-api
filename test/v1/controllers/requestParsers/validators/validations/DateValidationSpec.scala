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
import v1.models.errors.{EndDateFormatError, MtdError, StartDateFormatError}
import v1.models.utils.JsonErrorValidators

class DateValidationSpec extends UnitSpec with JsonErrorValidators {

  case class SetUp(date: String, error: MtdError = StartDateFormatError)

  "validate" should {
    "return no errors" when {
      "when a date in the correct format is supplied" in new SetUp("2018-05-06") {

        val validationResult = DateValidation.validate(error)(date)

        validationResult.isEmpty shouldBe true
      }
    }

    "return an error" when {
      "an invalid start date with dd-mm-yyyy format" in new SetUp("06-05-2019") {

        val validationResult = DateValidation.validate(error)(date)

        validationResult.length shouldBe 1
        validationResult.head shouldBe StartDateFormatError
      }

      "an invalid start date with Letters format" in new SetUp("dd-mm-yyyy") {

        val validationResult = DateValidation.validate(error)(date)

        validationResult.length shouldBe 1
        validationResult.head shouldBe StartDateFormatError
      }

      "an invalid end date with dd-mm-yyyy format" in new SetUp("06-05-2019") {

        val validationResult = DateValidation.validate(EndDateFormatError)(date)

        validationResult.length shouldBe 1
        validationResult.head shouldBe EndDateFormatError
      }

    }

  }
}
