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

package v7.ukPropertyBsas.submit.def2

import cats.data.Validated
import cats.data.Validated.Invalid
import common.errors.{RuleBothAdjustmentsSuppliedError, RuleBothExpensesError, RuleZeroAdjustmentsInvalidError}
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.ResolveParsedNumber
import shared.models.errors.MtdError
import v7.ukPropertyBsas.submit.def2.model.request.{Def2_SubmitUkPropertyBsasRequestData, Expenses, FHLExpenses, FurnishedHolidayLet, UkProperty}

object Def2_SubmitUkPropertyBsasRulesValidator extends RulesValidator[Def2_SubmitUkPropertyBsasRequestData] {

  def validateBusinessRules(parsed: Def2_SubmitUkPropertyBsasRequestData): Validated[Seq[MtdError], Def2_SubmitUkPropertyBsasRequestData] = {
    import parsed.body

    val validatedZeroAdjustments = validateZeroAdjustments(body.ukProperty, body.furnishedHolidayLet)

    val (validatedFhl, validatedFhlConsolidated) = body.furnishedHolidayLet match {
      case Some(fhl) =>
        (
          validateFhl(fhl),
          validateFhlConsolidatedExpenses(fhl)
        )
      case None =>
        (valid, valid)
    }

    val (validatedUkProperty, validatedUkPropertyConsolidated) = body.ukProperty match {
      case Some(ukProperty) =>
        (
          validateUkProperty(ukProperty),
          validateUkPropertyConsolidatedExpenses(ukProperty)
        )
      case None =>
        (valid, valid)
    }

    combine(
      validatedZeroAdjustments,
      validatedFhl,
      validatedFhlConsolidated,
      validatedUkProperty,
      validatedUkPropertyConsolidated
    ).onSuccess(parsed)
  }

  private def validateZeroAdjustments(ukProperty: Option[UkProperty],
                                      furnishedHolidayLet: Option[FurnishedHolidayLet]): Validated[Seq[MtdError], Unit] = {
    val zeroAdjustmentsFromUkProperty: Option[Boolean] = ukProperty.flatMap(_.zeroAdjustments)
    val zeroAdjustmentsFromFhl: Option[Boolean]        = furnishedHolidayLet.flatMap(_.zeroAdjustments)

    val path: Option[String] =
      zeroAdjustmentsFromUkProperty
        .map(_ => "/ukProperty")
        .orElse(zeroAdjustmentsFromFhl.map(_ => "/furnishedHolidayLet"))

    val zeroAdjustments: Option[Boolean] = zeroAdjustmentsFromUkProperty.orElse(zeroAdjustmentsFromFhl)

    val hasAdjustableFields: Boolean = ukProperty.exists(prop => prop.income.isDefined || prop.expenses.isDefined) ||
      furnishedHolidayLet.exists(fhl => fhl.income.isDefined || fhl.expenses.isDefined)

    (zeroAdjustments, hasAdjustableFields) match {
      case (Some(true), true) => Invalid(List(RuleBothAdjustmentsSuppliedError.withPath(path.get)))

      case (Some(false), true) =>
        Invalid(
          List(
            RuleBothAdjustmentsSuppliedError.withPath(path.get),
            RuleZeroAdjustmentsInvalidError.withPath(s"${path.get}/zeroAdjustments")
          )
        )

      case (Some(false), false) => Invalid(List(RuleZeroAdjustmentsInvalidError.withPath(s"${path.get}/zeroAdjustments")))

      case _ => valid
    }
  }

  private def resolveNonNegativeNumber(path: String, value: Option[BigDecimal]): Validated[Seq[MtdError], Option[BigDecimal]] =
    ResolveParsedNumber(disallowZero = true)(value, path)

  private def resolveMaybeNegativeNumber(path: String, value: Option[BigDecimal]): Validated[Seq[MtdError], Option[BigDecimal]] =
    ResolveParsedNumber(min = -99999999999.99, disallowZero = true)(value, path)

  private def validateFhl(fhl: FurnishedHolidayLet): Validated[Seq[MtdError], Unit] = {
    combine(
      resolveMaybeNegativeNumber("/furnishedHolidayLet/income/totalRentsReceived", fhl.income.flatMap(_.totalRentsReceived)),
      resolveMaybeNegativeNumber("/furnishedHolidayLet/expenses/premisesRunningCosts", fhl.expenses.flatMap(_.premisesRunningCosts)),
      resolveMaybeNegativeNumber("/furnishedHolidayLet/expenses/repairsAndMaintenance", fhl.expenses.flatMap(_.repairsAndMaintenance)),
      resolveMaybeNegativeNumber("/furnishedHolidayLet/expenses/financialCosts", fhl.expenses.flatMap(_.financialCosts)),
      resolveMaybeNegativeNumber("/furnishedHolidayLet/expenses/professionalFees", fhl.expenses.flatMap(_.professionalFees)),
      resolveMaybeNegativeNumber("/furnishedHolidayLet/expenses/travelCosts", fhl.expenses.flatMap(_.travelCosts)),
      resolveMaybeNegativeNumber("/furnishedHolidayLet/expenses/costOfServices", fhl.expenses.flatMap(_.costOfServices)),
      resolveMaybeNegativeNumber("/furnishedHolidayLet/expenses/other", fhl.expenses.flatMap(_.other)),
      resolveMaybeNegativeNumber("/furnishedHolidayLet/expenses/consolidatedExpenses", fhl.expenses.flatMap(_.consolidatedExpenses))
    )
  }

  private def validateUkProperty(ukProperty: UkProperty): Validated[Seq[MtdError], Unit] = {
    combine(
      resolveMaybeNegativeNumber("/ukProperty/income/totalRentsReceived", ukProperty.income.flatMap(_.totalRentsReceived)),
      resolveMaybeNegativeNumber("/ukProperty/income/premiumsOfLeaseGrant", ukProperty.income.flatMap(_.premiumsOfLeaseGrant)),
      resolveMaybeNegativeNumber("/ukProperty/income/reversePremiums", ukProperty.income.flatMap(_.reversePremiums)),
      resolveMaybeNegativeNumber("/ukProperty/income/otherPropertyIncome", ukProperty.income.flatMap(_.otherPropertyIncome)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/premisesRunningCosts", ukProperty.expenses.flatMap(_.premisesRunningCosts)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/repairsAndMaintenance", ukProperty.expenses.flatMap(_.repairsAndMaintenance)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/financialCosts", ukProperty.expenses.flatMap(_.financialCosts)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/professionalFees", ukProperty.expenses.flatMap(_.professionalFees)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/travelCosts", ukProperty.expenses.flatMap(_.travelCosts)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/costOfServices", ukProperty.expenses.flatMap(_.costOfServices)),
      resolveNonNegativeNumber("/ukProperty/expenses/residentialFinancialCost", ukProperty.expenses.flatMap(_.residentialFinancialCost)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/other", ukProperty.expenses.flatMap(_.other)),
      resolveMaybeNegativeNumber("/ukProperty/expenses/consolidatedExpenses", ukProperty.expenses.flatMap(_.consolidatedExpenses))
    )
  }

  private def validateFhlConsolidatedExpenses(fhl: FurnishedHolidayLet): Validated[Seq[MtdError], Unit] = {
    fhl.expenses
      .collect {
        case expenses if expenses.consolidatedExpenses.isDefined =>
          expenses match {
            case FHLExpenses(None, None, None, None, None, None, None, Some(_)) =>
              valid
            case _ =>
              Invalid(List(RuleBothExpensesError.withPath("/furnishedHolidayLet/expenses")))
          }
      }
      .getOrElse(valid)
  }

  private def validateUkPropertyConsolidatedExpenses(ukProperty: UkProperty): Validated[Seq[MtdError], Unit] = {
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
