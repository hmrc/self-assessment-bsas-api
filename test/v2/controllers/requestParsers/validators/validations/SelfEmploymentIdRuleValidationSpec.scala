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
import v2.models.domain.TypeOfBusiness
import v2.models.errors.RuleSelfEmploymentIdError
import v2.models.utils.JsonErrorValidators

class SelfEmploymentIdRuleValidationSpec extends UnitSpec with JsonErrorValidators  {

  case class SetUp(selfEmploymentId: Option[String] = Some("XAIS12345678901"), typeOfBusiness: TypeOfBusiness = TypeOfBusiness.`self-employment`)

  "validate" should {
    "return no errors" when {
      "a valid self employment id is provided" in new SetUp() {

        SelfEmploymentIdRuleValidation.validate(selfEmploymentId, typeOfBusiness).isEmpty shouldBe true
      }

      "no self employment id is provided for a property" in new SetUp(None, TypeOfBusiness.`uk-property-non-fhl`) {

        SelfEmploymentIdRuleValidation.validate(selfEmploymentId, typeOfBusiness).isEmpty shouldBe true
      }
    }

    "return an error" when {
      "the business type is self-assessment business however there is no self employment ID " in new SetUp(None) {

        val validationResult = SelfEmploymentIdRuleValidation.validate(selfEmploymentId, typeOfBusiness)

        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleSelfEmploymentIdError
      }

      "the business type is a property-fhl there is a self employment ID " in new SetUp(typeOfBusiness = TypeOfBusiness.`uk-property-fhl`) {

        val validationResult = SelfEmploymentIdRuleValidation.validate(selfEmploymentId, typeOfBusiness)

        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleSelfEmploymentIdError
      }

      "the business type is a none property-fhl there is a self employment ID " in new SetUp(typeOfBusiness = TypeOfBusiness.`uk-property-non-fhl`) {

        val validationResult = SelfEmploymentIdRuleValidation.validate(selfEmploymentId, typeOfBusiness)

        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleSelfEmploymentIdError
      }
    }
  }
}
