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

package v2.controllers.requestParsers.validators.validations

import support.UnitSpec
import v2.models.errors.SelfEmploymentIdFormatError
import v2.models.utils.JsonErrorValidators

class SelfEmploymentIdValidationSpec extends UnitSpec with JsonErrorValidators  {

  case class SetUp(selfEmploymentId: String, typeOfBusiness: String = "self-employed")

  "validate" should {
    "return no errors" when {
      "a valid self employment id is provided" in new SetUp("XAIS12345678901") {

        SelfEmploymentIdValidation.validate(selfEmploymentId).isEmpty shouldBe true
      }
    }

    "return an error" when {
      "an invalid self employment id is provided" in new SetUp("XAXAIS65271982AD"){

        val validationResult = SelfEmploymentIdValidation.validate(selfEmploymentId)

        validationResult.length shouldBe 1
        validationResult.head shouldBe SelfEmploymentIdFormatError
      }
    }
  }
}
