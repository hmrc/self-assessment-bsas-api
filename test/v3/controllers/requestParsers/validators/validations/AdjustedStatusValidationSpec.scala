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

package v3.controllers.requestParsers.validators.validations

import api.models.errors.AdjustedStatusFormatError
import support.UnitSpec

class AdjustedStatusValidationSpec extends UnitSpec {

  val validAdjustedStatusTrueLowercase = "true"
  val validAdjustedStatusFalseLowercase = "false"
  val validAdjustedStatusTrueUppercase = "TRUE"
  val validAdjustedStatusFalseUppercase = "FALSE"
  val validAdjustedStatusTruePascalcase = "True"
  val validAdjustedStatusFalsePascalcase = "False"
  val invalidAdjustedStatus1 = "1"
  val invalidAdjustedStatusNonsense = "beans"

  "validate" should {
    "return no errors" when {
      List(validAdjustedStatusTrueLowercase, validAdjustedStatusFalseLowercase, validAdjustedStatusTrueUppercase,
        validAdjustedStatusFalseUppercase, validAdjustedStatusTruePascalcase, validAdjustedStatusFalsePascalcase).foreach {
        status =>
          s"passed the valid adjusted status '$status'" in {
            AdjustedStatusValidation.validate(status) shouldBe List()
          }
      }
    }
    "return an error" when {
      List(invalidAdjustedStatus1, invalidAdjustedStatusNonsense).foreach {
        status =>
          s"passed the invalid adjusted status '$status'" in {
            AdjustedStatusValidation.validate(status) shouldBe List(AdjustedStatusFormatError)
          }
      }
    }
  }

}
