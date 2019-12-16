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
import v1.fixtures.SubmitUKPropertyBsasRequestBodyFixtures._
import v1.models.utils.JsonErrorValidators

class AdjustmentRangeValidationSpec extends UnitSpec with JsonErrorValidators {

  val validAdjustment = Some(BigDecimal(2.00))

  val invaldAdjustmentPositive = Some(BigDecimal(100000000000.00))
  val invalidAdjustmentNegative = Some(BigDecimal(-100000000000.00))

  case class SetUp(adjustmentValue: Option[BigDecimal], fieldName: String = "other")

  "validate" should {
    "return no errors" when {
      "a adjustment is provided which is within bounds" in new SetUp(validAdjustment) {

        AdjustmentRangeValidation.validate(adjustmentValue, fieldName).isEmpty shouldBe true
      }
    }

    "return errors" when {

      "a adjustments below -99999999999.99" in new SetUp(invalidAdjustmentNegative) {

        val result = AdjustmentRangeValidation.validate(adjustmentValue, fieldName)

        result.length shouldBe 1
        result shouldBe List(rangeError(fieldName))
      }

      "a adjustment is above 99999999999.99" in new SetUp(invaldAdjustmentPositive) {

        val result = AdjustmentRangeValidation.validate(adjustmentValue, fieldName)

        result.length shouldBe 1
        result shouldBe List(rangeError(fieldName))
      }
    }
  }
}
