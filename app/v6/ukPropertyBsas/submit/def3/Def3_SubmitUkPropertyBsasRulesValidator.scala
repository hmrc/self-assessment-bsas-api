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
import v6.ukPropertyBsas.submit.def3.model.request.{Def3_SubmitUkPropertyBsasRequestData, Expenses, Income}

object Def3_SubmitUkPropertyBsasRulesValidator extends RulesValidator[Def3_SubmitUkPropertyBsasRequestData] {

  def validateBusinessRules(parsed: Def3_SubmitUkPropertyBsasRequestData): Validated[Seq[MtdError], Def3_SubmitUkPropertyBsasRequestData] = {
    import parsed.body

    val (validatedExpenses, validatedConsolidatedExpenses) = body.expenses match {
      case Some(expenses) =>
        (
          validateExpenses(expenses),
          validateConsolidatedExpenses(expenses)
        )
      case None =>
        (valid, valid)
    }

    val validatedIncome = body.income match {
      case Some(income) => validateIncome(income)
      case None         => valid
    }

    combine(
      validatedExpenses,
      validatedConsolidatedExpenses,
      validatedIncome
    ).onSuccess(parsed)

  }

  private def resolveMaybeNegativeNumber(path: String, value: Option[BigDecimal]): Validated[Seq[MtdError], Option[BigDecimal]] =
    ResolveParsedNumber(min = -99999999999.99, disallowZero = true)(value, path)

  private def validateExpenses(expenses: Expenses): Validated[Seq[MtdError], Unit] = {
    combine(
      resolveMaybeNegativeNumber("/expenses/premisesRunningCosts", expenses.premisesRunningCosts),
      resolveMaybeNegativeNumber("/expenses/repairsAndMaintenance", expenses.repairsAndMaintenance),
      resolveMaybeNegativeNumber("/expenses/financialCosts", expenses.financialCosts),
      resolveMaybeNegativeNumber("/expenses/professionalFees", expenses.professionalFees),
      resolveMaybeNegativeNumber("/expenses/travelCosts", expenses.travelCosts),
      resolveMaybeNegativeNumber("/expenses/costOfServices", expenses.costOfServices),
      resolveMaybeNegativeNumber("/expenses/other", expenses.other),
      resolveMaybeNegativeNumber("/expenses/consolidatedExpenses", expenses.consolidatedExpenses)
    )
  }

  private def validateIncome(income: Income): Validated[Seq[MtdError], Unit] = {
    combine(
      resolveMaybeNegativeNumber("/income/totalRentsReceived", income.totalRentsReceived),
      resolveMaybeNegativeNumber("/income/premiumsOfLeaseGrant", income.premiumsOfLeaseGrant),
      resolveMaybeNegativeNumber("/income/reversePremiums", income.reversePremiums),
      resolveMaybeNegativeNumber("/income/otherPropertyIncome", income.otherPropertyIncome)
    )
  }

  private def validateConsolidatedExpenses(expenses: Expenses): Validated[Seq[MtdError], Unit] = {
    expenses match {
      case Expenses(None, None, None, None, None, None, None, None, Some(_)) =>
        valid
      case Expenses(_, _, _, _, _, _, _, _, Some(_)) =>
        Invalid(List(RuleBothExpensesError.withPath("/expenses")))
      case _ =>
        valid
    }
  }

}
