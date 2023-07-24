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
import api.controllers.validators.resolvers.{ ResolveParsedCountryCode, ResolveParsedNumber }
import api.models.errors.MtdError
import v3.models.errors.{ RuleBothExpensesError, RuleDuplicateCountryCodeError }
import v3.models.request.submitBsas.foreignProperty._

import javax.inject.Singleton

@Singleton class SubmitForeignPropertyBsasRulesValidatorFactory {

  def validator(parsed: SubmitForeignPropertyBsasRequestData): Validator[SubmitForeignPropertyBsasRequestData] =
    new Validator[SubmitForeignPropertyBsasRequestData] {

      def validate: Either[Seq[MtdError], SubmitForeignPropertyBsasRequestData] = {
        val validatedForeignFhlEea = parsed.body.foreignFhlEea match {
          case Some(fhlEea) =>
            combine(
              parsed,
              validateForeignFhlEea(fhlEea),
              validateForeignFhlEeaConsolidatedExpenses(fhlEea)
            )
          case None =>
            Right(parsed)
        }

        val validatedNonFurnishedHolidayLets = parsed.body.nonFurnishedHolidayLet match {
          case Some(foreignProperties) =>
            val foreignPropertiesWithIndex = foreignProperties.zipWithIndex.toList
            combine(
              parsed,
              validateNonFurnishedHolidayLets(foreignPropertiesWithIndex),
              duplicateCountryCodeValidation(foreignPropertiesWithIndex)
            )
          case None =>
            Right(parsed)
        }

        combine(
          parsed,
          validatedForeignFhlEea,
          validatedNonFurnishedHolidayLets
        )
      }

      private val resolveAdjustment = ResolveParsedNumber(min = -99999999999.99, disallowZero = true)

      private def resolveAdjusted(path: String, value: Option[BigDecimal]): Either[Seq[MtdError], Option[BigDecimal]] =
        resolveAdjustment(value, path = Some(path))

      private def validateForeignFhlEea(foreignFhlEea: FhlEea): Either[Seq[MtdError], SubmitForeignPropertyBsasRequestData] = {
        combine(
          parsed,
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
      }

      private def validateForeignFhlEeaConsolidatedExpenses(foreignFhlEea: FhlEea): Either[Seq[MtdError], SubmitForeignPropertyBsasRequestData] = {
        foreignFhlEea.expenses
          .collect {
            case expenses if expenses.consolidatedExpenses.isDefined =>
              expenses match {
                case FhlEeaExpenses(None, None, None, None, None, None, None, Some(_)) =>
                  Right(parsed)
                case _ =>
                  Left(List(RuleBothExpensesError.copy(paths = Some(List("/foreignFhlEea/expenses")))))
              }
          }
          .getOrElse(Right(parsed))
      }

      private def validateNonFurnishedHolidayLets(
          foreignProperties: Seq[(ForeignProperty, Int)]): Either[Seq[MtdError], SubmitForeignPropertyBsasRequestData] = {

        val validatedFhls = foreignProperties.map {
          case (foreignProperty, i) =>
            combine(
              parsed,
              validateForeignProperty(foreignProperty, i),
              validateForeignPropertyConsolidatedExpenses(foreignProperty, i)
            )
        }

        combine(parsed, validatedFhls: _*)
      }

      private def duplicateCountryCodeValidation(
          foreignProperties: Seq[(ForeignProperty, Int)]): Either[Seq[MtdError], SubmitForeignPropertyBsasRequestData] = {
        val duplicateErrors = {
          foreignProperties
            .map {
              case (entry, idx) => (entry.countryCode, s"/nonFurnishedHolidayLet/$idx/countryCode")
            }
            .groupBy(_._1)
            .collect {
              case (code, codeAndPaths) if codeAndPaths.size >= 2 =>
                RuleDuplicateCountryCodeError.forDuplicatedCodesAndPaths(code, codeAndPaths.map(_._2))
            }
        }

        if (duplicateErrors.nonEmpty)
          Left(duplicateErrors.toList)
        else
          Right(parsed)
      }

      private def validateForeignProperty(foreignProperty: ForeignProperty,
                                          index: Int): Either[Seq[MtdError], SubmitForeignPropertyBsasRequestData] = {

        def path(suffix: String) = s"/nonFurnishedHolidayLet/$index/$suffix"

        combine(
          parsed,
          ResolveParsedCountryCode(foreignProperty.countryCode, path = Some(path("countryCode"))),
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

      private def validateForeignPropertyConsolidatedExpenses(foreignProperty: ForeignProperty,
                                                              index: Int): Either[Seq[MtdError], SubmitForeignPropertyBsasRequestData] = {

        foreignProperty.expenses
          .collect {
            case expenses if expenses.consolidatedExpenses.isDefined =>
              expenses match {
                case ForeignPropertyExpenses(None, None, None, None, None, None, _, None, Some(_)) =>
                  Right(parsed)
                case _ =>
                  Left(List(RuleBothExpensesError.copy(paths = Some(List(s"/nonFurnishedHolidayLet/$index/expenses")))))
              }
          }
          .getOrElse(Right(parsed))
      }
    }
}
