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

package v7.foreignPropertyBsas.submit.def4

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.toFoldableOps
import common.errors.*
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.{ResolveParsedNumber, ResolveUuid}
import shared.models.errors.{MtdError, PropertyIdFormatError}
import v7.common.model.PropertyId
import v7.foreignPropertyBsas.submit.def4.model.request.*

object Def4_SubmitForeignPropertyBsasRulesValidator extends RulesValidator[Def4_SubmitForeignPropertyBsasRequestData] {

  def validateBusinessRules(
      parsed: Def4_SubmitForeignPropertyBsasRequestData): Validated[Seq[MtdError], Def4_SubmitForeignPropertyBsasRequestData] = {
    import parsed.body

    val validatedZeroAdjustments = validateZeroAdjustments(body.foreignProperty)

    val (validatedPropertyLevelDetails, validatedDuplicatePropertyId) = body.foreignProperty match {
      case Some(foreignProperty) =>
        val propertyLevelDetailsWithIndex = foreignProperty.propertyLevelDetail.getOrElse(Seq.empty).zipWithIndex
        (
          validatePropertyLevelDetails(propertyLevelDetailsWithIndex),
          duplicatePropertyIdValidation(propertyLevelDetailsWithIndex)
        )

      case None => (valid, valid)
    }

    combine(
      validatedZeroAdjustments,
      validatedPropertyLevelDetails,
      validatedDuplicatePropertyId
    ).onSuccess(parsed)
  }

  private def validateZeroAdjustments(foreignProperty: Option[ForeignProperty]): Validated[Seq[MtdError], Unit] = {
    val zeroAdjustments: Option[Boolean] = foreignProperty.flatMap(_.zeroAdjustments)
    val hasAdjustableFields: Boolean = foreignProperty.exists { prop =>
      prop.propertyLevelDetail.exists(_.exists(detail => detail.income.isDefined || detail.expenses.isDefined))
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

  private def resolveMaybeNegativeNumber(path: String, value: Option[BigDecimal]): Validated[Seq[MtdError], Option[BigDecimal]] =
    ResolveParsedNumber(min = -99999999999.99, disallowZero = true)(value, path)

  private def resolvePropertyId(path: String, value: PropertyId): Validated[Seq[MtdError], PropertyId] =
    ResolveUuid[PropertyId](value.toString, PropertyIdFormatError.withPath(path))(PropertyId(_))

  private def validatePropertyLevelDetails(propertyLevelDetails: Seq[(PropertyLevelDetail, Int)]): Validated[Seq[MtdError], Unit] =
    propertyLevelDetails.traverse_ { case (propertyLevelDetail, i) =>
      validatePropertyLevelDetail(propertyLevelDetail, i)
        .combine(validatePropertyLevelDetailConsolidatedExpenses(propertyLevelDetail, i))
    }

  private def duplicatePropertyIdValidation(propertyLevelDetails: Seq[(PropertyLevelDetail, Int)]): Validated[Seq[MtdError], Unit] = {
    val duplicateErrors = propertyLevelDetails
      .groupMap(_._1.propertyId.propertyId) { case (_, index) =>
        s"/foreignProperty/propertyLevelDetail/$index/propertyId"
      }
      .collect {
        case (id, paths) if paths.size > 1 => RuleDuplicatePropertyIdError.forDuplicatedIdsAndPaths(id, paths)
      }

    if (duplicateErrors.nonEmpty) Invalid(duplicateErrors.toList) else valid
  }

  private def validatePropertyLevelDetail(propertyLevelDetail: PropertyLevelDetail, index: Int): Validated[Seq[MtdError], Unit] = {

    def path(suffix: String) = s"/foreignProperty/propertyLevelDetail/$index/$suffix"

    combine(
      resolvePropertyId(path("propertyId"), propertyLevelDetail.propertyId),
      resolveMaybeNegativeNumber(path("income/totalRentsReceived"), propertyLevelDetail.income.flatMap(_.totalRentsReceived)),
      resolveMaybeNegativeNumber(path("income/premiumsOfLeaseGrant"), propertyLevelDetail.income.flatMap(_.premiumsOfLeaseGrant)),
      resolveMaybeNegativeNumber(path("income/otherPropertyIncome"), propertyLevelDetail.income.flatMap(_.otherPropertyIncome)),
      resolveMaybeNegativeNumber(path("expenses/premisesRunningCosts"), propertyLevelDetail.expenses.flatMap(_.premisesRunningCosts)),
      resolveMaybeNegativeNumber(path("expenses/repairsAndMaintenance"), propertyLevelDetail.expenses.flatMap(_.repairsAndMaintenance)),
      resolveMaybeNegativeNumber(path("expenses/financialCosts"), propertyLevelDetail.expenses.flatMap(_.financialCosts)),
      resolveMaybeNegativeNumber(path("expenses/professionalFees"), propertyLevelDetail.expenses.flatMap(_.professionalFees)),
      resolveMaybeNegativeNumber(path("expenses/travelCosts"), propertyLevelDetail.expenses.flatMap(_.travelCosts)),
      resolveMaybeNegativeNumber(path("expenses/costOfServices"), propertyLevelDetail.expenses.flatMap(_.costOfServices)),
      resolveMaybeNegativeNumber(path("expenses/residentialFinancialCost"), propertyLevelDetail.expenses.flatMap(_.residentialFinancialCost)),
      resolveMaybeNegativeNumber(path("expenses/other"), propertyLevelDetail.expenses.flatMap(_.other)),
      resolveMaybeNegativeNumber(path("expenses/consolidatedExpenses"), propertyLevelDetail.expenses.flatMap(_.consolidatedExpenses))
    )
  }

  private def validatePropertyLevelDetailConsolidatedExpenses(propertyLevelDetail: PropertyLevelDetail, index: Int): Validated[Seq[MtdError], Unit] =
    propertyLevelDetail.expenses
      .collect {
        case expenses if expenses.consolidatedExpenses.isDefined =>
          expenses match {
            case ForeignPropertyExpenses(None, None, None, None, None, None, _, None, Some(_)) =>
              valid
            case _ =>
              Invalid(List(RuleBothExpensesError.copy(paths = Some(List(s"/foreignProperty/propertyLevelDetail/$index/expenses")))))
          }
      }
      .getOrElse(valid)

}
