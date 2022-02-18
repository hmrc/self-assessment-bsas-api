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
import v3.models.request.submitBsas.selfEmployment.{ Additions, Expenses }
import v3.models.request.submitBsas.ukProperty.{ FHLExpenses, NonFHLExpenses }
import v3.models.utils.JsonErrorValidators

class BothExpensesValidationSpec extends UnitSpec with JsonErrorValidators {

  val path            = "path"
  val error: MtdError = RuleBothExpensesError.copy(paths = Some(Seq(path)))

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

    "passed a self employment Expenses model" should {
      "both expenses are present" when {
        // @formatter:off

        val model: Expenses =
          Expenses(None, None, None, None, None, None, None, None, None, None, None, None, None, None, None,
            consolidatedExpenses = Some(123.45))

        val additionModel: Additions =
          Additions(None, None, None, None, None, None, None, None, None, None, None, None, None, None, None)

        "return no errors" when {
          "a valid consolidatedExpenses model is supplied with only consolidatedExpenses" in {
            BothExpensesValidation.bothExpensesValidation(model, None, path) shouldBe NoValidationErrors
          }
        }

        "return an error" when {
          "a model with consolidatedExpenses and costOfGoodsAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(expenses = model.copy(costOfGoodsAllowable = Some(123.45)), additions = None, path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and paymentsToSubcontractorsAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(paymentsToSubcontractorsAllowable = Some(123.45)), None, path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and wagesAndStaffCostsAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(wagesAndStaffCostsAllowable = Some(123.45)), None, path) shouldBe List(error)
          }
          "a model with consolidatedExpenses and carVanTravelExpensesAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(carVanTravelExpensesAllowable = Some(123.45)), None, path) shouldBe List(error)
          }
          "a model with consolidatedExpenses and premisesRunningCostsAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(premisesRunningCostsAllowable = Some(123.45)), None, path) shouldBe List(error)
          }
          "a model with consolidatedExpenses and maintenanceCostsAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(maintenanceCostsAllowable = Some(123.45)), None, path) shouldBe List(error)
          }
          "a model with consolidatedExpenses and adminCostsAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(adminCostsAllowable = Some(123.45)), None, path) shouldBe List(error)
          }
          "a model with consolidatedExpenses and interestOnBankOtherLoansAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(interestOnBankOtherLoansAllowable = Some(123.45)), None, path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and financeChargesAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(financeChargesAllowable = Some(123.45)), None, path) shouldBe List(error)
          }
          "a model with consolidatedExpenses and irrecoverableDebtsAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(irrecoverableDebtsAllowable = Some(123.45)), None, path) shouldBe List(error)
          }
          "a model with consolidatedExpenses and professionalFeesAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(professionalFeesAllowable = Some(123.45)), None, path) shouldBe List(error)
          }
          "a model with consolidatedExpenses and depreciationAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(depreciationAllowable = Some(123.45)), None, path) shouldBe List(error)
          }
          "a model with consolidatedExpenses and otherExpensesAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(otherExpensesAllowable = Some(123.45)), None, path) shouldBe List(error)
          }
          "a model with consolidatedExpenses and advertisingCostsAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(advertisingCostsAllowable = Some(123.45)), None, path) shouldBe List(error)
          }
          "a model with consolidatedExpenses and businessEntertainmentCostsAllowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model.copy(businessEntertainmentCostsAllowable = Some(123.45)), None, path) shouldBe List(
              error)
          }
        }

        "return an error" when {
          "a model with consolidatedExpenses and costOfGoodsDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(costOfGoodsDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and paymentsToSubcontractorsDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(paymentsToSubcontractorsDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and wagesAndStaffCostsDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(wagesAndStaffCostsDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and carVanTravelExpensesDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(carVanTravelExpensesDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and premisesRunningCostsDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(premisesRunningCostsDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and maintenanceCostsDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(maintenanceCostsDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and adminCostsDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(adminCostsDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and interestOnBankOtherLoansDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(interestOnBankOtherLoansDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and financeChargesDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(financeChargesDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and irrecoverableDebtsDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(irrecoverableDebtsDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and professionalFeesDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(professionalFeesDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and depreciationDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(depreciationDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and otherExpensesDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(otherExpensesDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and advertisingCostsDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model, Some(additionModel.copy(advertisingCostsDisallowable = Some(123.45))), path) shouldBe List(
              error)
          }
          "a model with consolidatedExpenses and businessEntertainmentCostsDisallowable is supplied" in {
            BothExpensesValidation.bothExpensesValidation(model,
                                                          Some(additionModel.copy(businessEntertainmentCostsDisallowable = Some(123.45))),
                                                          path) shouldBe List(error)
          }
        }
        // @formatter:on
      }
    }
  }
}
