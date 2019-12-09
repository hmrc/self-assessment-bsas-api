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

class AdjustmentValueValidationSpec extends UnitSpec with JsonErrorValidators {

  val validPositiveAdjustment = Some(BigDecimal(1.00))
  val validNegativeAdjustment = Some(BigDecimal(-2.00))
  val validDecimalAdjustment = Some(BigDecimal(3.46))

  val invaldzero = Some(BigDecimal(0))
  val invalidDecimalZero = Some(BigDecimal(0.00))

  case class SetUp(adjustmentValue: Option[BigDecimal], fieldName: String = "other")

  "validate" should {
    "return no errors" when {
      "the adjustment is a positive number" in new SetUp(validPositiveAdjustment) {

        AdjustmentValueValidation.validate(adjustmentValue, fieldName).isEmpty shouldBe true
      }

      "the adjustment is a negative number" in new SetUp(validNegativeAdjustment) {

        AdjustmentValueValidation.validate(adjustmentValue, fieldName).isEmpty shouldBe true
      }

      "the adjustment is a decimal number" in new SetUp(validDecimalAdjustment) {

        AdjustmentValueValidation.validate(adjustmentValue, fieldName).isEmpty shouldBe true
      }
    }

    "return errors" when {
      "the adjustment has a value zero" in new SetUp(invaldzero) {

        val result = AdjustmentValueValidation.validate(adjustmentValue, fieldName)

        result.length shouldBe 1
        result shouldBe List(zeroError(fieldName))
      }

      "the adjustment has a value zero decimal" in new SetUp(invalidDecimalZero) {

        val result = AdjustmentValueValidation.validate(adjustmentValue, fieldName)

        result.length shouldBe 1
        result shouldBe List(zeroError(fieldName))
      }
    }
  }
}