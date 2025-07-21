/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.selfEmploymentBsas.submit.def1

import cats.data.Validated
import cats.data.Validated.Invalid
import common.errors.RuleBothExpensesError
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.ResolveParsedNumber
import shared.models.errors.MtdError
import v7.selfEmploymentBsas.submit.def1.model.request.{Additions, Def1_SubmitSelfEmploymentBsasRequestData, Expenses, Income}

object Def1_SubmitSelfEmploymentBsasRulesValidator extends RulesValidator[Def1_SubmitSelfEmploymentBsasRequestData] {

  def validateBusinessRules(parsed: Def1_SubmitSelfEmploymentBsasRequestData): Validated[Seq[MtdError], Def1_SubmitSelfEmploymentBsasRequestData] = {

    import parsed.*

    val validatedIncome = body.income.map(validateIncome).getOrElse(valid)

    val (validatedExpenses, validatedBothExpenses) = body.expenses
      .map(expenses => (validateExpenses(expenses), validateBothExpenses(expenses, body.additions)))
      .getOrElse((valid, valid))

    val validatedAdditions = body.additions.map(validateAdditions).getOrElse(valid)

    combine(
      validatedIncome,
      validatedExpenses,
      validatedBothExpenses,
      validatedAdditions
    ).onSuccess(parsed)
  }

  private val resolveAdjustment = ResolveParsedNumber(min = -99999999999.99, disallowZero = true)

  private def resolveAdjusted(path: String, value: Option[BigDecimal]): Validated[Seq[MtdError], Option[BigDecimal]] =
    resolveAdjustment(value, path)

  private def validateIncome(income: Income): Validated[Seq[MtdError], Unit] = {
    combine(
      resolveAdjusted("/income/turnover", income.turnover),
      resolveAdjusted("/income/other", income.other)
    )
  }

  private def validateExpenses(expenses: Expenses): Validated[Seq[MtdError], Unit] =
    combine(
      resolveAdjusted("/expenses/costOfGoods", expenses.costOfGoods),
      resolveAdjusted("/expenses/paymentsToSubcontractors", expenses.paymentsToSubcontractors),
      resolveAdjusted("/expenses/wagesAndStaffCosts", expenses.wagesAndStaffCosts),
      resolveAdjusted("/expenses/carVanTravelExpenses", expenses.carVanTravelExpenses),
      resolveAdjusted("/expenses/premisesRunningCosts", expenses.premisesRunningCosts),
      resolveAdjusted("/expenses/maintenanceCosts", expenses.maintenanceCosts),
      resolveAdjusted("/expenses/adminCosts", expenses.adminCosts),
      resolveAdjusted("/expenses/interestOnBankOtherLoans", expenses.interestOnBankOtherLoans),
      resolveAdjusted("/expenses/financeCharges", expenses.financeCharges),
      resolveAdjusted("/expenses/irrecoverableDebts", expenses.irrecoverableDebts),
      resolveAdjusted("/expenses/professionalFees", expenses.professionalFees),
      resolveAdjusted("/expenses/depreciation", expenses.depreciation),
      resolveAdjusted("/expenses/otherExpenses", expenses.otherExpenses),
      resolveAdjusted("/expenses/advertisingCosts", expenses.advertisingCosts),
      resolveAdjusted("/expenses/businessEntertainmentCosts", expenses.businessEntertainmentCosts),
      resolveAdjusted("/expenses/consolidatedExpenses", expenses.consolidatedExpenses)
    )

  private def validateAdditions(additions: Additions): Validated[Seq[MtdError], Unit] =
    combine(
      resolveAdjusted("/additions/costOfGoodsDisallowable", additions.costOfGoodsDisallowable),
      resolveAdjusted("/additions/paymentsToSubcontractorsDisallowable", additions.paymentsToSubcontractorsDisallowable),
      resolveAdjusted("/additions/wagesAndStaffCostsDisallowable", additions.wagesAndStaffCostsDisallowable),
      resolveAdjusted("/additions/carVanTravelExpensesDisallowable", additions.carVanTravelExpensesDisallowable),
      resolveAdjusted("/additions/premisesRunningCostsDisallowable", additions.premisesRunningCostsDisallowable),
      resolveAdjusted("/additions/maintenanceCostsDisallowable", additions.maintenanceCostsDisallowable),
      resolveAdjusted("/additions/adminCostsDisallowable", additions.adminCostsDisallowable),
      resolveAdjusted("/additions/interestOnBankOtherLoansDisallowable", additions.interestOnBankOtherLoansDisallowable),
      resolveAdjusted("/additions/financeChargesDisallowable", additions.financeChargesDisallowable),
      resolveAdjusted("/additions/irrecoverableDebtsDisallowable", additions.irrecoverableDebtsDisallowable),
      resolveAdjusted("/additions/professionalFeesDisallowable", additions.professionalFeesDisallowable),
      resolveAdjusted("/additions/depreciationDisallowable", additions.depreciationDisallowable),
      resolveAdjusted("/additions/otherExpensesDisallowable", additions.otherExpensesDisallowable),
      resolveAdjusted("/additions/advertisingCostsDisallowable", additions.advertisingCostsDisallowable),
      resolveAdjusted("/additions/businessEntertainmentCostsDisallowable", additions.businessEntertainmentCostsDisallowable)
    )

  private def validateBothExpenses(expenses: Expenses, additions: Option[Additions]): Validated[Seq[MtdError], Unit] = {

    def bothExpensesError = List(RuleBothExpensesError.withPath("/expenses"))

    (expenses.consolidatedExpenses, additions) match {
      case (None, _)                                                                       => valid
      case (Some(_), None) if expenses.hasOnlyConsolidatedExpenses                         => valid
      case (Some(_), None)                                                                 => Invalid(bothExpensesError)
      case (Some(_), Some(adds)) if !expenses.hasOnlyConsolidatedExpenses || adds.nonEmpty => Invalid(bothExpensesError)
      case (Some(_), Some(_))                                                              => valid
    }
  }

}
