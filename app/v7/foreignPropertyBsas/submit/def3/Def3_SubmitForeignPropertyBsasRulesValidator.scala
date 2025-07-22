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

package v7.foreignPropertyBsas.submit.def3

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.toFoldableOps
import common.errors.*
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.{ResolveParsedCountryCode, ResolveParsedNumber}
import shared.models.errors.MtdError
import v7.foreignPropertyBsas.submit.def3.model.request.*

object Def3_SubmitForeignPropertyBsasRulesValidator extends RulesValidator[Def3_SubmitForeignPropertyBsasRequestData] {

  def validateBusinessRules(
      parsed: Def3_SubmitForeignPropertyBsasRequestData): Validated[Seq[MtdError], Def3_SubmitForeignPropertyBsasRequestData] = {
    import parsed.body

    val validatedZeroAdjustments = validateZeroAdjustments(body.foreignProperty)

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
      validatedZeroAdjustments,
      validatedCountryLevelDetails,
      validatedDuplicateCountryCode
    ).onSuccess(parsed)
  }

  private def validateZeroAdjustments(foreignProperty: Option[ForeignProperty]): Validated[Seq[MtdError], Unit] = {
    val zeroAdjustments: Option[Boolean] = foreignProperty.flatMap(_.zeroAdjustments)
    val hasAdjustableFields: Boolean = foreignProperty.exists { prop =>
      prop.countryLevelDetail.exists(_.exists(detail => detail.income.isDefined || detail.expenses.isDefined))
    }

    (zeroAdjustments, hasAdjustableFields) match {
      case (Some(true), true) => Invalid(List(RuleBothAdjustmentsSuppliedError.withPath("/foreignProperty")))

      case (Some(false), true) =>
        Invalid(
          List(
            RuleBothAdjustmentsSuppliedError.withPath("/foreignProperty"),
            RuleZeroAdjustmentsInvalidError.withPath("/foreignProperty/zeroAdjustments")
          )
        )

      case (Some(false), false) => Invalid(List(RuleZeroAdjustmentsInvalidError.withPath("/foreignProperty/zeroAdjustments")))

      case _ => valid
    }
  }

  private def resolveNonNegativeNumber(path: String, value: Option[BigDecimal]): Validated[Seq[MtdError], Option[BigDecimal]] =
    ResolveParsedNumber(disallowZero = true)(value, path)

  private def resolveMaybeNegativeNumber(path: String, value: Option[BigDecimal]): Validated[Seq[MtdError], Option[BigDecimal]] =
    ResolveParsedNumber(min = -99999999999.99, disallowZero = true)(value, path)

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
      resolveNonNegativeNumber(path("expenses/residentialFinancialCost"), countryLevelDetail.expenses.flatMap(_.residentialFinancialCost)),
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
