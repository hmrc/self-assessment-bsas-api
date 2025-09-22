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

package v7.foreignPropertyBsas.submit.def2

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.toFoldableOps
import common.errors.*
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.{ResolveParsedCountryCode, ResolveParsedNumber}
import shared.models.errors.MtdError
import v7.foreignPropertyBsas.submit.def2.model.request.*

object Def2_SubmitForeignPropertyBsasRulesValidator extends RulesValidator[Def2_SubmitForeignPropertyBsasRequestData] {

  def validateBusinessRules(
      parsed: Def2_SubmitForeignPropertyBsasRequestData): Validated[Seq[MtdError], Def2_SubmitForeignPropertyBsasRequestData] = {
    import parsed.body

    val validatedForeignFhlEeaZeroAdjustments = validateForeignFhlEeaZeroAdjustments(body.foreignFhlEea)

    val validatedForeignPropertyZeroAdjustments = validateForeignPropertyZeroAdjustments(body.foreignProperty)

    val (validatedForeignFhlEea, validatedForeignFhlEeaConsolidated) = body.foreignFhlEea match {
      case Some(fhlEea) =>
        (
          validateForeignFhlEea(fhlEea),
          validateForeignFhlEeaConsolidatedExpenses(fhlEea)
        )

      case None =>
        (valid, valid)
    }

    val (validatedCountryLevelDetails, validatedDuplicateCountryCode) = body.foreignProperty match {
      case Some(foreignProperty) =>
        val countryLevelDetailsWithIndex = foreignProperty.countryLevelDetail.getOrElse(Seq.empty).zipWithIndex
        (
          validateCountryLevelDetails(countryLevelDetailsWithIndex),
          duplicateCountryCodeValidation(countryLevelDetailsWithIndex)
        )

      case None =>
        (valid, valid)
    }

    combine(
      validatedForeignFhlEeaZeroAdjustments,
      validatedForeignPropertyZeroAdjustments,
      validatedForeignFhlEea,
      validatedForeignFhlEeaConsolidated,
      validatedCountryLevelDetails,
      validatedDuplicateCountryCode
    ).onSuccess(parsed)
  }

  private def validateZeroAdjustments(zeroAdjustments: Option[Boolean], hasAdjustableFields: Boolean, path: String): Validated[Seq[MtdError], Unit] =
    (zeroAdjustments, hasAdjustableFields) match {
      case (Some(true), true) => Invalid(List(RuleBothAdjustmentsSuppliedError.withPath(path)))

      case (Some(false), true) =>
        Invalid(
          List(
            RuleBothAdjustmentsSuppliedError.withPath(path),
            RuleZeroAdjustmentsInvalidError.withPath(s"$path/zeroAdjustments")
          )
        )

      case (Some(false), false) => Invalid(List(RuleZeroAdjustmentsInvalidError.withPath(s"$path/zeroAdjustments")))

      case _ => valid
    }

  private def validateForeignFhlEeaZeroAdjustments(foreignFhlEea: Option[FhlEea]): Validated[Seq[MtdError], Unit] = {
    val zeroAdjustments: Option[Boolean] = foreignFhlEea.flatMap(_.zeroAdjustments)
    val hasAdjustableFields: Boolean     = foreignFhlEea.exists(fhl => fhl.income.isDefined || fhl.expenses.isDefined)

    validateZeroAdjustments(zeroAdjustments, hasAdjustableFields, "/foreignFhlEea")
  }

  private def validateForeignPropertyZeroAdjustments(foreignProperty: Option[ForeignProperty]): Validated[Seq[MtdError], Unit] = {
    val zeroAdjustments: Option[Boolean] = foreignProperty.flatMap(_.zeroAdjustments)
    val hasAdjustableFields: Boolean = foreignProperty.exists { prop =>
      prop.countryLevelDetail.exists(_.exists(detail => detail.income.isDefined || detail.expenses.isDefined))
    }

    validateZeroAdjustments(zeroAdjustments, hasAdjustableFields, "/foreignProperty")
  }

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

  private def validateForeignFhlEeaConsolidatedExpenses(foreignFhlEea: FhlEea): Validated[Seq[MtdError], Unit] =
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

  private def validateCountryLevelDetails(countryLevelDetails: Seq[(CountryLevelDetail, Int)]): Validated[Seq[MtdError], Unit] =
    countryLevelDetails.traverse_ { case (countryLevelDetail, i) =>
      validateCountryLevelDetail(countryLevelDetail, i)
        .combine(validateCountryLevelDetailConsolidatedExpenses(countryLevelDetail, i))
    }

  private def duplicateCountryCodeValidation(countryLevelDetails: Seq[(CountryLevelDetail, Int)]): Validated[Seq[MtdError], Unit] = {
    val duplicateErrors = {
      countryLevelDetails
        .map { case (entry, idx) =>
          (entry.countryCode, s"/foreignProperty/countryLevelDetail/$idx/countryCode")
        }
        .groupBy(_._1)
        .collect {
          case (code, codeAndPaths) if codeAndPaths.size >= 2 =>
            RuleDuplicateCountryCodeError.forDuplicatedCodesAndPaths(code, codeAndPaths.map(_._2))
        }
    }

    if (duplicateErrors.nonEmpty) Invalid(duplicateErrors.toList) else valid
  }

  private def validateCountryLevelDetail(countryLevelDetail: CountryLevelDetail, index: Int): Validated[Seq[MtdError], Unit] = {

    def path(suffix: String) = s"/foreignProperty/countryLevelDetail/$index/$suffix"

    combine(
      ResolveParsedCountryCode(countryLevelDetail.countryCode, path("countryCode")),
      resolveMaybeNegativeNumber(path("income/totalRentsReceived"), countryLevelDetail.income.flatMap(_.totalRentsReceived)),
      resolveMaybeNegativeNumber(path("income/premiumsOfLeaseGrant"), countryLevelDetail.income.flatMap(_.premiumsOfLeaseGrant)),
      resolveMaybeNegativeNumber(path("income/otherPropertyIncome"), countryLevelDetail.income.flatMap(_.otherPropertyIncome)),
      resolveMaybeNegativeNumber(path("expenses/premisesRunningCosts"), countryLevelDetail.expenses.flatMap(_.premisesRunningCosts)),
      resolveMaybeNegativeNumber(path("expenses/repairsAndMaintenance"), countryLevelDetail.expenses.flatMap(_.repairsAndMaintenance)),
      resolveMaybeNegativeNumber(path("expenses/financialCosts"), countryLevelDetail.expenses.flatMap(_.financialCosts)),
      resolveMaybeNegativeNumber(path("expenses/professionalFees"), countryLevelDetail.expenses.flatMap(_.professionalFees)),
      resolveMaybeNegativeNumber(path("expenses/travelCosts"), countryLevelDetail.expenses.flatMap(_.travelCosts)),
      resolveMaybeNegativeNumber(path("expenses/costOfServices"), countryLevelDetail.expenses.flatMap(_.costOfServices)),
      resolveMaybeNegativeNumber(path("expenses/residentialFinancialCost"), countryLevelDetail.expenses.flatMap(_.residentialFinancialCost)),
      resolveMaybeNegativeNumber(path("expenses/other"), countryLevelDetail.expenses.flatMap(_.other)),
      resolveMaybeNegativeNumber(path("expenses/consolidatedExpenses"), countryLevelDetail.expenses.flatMap(_.consolidatedExpenses))
    )
  }

  private def validateCountryLevelDetailConsolidatedExpenses(countryLevelDetail: CountryLevelDetail, index: Int): Validated[Seq[MtdError], Unit] =
    countryLevelDetail.expenses
      .collect {
        case expenses if expenses.consolidatedExpenses.isDefined =>
          expenses match {
            case ForeignPropertyExpenses(None, None, None, None, None, None, _, None, Some(_)) =>
              valid
            case _ =>
              Invalid(List(RuleBothExpensesError.copy(paths = Some(List(s"/foreignProperty/countryLevelDetail/$index/expenses")))))
          }
      }
      .getOrElse(valid)

}
