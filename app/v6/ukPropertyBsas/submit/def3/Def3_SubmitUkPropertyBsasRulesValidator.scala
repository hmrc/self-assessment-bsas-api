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

package v6.ukPropertyBsas.submit.def3

import cats.data.Validated
import cats.data.Validated.Invalid
import common.errors.RuleBothExpensesError
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.ResolveParsedNumber
import shared.models.errors.MtdError
import v6.ukPropertyBsas.submit.def3.model.request.{Def3_SubmitUkPropertyBsasRequestData, Expenses, UkProperty}

object Def3_SubmitUkPropertyBsasRulesValidator extends RulesValidator[Def3_SubmitUkPropertyBsasRequestData] {

  def validateBusinessRules(parsed: Def3_SubmitUkPropertyBsasRequestData): Validated[Seq[MtdError], Def3_SubmitUkPropertyBsasRequestData] = {
    import parsed.body

    val (validatedExpenses, validatedConsolidatedExpenses) = body.ukProperty match {
      case Some(ukProperty) =>
        (
          validateExpenses(ukProperty),
          validateConsolidatedExpenses(ukProperty)
        )
      case None =>
        (valid, valid)
    }

    val validatedIncome = body.ukProperty match {
      case Some(ukProperty) => validateIncome(ukProperty)
      case None             => valid
    }

    combine(
      validatedExpenses,
      validatedConsolidatedExpenses,
      validatedIncome
    ).onSuccess(parsed)

  }

  private def resolveMaybeNegativeNumber(path: String, value: Option[BigDecimal]): Validated[Seq[MtdError], Option[BigDecimal]] =
    ResolveParsedNumber(min = -99999999999.99, disallowZero = true)(value, path)

  private def validateExpenses(ukProperty: UkProperty): Validated[Seq[MtdError], Unit] = {
    combine(
      resolveMaybeNegativeNumber("/ukProperty/expenses/premisesRunningCosts", ukProperty.expenses.flatMap(_.premisesRunningCosts)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/repairsAndMaintenance", ukProperty.expenses.flatMap(_.repairsAndMaintenance)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/financialCosts", ukProperty.expenses.flatMap(_.financialCosts)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/professionalFees", ukProperty.expenses.flatMap(_.professionalFees)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/travelCosts", ukProperty.expenses.flatMap(_.travelCosts)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/consolidatedExpenses", ukProperty.expenses.flatMap(_.consolidatedExpenses)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/costOfServices", ukProperty.expenses.flatMap(_.costOfServices)),
      ResolveParsedNumber(disallowZero = true)(
        ukProperty.expenses.flatMap(_.residentialFinancialCost),
        "/ukProperty/expenses/residentialFinancialCost"),
      ResolveParsedNumber(disallowZero = true)(ukProperty.expenses.flatMap(_.other), "/ukProperty/expenses/other")
    )
  }

  private def validateIncome(ukProperty: UkProperty): Validated[Seq[MtdError], Unit] = {
    combine(
      resolveMaybeNegativeNumber("/ukProperty/income/totalRentsReceived", ukProperty.income.flatMap(_.totalRentsReceived)),
      resolveMaybeNegativeNumber("/ukProperty/income/premiumsOfLeaseGrant", ukProperty.income.flatMap(_.premiumsOfLeaseGrant)),
      resolveMaybeNegativeNumber("/ukProperty/income/reversePremiums", ukProperty.income.flatMap(_.reversePremiums)),
      resolveMaybeNegativeNumber("/ukProperty/income/otherPropertyIncome", ukProperty.income.flatMap(_.otherPropertyIncome))
    )
  }

  private def validateConsolidatedExpenses(ukProperty: UkProperty): Validated[Seq[MtdError], Unit] = {
    ukProperty.expenses
      .collect {
        case expenses if expenses.consolidatedExpenses.isDefined =>
          expenses match {
            case Expenses(None, None, None, None, None, None, None, None, Some(_)) =>
              valid
            case _ =>
              Invalid(List(RuleBothExpensesError.withPath("/ukProperty/expenses")))
          }
      }
      .getOrElse(valid)
  }

}
