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

import api.controllers.validators.RulesValidator
import api.controllers.validators.resolvers.{ResolveParsedCountryCode, ResolveParsedNumber}
import api.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated._
import cats.implicits._
import v3.models.errors.{RuleBothExpensesError, RuleDuplicateCountryCodeError}
import v3.models.request.submitBsas.foreignProperty._

object SubmitForeignPropertyBsasRulesValidator extends RulesValidator[SubmitForeignPropertyBsasRequestData] {

  def validateBusinessRules(parsed: SubmitForeignPropertyBsasRequestData): Validated[Seq[MtdError], SubmitForeignPropertyBsasRequestData] = {
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

    combineResults(
      parsed,
      validatedForeignFhlEea,
      validatedForeignFhlEeaConsolidated,
      validatedNonFurnishedHolidayLets,
      validatedDuplicateCountryCode
    )
  }

  private val resolveAdjustment = ResolveParsedNumber(min = -99999999999.99, disallowZero = true)

  private def resolveAdjusted(path: String, value: Option[BigDecimal]): Validated[Seq[MtdError], Unit] =
    resolveAdjustment(value, path = Some(path)).map(_ => ())

  private def validateForeignFhlEea(foreignFhlEea: FhlEea): Validated[Seq[MtdError], Unit] =
    combineProgress(
      resolveAdjusted("/foreignFhlEea/income/totalRentsReceived", foreignFhlEea.income.flatMap(_.totalRentsReceived)),
      resolveAdjusted("/foreignFhlEea/expenses/premisesRunningCosts", foreignFhlEea.expenses.flatMap(_.premisesRunningCosts)),
      resolveAdjusted("/foreignFhlEea/expenses/repairsAndMaintenance", foreignFhlEea.expenses.flatMap(_.repairsAndMaintenance)),
      resolveAdjusted("/foreignFhlEea/expenses/financialCosts", foreignFhlEea.expenses.flatMap(_.financialCosts)),
      resolveAdjusted("/foreignFhlEea/expenses/professionalFees", foreignFhlEea.expenses.flatMap(_.professionalFees)),
      resolveAdjusted("/foreignFhlEea/expenses/travelCosts", foreignFhlEea.expenses.flatMap(_.travelCosts)),
      resolveAdjusted("/foreignFhlEea/expenses/costOfServices", foreignFhlEea.expenses.flatMap(_.costOfServices)),
      resolveAdjusted("/foreignFhlEea/expenses/other", foreignFhlEea.expenses.flatMap(_.other)),
      resolveAdjusted("/foreignFhlEea/expenses/consolidatedExpenses", foreignFhlEea.expenses.flatMap(_.consolidatedExpenses))
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

    combineProgress(
      ResolveParsedCountryCode(foreignProperty.countryCode, path = Some(path("countryCode"))).map(_ => ()),
      resolveAdjusted(path("income/totalRentsReceived"), foreignProperty.income.flatMap(_.totalRentsReceived)),
      resolveAdjusted(path("income/premiumsOfLeaseGrant"), foreignProperty.income.flatMap(_.premiumsOfLeaseGrant)),
      resolveAdjusted(path("income/otherPropertyIncome"), foreignProperty.income.flatMap(_.otherPropertyIncome)),
      resolveAdjusted(path("expenses/premisesRunningCosts"), foreignProperty.expenses.flatMap(_.premisesRunningCosts)),
      resolveAdjusted(path("expenses/repairsAndMaintenance"), foreignProperty.expenses.flatMap(_.repairsAndMaintenance)),
      resolveAdjusted(path("expenses/financialCosts"), foreignProperty.expenses.flatMap(_.financialCosts)),
      resolveAdjusted(path("expenses/professionalFees"), foreignProperty.expenses.flatMap(_.professionalFees)),
      resolveAdjusted(path("expenses/travelCosts"), foreignProperty.expenses.flatMap(_.travelCosts)),
      resolveAdjusted(path("expenses/costOfServices"), foreignProperty.expenses.flatMap(_.costOfServices)),
      resolveAdjusted(path("expenses/residentialFinancialCost"), foreignProperty.expenses.flatMap(_.residentialFinancialCost)),
      resolveAdjusted(path("expenses/other"), foreignProperty.expenses.flatMap(_.other)),
      resolveAdjusted(path("expenses/consolidatedExpenses"), foreignProperty.expenses.flatMap(_.consolidatedExpenses))
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
