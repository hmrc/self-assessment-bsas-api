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

import support.UnitSpec
import v3.models.errors.{ MtdError, RuleBothExpensesError }
import v3.models.request.submitBsas.foreignProperty.{ FhlEeaExpenses, ForeignPropertyExpenses }
import v3.models.request.submitBsas.ukProperty.{ FHLExpenses, NonFHLExpenses }
import v3.models.utils.JsonErrorValidators

class BothExpensesValidationSpec extends UnitSpec with JsonErrorValidators {

  val path            = "path"
  val error: MtdError = RuleBothExpensesError.copy(paths = Some(Seq(path)))

  val figure = BigDecimal(100.20)

  def inputData(mappedData: Map[String, BigDecimal]): Option[Map[String, BigDecimal]] = {
    Some(mappedData)
  }

  val validConsolidatedExpensesOnly =
    Map("consolidatedExpenses" -> figure)

  val validConsolidatedExpenses =
    Map(
      "consolidatedExpenses"     -> figure,
      "residentialFinancialCost" -> figure
    )

  val validOtherExpense =
    Map("testfield1" -> figure)

  val multipleValidOtherExpenses =
    Map(
      "testfield1" -> figure,
      "testfield2" -> figure
    )

  case class SetUp(expensesAdjusted: Option[Map[String, BigDecimal]])

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

  "validate" when {
    "passed a ForeignPropertyExpenses model" should {
      val model: ForeignPropertyExpenses =
        ForeignPropertyExpenses(None, None, None, None, None, None, None, None, consolidatedExpenses = Some(123.45))

      "return no errors" when {
        "a valid consolidatedExpenses model is supplied with only consolidatedExpenses" in {
          BothExpensesValidation.validate(model, path) shouldBe Nil
        }

        "a valid consolidatedExpenses model is supplied with all consolidatedExpenses fields" in {
          BothExpensesValidation.validate(model.copy(residentialFinancialCost = Some(123.45)), path) shouldBe Nil
        }
      }

      "return an error" when {
        "a model with consolidatedExpenses and premisesRunningCosts is supplied" in {
          BothExpensesValidation.validate(model.copy(premisesRunningCosts = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and repairsAndMaintenance is supplied" in {
          BothExpensesValidation.validate(model.copy(repairsAndMaintenance = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and financialCosts is supplied" in {
          BothExpensesValidation.validate(model.copy(financialCosts = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and professionalFees is supplied" in {
          BothExpensesValidation.validate(model.copy(professionalFees = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and costsOfServices is supplied" in {
          BothExpensesValidation.validate(model.copy(costOfServices = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and travelCosts is supplied" in {
          BothExpensesValidation.validate(model.copy(travelCosts = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and other is supplied" in {
          BothExpensesValidation.validate(model.copy(other = Some(123.45)), path) shouldBe List(error)
        }
      }
    }

    "passed a foreign property FhlEeaExpenses model" should {
      val model: FhlEeaExpenses =
        FhlEeaExpenses(None, None, None, None, None, None, None, consolidatedExpenses = Some(123.45))

      "return no errors" when {
        "a valid consolidatedExpenses model is supplied with only consolidatedExpenses" in {
          BothExpensesValidation.validate(model, path) shouldBe Nil
        }
      }

      "return an error" when {
        "a model with consolidatedExpenses and premisesRunningCosts is supplied" in {
          BothExpensesValidation.validate(model.copy(premisesRunningCosts = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and repairsAndMaintenance is supplied" in {
          BothExpensesValidation.validate(model.copy(repairsAndMaintenance = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and financialCosts is supplied" in {
          BothExpensesValidation.validate(model.copy(financialCosts = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and professionalFees is supplied" in {
          BothExpensesValidation.validate(model.copy(professionalFees = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and costsOfServices is supplied" in {
          BothExpensesValidation.validate(model.copy(costOfServices = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and travelCosts is supplied" in {
          BothExpensesValidation.validate(model.copy(travelCosts = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and other is supplied" in {
          BothExpensesValidation.validate(model.copy(other = Some(123.45)), path) shouldBe List(error)
        }
      }
    }

    "passed a uk property NonFHLExpenses model" should {
      val model: NonFHLExpenses =
        NonFHLExpenses(None, None, None, None, None, None, None, None, consolidatedExpenses = Some(123.45))

      "return no errors" when {
        "a valid consolidatedExpenses model is supplied with only consolidatedExpenses" in {
          BothExpensesValidation.validate(model, path) shouldBe Nil
        }
      }

      "return an error" when {
        "a model with consolidatedExpenses and premisesRunningCosts is supplied" in {
          BothExpensesValidation.validate(model.copy(premisesRunningCosts = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and repairsAndMaintenance is supplied" in {
          BothExpensesValidation.validate(model.copy(repairsAndMaintenance = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and financialCosts is supplied" in {
          BothExpensesValidation.validate(model.copy(financialCosts = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and professionalFees is supplied" in {
          BothExpensesValidation.validate(model.copy(professionalFees = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and costsOfServices is supplied" in {
          BothExpensesValidation.validate(model.copy(costOfServices = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and residentialFinancialCost is supplied" in {
          BothExpensesValidation.validate(model.copy(residentialFinancialCost = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and travelCosts is supplied" in {
          BothExpensesValidation.validate(model.copy(travelCosts = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and other is supplied" in {
          BothExpensesValidation.validate(model.copy(other = Some(123.45)), path) shouldBe List(error)
        }
      }
    }

    "passed a uk property FHLExpenses model" should {
      val model: FHLExpenses =
        FHLExpenses(None, None, None, None, None, None, None, consolidatedExpenses = Some(123.45))

      "return no errors" when {
        "a valid consolidatedExpenses model is supplied with only consolidatedExpenses" in {
          BothExpensesValidation.validate(model, path) shouldBe Nil
        }
      }

      "return an error" when {
        "a model with consolidatedExpenses and premisesRunningCosts is supplied" in {
          BothExpensesValidation.validate(model.copy(premisesRunningCosts = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and repairsAndMaintenance is supplied" in {
          BothExpensesValidation.validate(model.copy(repairsAndMaintenance = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and financialCosts is supplied" in {
          BothExpensesValidation.validate(model.copy(financialCosts = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and professionalFees is supplied" in {
          BothExpensesValidation.validate(model.copy(professionalFees = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and costsOfServices is supplied" in {
          BothExpensesValidation.validate(model.copy(costOfServices = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and travelCosts is supplied" in {
          BothExpensesValidation.validate(model.copy(travelCosts = Some(123.45)), path) shouldBe List(error)
        }
        "a model with consolidatedExpenses and other is supplied" in {
          BothExpensesValidation.validate(model.copy(other = Some(123.45)), path) shouldBe List(error)
        }
      }
    }
  }
}
