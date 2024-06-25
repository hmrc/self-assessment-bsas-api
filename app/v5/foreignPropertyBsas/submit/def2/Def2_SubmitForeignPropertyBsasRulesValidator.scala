/*
 * Copyright 2024 HM Revenue & Customs
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

package v5.foreignPropertyBsas.submit.def2

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.toFoldableOps
import common.errors.{RuleBothExpensesError, RuleDuplicateCountryCodeError}
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.{ResolveParsedCountryCode, ResolveParsedNumber}
import shared.models.errors.MtdError
import v5.foreignPropertyBsas.submit.def2.model.request.{FhlEea, FhlEeaExpenses, ForeignProperty, ForeignPropertyExpenses}
import v5.foreignPropertyBsas.submit.def2.model.request.Def2_SubmitForeignPropertyBsasRequestData

object Def2_SubmitForeignPropertyBsasRulesValidator extends RulesValidator[Def2_SubmitForeignPropertyBsasRequestData] {

  def validateBusinessRules(
      parsed: Def2_SubmitForeignPropertyBsasRequestData): Validated[Seq[MtdError], Def2_SubmitForeignPropertyBsasRequestData] = {
    import parsed.body

    val (validatedForeignFhlEea, validatedForeignFhlEeaConsolidated) = body.foreignFhlEea match {
      case Some(fhlEea) =>
        (
          validateForeignFhlEea(fhlEea),
          validateForeignFhlEeaConsolidatedExpenses(fhlEea)
        )

      case None =>
        (valid, valid)
    }

    val (validatedNonFurnishedHolidayLets, validatedDuplicateCountryCode) = body.nonFurnishedHolidayLet match {
      case Some(foreignProperties) =>
        val foreignPropertiesWithIndex = foreignProperties.zipWithIndex.toList
        (
          validateNonFurnishedHolidayLets(foreignPropertiesWithIndex),
          duplicateCountryCodeValidation(foreignPropertiesWithIndex)
        )

      case None =>
        (valid, valid)
    }

    combine(
      validatedForeignFhlEea,
      validatedForeignFhlEeaConsolidated,
      validatedNonFurnishedHolidayLets,
      validatedDuplicateCountryCode
    ).onSuccess(parsed)
  }

  private def resolveNonNegativeNumber(path: String, value: Option[BigDecimal]): Validated[Seq[MtdError], Option[BigDecimal]] =
    ResolveParsedNumber(disallowZero = true)(value, path)

  private def resolveMaybeNegativeNumber(path: String, value: Option[BigDecimal]): Validated[Seq[MtdError], Option[BigDecimal]] =
    ResolveParsedNumber(min = -99999999999.99, disallowZero = true)(value, path)

  private def validateForeignFhlEea(foreignFhlEea: FhlEea): Validated[Seq[MtdError], Unit] =
    combine(
      resolveMaybeNegativeNumber("/foreignFhlEea/income/totalRentsReceived", foreignFhlEea.income.flatMap(_.totalRentsReceived)),
      resolveMaybeNegativeNumber("/foreignFhlEea/expenses/premisesRunningCosts", foreignFhlEea.expenses.flatMap(_.premisesRunningCosts)),
      resolveMaybeNegativeNumber("/foreignFhlEea/expenses/repairsAndMaintenance", foreignFhlEea.expenses.flatMap(_.repairsAndMaintenance)),
      resolveMaybeNegativeNumber("/foreignFhlEea/expenses/financialCosts", foreignFhlEea.expenses.flatMap(_.financialCosts)),
      resolveMaybeNegativeNumber("/foreignFhlEea/expenses/professionalFees", foreignFhlEea.expenses.flatMap(_.professionalFees)),
      resolveMaybeNegativeNumber("/foreignFhlEea/expenses/travelCosts", foreignFhlEea.expenses.flatMap(_.travelCosts)),
      resolveMaybeNegativeNumber("/foreignFhlEea/expenses/costOfServices", foreignFhlEea.expenses.flatMap(_.costOfServices)),
      resolveMaybeNegativeNumber("/foreignFhlEea/expenses/other", foreignFhlEea.expenses.flatMap(_.other)),
      resolveMaybeNegativeNumber("/foreignFhlEea/expenses/consolidatedExpenses", foreignFhlEea.expenses.flatMap(_.consolidatedExpenses))
    )

  private def validateForeignFhlEeaConsolidatedExpenses(foreignFhlEea: FhlEea): Validated[Seq[MtdError], Unit] = {
    foreignFhlEea.expenses
      .collect {
        case expenses if expenses.consolidatedExpenses.isDefined =>
          expenses match {
            case FhlEeaExpenses(None, None, None, None, None, None, None, Some(_)) =>
              valid
            case _ =>
              Invalid(List(RuleBothExpensesError.copy(paths = Some(List("/foreignFhlEea/expenses")))))
          }
      }
      .getOrElse(valid)
  }

  private def validateNonFurnishedHolidayLets(foreignProperties: Seq[(ForeignProperty, Int)]): Validated[Seq[MtdError], Unit] = {
    foreignProperties.traverse_ { case (foreignProperty, i) =>
      validateForeignProperty(foreignProperty, i)
        .combine(validateForeignPropertyConsolidatedExpenses(foreignProperty, i))
    }
  }

  private def duplicateCountryCodeValidation(foreignProperties: Seq[(ForeignProperty, Int)]): Validated[Seq[MtdError], Unit] = {
    val duplicateErrors = {
      foreignProperties
        .map { case (entry, idx) =>
          (entry.countryCode, s"/nonFurnishedHolidayLet/$idx/countryCode")
        }
        .groupBy(_._1)
        .collect {
          case (code, codeAndPaths) if codeAndPaths.size >= 2 =>
            RuleDuplicateCountryCodeError.forDuplicatedCodesAndPaths(code, codeAndPaths.map(_._2))
        }
    }

    if (duplicateErrors.nonEmpty)
      Invalid(duplicateErrors.toList)
    else
      valid
  }

  private def validateForeignProperty(foreignProperty: ForeignProperty, index: Int): Validated[Seq[MtdError], Unit] = {

    def path(suffix: String) = s"/nonFurnishedHolidayLet/$index/$suffix"

    combine(
      ResolveParsedCountryCode(foreignProperty.countryCode, path("countryCode")),
      resolveMaybeNegativeNumber(path("income/totalRentsReceived"), foreignProperty.income.flatMap(_.totalRentsReceived)),
      resolveMaybeNegativeNumber(path("income/premiumsOfLeaseGrant"), foreignProperty.income.flatMap(_.premiumsOfLeaseGrant)),
      resolveMaybeNegativeNumber(path("income/otherPropertyIncome"), foreignProperty.income.flatMap(_.otherPropertyIncome)),
      resolveMaybeNegativeNumber(path("expenses/premisesRunningCosts"), foreignProperty.expenses.flatMap(_.premisesRunningCosts)),
      resolveMaybeNegativeNumber(path("expenses/repairsAndMaintenance"), foreignProperty.expenses.flatMap(_.repairsAndMaintenance)),
      resolveMaybeNegativeNumber(path("expenses/financialCosts"), foreignProperty.expenses.flatMap(_.financialCosts)),
      resolveMaybeNegativeNumber(path("expenses/professionalFees"), foreignProperty.expenses.flatMap(_.professionalFees)),
      resolveMaybeNegativeNumber(path("expenses/travelCosts"), foreignProperty.expenses.flatMap(_.travelCosts)),
      resolveMaybeNegativeNumber(path("expenses/costOfServices"), foreignProperty.expenses.flatMap(_.costOfServices)),
      resolveNonNegativeNumber(path("expenses/residentialFinancialCost"), foreignProperty.expenses.flatMap(_.residentialFinancialCost)),
      resolveMaybeNegativeNumber(path("expenses/other"), foreignProperty.expenses.flatMap(_.other)),
      resolveMaybeNegativeNumber(path("expenses/consolidatedExpenses"), foreignProperty.expenses.flatMap(_.consolidatedExpenses))
    )
  }

  private def validateForeignPropertyConsolidatedExpenses(foreignProperty: ForeignProperty, index: Int): Validated[Seq[MtdError], Unit] = {

    foreignProperty.expenses
      .collect {
        case expenses if expenses.consolidatedExpenses.isDefined =>
          expenses match {
            case ForeignPropertyExpenses(None, None, None, None, None, None, _, None, Some(_)) =>
              valid
            case _ =>
              Invalid(List(RuleBothExpensesError.copy(paths = Some(List(s"/nonFurnishedHolidayLet/$index/expenses")))))
          }
      }
      .getOrElse(valid)
  }

}
