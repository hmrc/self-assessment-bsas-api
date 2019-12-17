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
import v1.models.errors.RuleBothExpensesError
import v1.models.utils.JsonErrorValidators

class BothExpensesValidationSpec extends UnitSpec with JsonErrorValidators {

  val figure = BigDecimal(100.20)

  def inputData(mapedData: Map[String, Option[BigDecimal]]): Option[Map[String, Option[BigDecimal]]] = {
    Some(mapedData)
  }

  val validConsolidatedExpensesOnly =
    Map("consolidatedExpenses" -> Some(figure))

  val validConsolidatedExpenses =
    Map(
      "consolidatedExpenses" -> Some(figure),
      "residentialFinancialCost" -> Some(figure)
    )

  val validOtherExpense =
    Map("testfield1" -> Some(figure))

  val multipleValidOtherExpenses =
    Map(
      "testfield1" -> Some(figure),
      "testfield2" -> Some(figure)
    )

  case class SetUp(expensesAdjusted: Option[Map[String, Option[BigDecimal]]])

  "validate" should {
    "return no errors" when {
      "an expense is present" in new SetUp(inputData(validOtherExpense)) {

        BothExpensesValidation.validate(expensesAdjusted).isEmpty shouldBe true
      }

      "expenses are present" in new SetUp(inputData(multipleValidOtherExpenses)) {

        BothExpensesValidation.validate(expensesAdjusted).isEmpty shouldBe true
      }

      "consolidated expenses is present" in new SetUp(inputData(validConsolidatedExpensesOnly)) {

        BothExpensesValidation.validate(expensesAdjusted).isEmpty shouldBe true
      }

      "consolidated expenses is present with residential cost" in new SetUp(inputData(validConsolidatedExpenses)) {

        BothExpensesValidation.validate(expensesAdjusted).isEmpty shouldBe true
      }
    }

    "return errors" when {
      "when consolidated expenses is present with another expense" in
        new SetUp(inputData(validConsolidatedExpensesOnly ++ validOtherExpense)) {

          val result = BothExpensesValidation.validate(expensesAdjusted)

          result.length shouldBe 1
          result.head shouldBe RuleBothExpensesError
        }

      "when consolidated expenses and residential cost is present with another expense" in
        new SetUp(inputData(validConsolidatedExpenses ++ validOtherExpense)) {

          val result = BothExpensesValidation.validate(expensesAdjusted)

          result.length shouldBe 1
          result.head shouldBe RuleBothExpensesError
        }

      "when consolidated expenses and residential cost is present with multiple expenses" in
        new SetUp(inputData(validConsolidatedExpenses ++ multipleValidOtherExpenses)) {
          val result = BothExpensesValidation.validate(expensesAdjusted)

          result.length shouldBe 1
          result.head shouldBe RuleBothExpensesError
        }
    }
  }
}
