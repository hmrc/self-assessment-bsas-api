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

package v3.controllers.validators

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.ResolveParsedNumber
import api.models.errors.MtdError
import v3.models.errors.RuleBothExpensesError
import v3.models.request.submitBsas.selfEmployment._

import javax.inject.Singleton

@Singleton
class SubmitSelfEmploymentBsasRulesValidatorFactory {

  private val resolveAdjustment = ResolveParsedNumber(min = -99999999999.99, disallowZero = true)

  private def resolveAdjusted(path: String, value: Option[BigDecimal]): Either[Seq[MtdError], Option[BigDecimal]] =
    resolveAdjustment(value, path = Some(path))

  def validator(parsed: SubmitSelfEmploymentBsasRequestData): Validator[SubmitSelfEmploymentBsasRequestData] =
    new Validator[SubmitSelfEmploymentBsasRequestData] {

      def validate: Either[Seq[MtdError], SubmitSelfEmploymentBsasRequestData] = {

        val validatedIncome = parsed.body.income.map(validateIncome).getOrElse(Right(parsed))
        val validatedExpenses = parsed.body.expenses
          .map(
            expenses =>
              combine(
                parsed,
                validateExpenses(expenses),
                validateBothExpenses(expenses, parsed.body.additions)
            ))
          .getOrElse(Right(parsed))
        val validatedAdditions = parsed.body.additions.map(validateAdditions).getOrElse(Right(parsed))

        combine(
          parsed,
          validatedIncome,
          validatedExpenses,
          validatedAdditions
        )
      }

      private def validateIncome(income: Income): Either[Seq[MtdError], SubmitSelfEmploymentBsasRequestData] =
        combine(
          parsed,
          resolveAdjusted("/income/turnover", income.turnover),
          resolveAdjusted("/income/other", income.other)
        )

      private def validateExpenses(expenses: Expenses): Either[Seq[MtdError], SubmitSelfEmploymentBsasRequestData] =
        combine(
          parsed,
          resolveAdjusted("/expenses/costOfGoodsAllowable", expenses.costOfGoodsAllowable),
          resolveAdjusted("/expenses/paymentsToSubcontractorsAllowable", expenses.paymentsToSubcontractorsAllowable),
          resolveAdjusted("/expenses/wagesAndStaffCostsAllowable", expenses.wagesAndStaffCostsAllowable),
          resolveAdjusted("/expenses/carVanTravelExpensesAllowable", expenses.carVanTravelExpensesAllowable),
          resolveAdjusted("/expenses/premisesRunningCostsAllowable", expenses.premisesRunningCostsAllowable),
          resolveAdjusted("/expenses/maintenanceCostsAllowable", expenses.maintenanceCostsAllowable),
          resolveAdjusted("/expenses/adminCostsAllowable", expenses.adminCostsAllowable),
          resolveAdjusted("/expenses/interestOnBankOtherLoansAllowable", expenses.interestOnBankOtherLoansAllowable),
          resolveAdjusted("/expenses/financeChargesAllowable", expenses.financeChargesAllowable),
          resolveAdjusted("/expenses/irrecoverableDebtsAllowable", expenses.irrecoverableDebtsAllowable),
          resolveAdjusted("/expenses/professionalFeesAllowable", expenses.professionalFeesAllowable),
          resolveAdjusted("/expenses/depreciationAllowable", expenses.depreciationAllowable),
          resolveAdjusted("/expenses/otherExpensesAllowable", expenses.otherExpensesAllowable),
          resolveAdjusted("/expenses/advertisingCostsAllowable", expenses.advertisingCostsAllowable),
          resolveAdjusted("/expenses/businessEntertainmentCostsAllowable", expenses.businessEntertainmentCostsAllowable),
          resolveAdjusted("/expenses/consolidatedExpenses", expenses.consolidatedExpenses),
        )

      private def validateAdditions(additions: Additions): Either[Seq[MtdError], SubmitSelfEmploymentBsasRequestData] =
        combine(
          parsed,
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
          resolveAdjusted("/additions/businessEntertainmentCostsDisallowable", additions.businessEntertainmentCostsDisallowable),
        )

      private def validateBothExpenses(expenses: Expenses,
                                       additions: Option[Additions]): Either[Seq[MtdError], SubmitSelfEmploymentBsasRequestData] = {

        def bothExpensesError = List(RuleBothExpensesError.withPath("/expenses"))

        (expenses.consolidatedExpenses, additions) match {
          case (None, _)                                                                       => Right(parsed)
          case (Some(_), None) if expenses.hasOnlyConsolidatedExpenses                         => Right(parsed)
          case (Some(_), None)                                                                 => Left(bothExpensesError)
          case (Some(_), Some(adds)) if !expenses.hasOnlyConsolidatedExpenses || adds.nonEmpty => Left(bothExpensesError)
          case (Some(_), Some(_))                                                              => Right(parsed)
        }
      }

    }

}
