/*
 * Copyright 2021 HM Revenue & Customs
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
import v1.models.errors.EndBeforeStartDateError
import v1.models.utils.JsonErrorValidators

class EndBeforeDateValidationSpec extends UnitSpec with JsonErrorValidators {

  case class SetUp(startDate:String, endDate: String)

  "validate" should {
    "return no errors" when {
      "the start date is before the end date" in new SetUp("2019-05-06", "2020-05-05") {

        val validationResult = EndBeforeStartDateValidation.validate(startDate, endDate)

        validationResult.isEmpty shouldBe true
      }
    }

    "return errors" when {
      "the end date is before the start date" in new SetUp("2019-05-06", "2019-05-05") {

        val validationResult = EndBeforeStartDateValidation.validate(startDate, endDate)

        validationResult.length shouldBe 1
        validationResult.head shouldBe EndBeforeStartDateError
      }
    }
  }
}
