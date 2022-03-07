/*
 * Copyright 2022 HM Revenue & Customs
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

import mocks.MockAppConfig
import org.scalamock.handlers.CallHandler0
import support.UnitSpec
import v3.mocks.MockCurrentDateProvider
import v3.models.domain.TypeOfBusiness
import v3.models.errors.RuleAccountingPeriodNotSupportedError
import v3.models.utils.JsonErrorValidators

class AccountingPeriodNotSupportedValidationSpec extends UnitSpec with JsonErrorValidators with MockCurrentDateProvider with MockAppConfig{

  def setup(): CallHandler0[String] = {
    MockedAppConfig.v3TriggerForeignBsasMinimumTaxYear.returns("2021-22").anyNumberOfTimes()
    MockedAppConfig.v3TriggerNonForeignBsasMinimumTaxYear.returns("2019-20").anyNumberOfTimes()
  }

  "validate" should {
    "return no errors" when {
      List(
        (TypeOfBusiness.`self-employment`, "2019-04-06"),
        (TypeOfBusiness.`uk-property-fhl`, "2019-04-06"),
        (TypeOfBusiness.`uk-property-non-fhl`, "2019-04-06"),
        (TypeOfBusiness.`foreign-property-fhl-eea`, "2021-04-06"),
        (TypeOfBusiness.`foreign-property`, "2021-04-06"),
      ).foreach {
        case (typeOfBusiness, endDate) =>
          s"typeOfBusiness is $typeOfBusiness and the endDate is after the allowed end date" in {
            setup()
            AccountingPeriodNotSupportedValidation.validate(typeOfBusiness, endDate, mockAppConfig) shouldBe Nil
          }
      }
    }

    "return errors" when {
      List(
        (TypeOfBusiness.`self-employment`, "2019-04-05"),
        (TypeOfBusiness.`uk-property-fhl`, "2019-04-05"),
        (TypeOfBusiness.`uk-property-non-fhl`, "2019-04-05"),
        (TypeOfBusiness.`foreign-property-fhl-eea`, "2021-04-05"),
        (TypeOfBusiness.`foreign-property`, "2021-04-05"),
      ).foreach {
        case (typeOfBusiness, endDate) =>
          s"typeOfBusiness is $typeOfBusiness and the endDate is before the earliest allowed end date" in {
            setup()
            AccountingPeriodNotSupportedValidation.validate(typeOfBusiness, endDate, mockAppConfig) shouldBe List(RuleAccountingPeriodNotSupportedError)
          }
      }
    }
  }
}
