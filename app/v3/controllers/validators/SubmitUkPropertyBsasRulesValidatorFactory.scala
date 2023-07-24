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
import v3.models.request.submitBsas.ukProperty._

import javax.inject.Singleton

@Singleton
class SubmitUkPropertyBsasRulesValidatorFactory {

  def validator(parsed: SubmitUkPropertyBsasRequestData): Validator[SubmitUkPropertyBsasRequestData] =
    new Validator[SubmitUkPropertyBsasRequestData] {

      def validate: Either[Seq[MtdError], SubmitUkPropertyBsasRequestData] = {
        val validatedFurnishedHolidayLets = parsed.body.furnishedHolidayLet match {
          case Some(fhl) =>
            combine(
              parsed,
              validateFhl(fhl),
              validateFhlConsolidatedExpenses(fhl)
            )
          case None =>
            Right(parsed)
        }

        val validatedNonFurnishedHolidayLets = parsed.body.nonFurnishedHolidayLet match {
          case Some(ukProperty) =>
            combine(
              parsed,
              validateNonFhl(ukProperty),
              validateNonFhlConsolidatedExpenses(ukProperty)
            )
          case None =>
            Right(parsed)
        }

        combine(
          parsed,
          validatedFurnishedHolidayLets,
          validatedNonFurnishedHolidayLets
        )
      }

      private val resolveAdjustment = ResolveParsedNumber(min = -99999999999.99, disallowZero = true)

      private def resolveAdjusted(path: String, value: Option[BigDecimal]): Either[Seq[MtdError], Option[BigDecimal]] =
        resolveAdjustment(value, path = Some(path))

      private def validateFhl(fhl: FurnishedHolidayLet): Either[Seq[MtdError], SubmitUkPropertyBsasRequestData] = {
        combine(
          parsed,
          resolveAdjusted("/furnishedHolidayLet/income/totalRentsReceived", fhl.income.flatMap(_.totalRentsReceived)),
          resolveAdjusted("/furnishedHolidayLet/expenses/premisesRunningCosts", fhl.expenses.flatMap(_.premisesRunningCosts)),
          resolveAdjusted("/furnishedHolidayLet/expenses/repairsAndMaintenance", fhl.expenses.flatMap(_.repairsAndMaintenance)),
          resolveAdjusted("/furnishedHolidayLet/expenses/financialCosts", fhl.expenses.flatMap(_.financialCosts)),
          resolveAdjusted("/furnishedHolidayLet/expenses/professionalFees", fhl.expenses.flatMap(_.professionalFees)),
          resolveAdjusted("/furnishedHolidayLet/expenses/travelCosts", fhl.expenses.flatMap(_.travelCosts)),
          resolveAdjusted("/furnishedHolidayLet/expenses/costOfServices", fhl.expenses.flatMap(_.costOfServices)),
          resolveAdjusted("/furnishedHolidayLet/expenses/other", fhl.expenses.flatMap(_.other)),
          resolveAdjusted("/furnishedHolidayLet/expenses/consolidatedExpenses", fhl.expenses.flatMap(_.consolidatedExpenses))
        )
      }

      private def validateNonFhl(nonFhl: NonFurnishedHolidayLet): Either[Seq[MtdError], SubmitUkPropertyBsasRequestData] = {
        combine(
          parsed,
          resolveAdjusted("/nonFurnishedHolidayLet/income/totalRentsReceived", nonFhl.income.flatMap(_.totalRentsReceived)),
          resolveAdjusted("/nonFurnishedHolidayLet/income/premiumsOfLeaseGrant", nonFhl.income.flatMap(_.premiumsOfLeaseGrant)),
          resolveAdjusted("/nonFurnishedHolidayLet/income/reversePremiums", nonFhl.income.flatMap(_.reversePremiums)),
          resolveAdjusted("/nonFurnishedHolidayLet/income/otherPropertyIncome", nonFhl.income.flatMap(_.otherPropertyIncome)),
          resolveAdjusted("/nonFurnishedHolidayLet/expenses/premisesRunningCosts", nonFhl.expenses.flatMap(_.premisesRunningCosts)),
          resolveAdjusted("/nonFurnishedHolidayLet/expenses/repairsAndMaintenance", nonFhl.expenses.flatMap(_.repairsAndMaintenance)),
          resolveAdjusted("/nonFurnishedHolidayLet/expenses/financialCosts", nonFhl.expenses.flatMap(_.financialCosts)),
          resolveAdjusted("/nonFurnishedHolidayLet/expenses/professionalFees", nonFhl.expenses.flatMap(_.professionalFees)),
          resolveAdjusted("/nonFurnishedHolidayLet/expenses/travelCosts", nonFhl.expenses.flatMap(_.travelCosts)),
          resolveAdjusted("/nonFurnishedHolidayLet/expenses/costOfServices", nonFhl.expenses.flatMap(_.costOfServices)),
          resolveAdjusted("/nonFurnishedHolidayLet/expenses/residentialFinancialCost", nonFhl.expenses.flatMap(_.residentialFinancialCost)),
          resolveAdjusted("/nonFurnishedHolidayLet/expenses/other", nonFhl.expenses.flatMap(_.other)),
          resolveAdjusted("/nonFurnishedHolidayLet/expenses/consolidatedExpenses", nonFhl.expenses.flatMap(_.consolidatedExpenses))
        )
      }

      private def validateFhlConsolidatedExpenses(fhl: FurnishedHolidayLet): Either[Seq[MtdError], SubmitUkPropertyBsasRequestData] = {
        fhl.expenses
          .collect {
            case expenses if expenses.consolidatedExpenses.isDefined =>
              expenses match {
                case FHLExpenses(None, None, None, None, None, None, None, Some(_)) =>
                  Right(parsed)
                case _ =>
                  Left(List(RuleBothExpensesError.withPath("/furnishedHolidayLet/expenses")))
              }
          }
          .getOrElse(Right(parsed))
      }

      private def validateNonFhlConsolidatedExpenses(nonFhl: NonFurnishedHolidayLet): Either[Seq[MtdError], SubmitUkPropertyBsasRequestData] = {
        nonFhl.expenses
          .collect {
            case expenses if expenses.consolidatedExpenses.isDefined =>
              expenses match {
                case NonFHLExpenses(None, None, None, None, None, None, None, None, Some(_)) =>
                  Right(parsed)
                case _ =>
                  Left(List(RuleBothExpensesError.withPath("/nonFurnishedHolidayLet/expenses")))
              }
          }
          .getOrElse(Right(parsed))
      }
    }
}
