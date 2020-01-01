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

package v1.controllers.requestParsers.validators.validations

import support.UnitSpec
import v1.mocks.MockCurrentDateProvider
import v1.models.errors.RuleAccountingPeriodNotEndedError
import v1.models.utils.JsonErrorValidators

class NotEndedAccountingPeriodValidationSpec extends UnitSpec with JsonErrorValidators with MockCurrentDateProvider {

  case class SetUp(endDate: String, currentDate: String = "2020-05-06" )

  "validate" should {
    "return no errors" when {
      "the end date of the accounting period is before the current date" in new SetUp("2020-05-05") {

        NotEndedAccountingPeriodValidation.validate(currentDate, endDate).isEmpty shouldBe true
      }
    }

    "return errors" when {
      "the end date is after the current date" in new SetUp("2020-05-07") {

        private val validationResult = NotEndedAccountingPeriodValidation.validate(currentDate, endDate)

        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleAccountingPeriodNotEndedError
      }
    }
  }
}
