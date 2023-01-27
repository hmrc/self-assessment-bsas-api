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

package v3.controllers.requestParsers.validators.validations

import v3.models.errors.{ MtdError, RuleBothExpensesError }
import v3.models.request.submitBsas.foreignProperty.{ FhlEeaExpenses, ForeignPropertyExpenses }
import v3.models.request.submitBsas.selfEmployment.{ Additions, Expenses }
import v3.models.request.submitBsas.ukProperty.{ FHLExpenses, NonFHLExpenses }

object BothExpensesValidation {

  def validate(expenses: ForeignPropertyExpenses, path: String): List[MtdError] = {
    expenses.consolidatedExpenses match {
      case None => NoValidationErrors
      case Some(_) =>
        expenses match {
          case ForeignPropertyExpenses(None, None, None, None, None, None, _, None, Some(_)) => NoValidationErrors
          case _                                                                             => List(RuleBothExpensesError.copy(paths = Some(Seq(path))))
        }
    }
  }

  def validate(expenses: FhlEeaExpenses, path: String): List[MtdError] = {
    expenses.consolidatedExpenses match {
      case None => NoValidationErrors
      case Some(_) =>
        expenses match {
          case FhlEeaExpenses(None, None, None, None, None, None, None, Some(_)) => NoValidationErrors
          case _                                                                 => List(RuleBothExpensesError.copy(paths = Some(Seq(path))))
        }
    }
  }

  def validate(expenses: FHLExpenses, path: String): List[MtdError] = {
    expenses.consolidatedExpenses match {
      case None => NoValidationErrors
      case Some(_) =>
        expenses match {
          case FHLExpenses(None, None, None, None, None, None, None, Some(_)) => NoValidationErrors
          case _                                                              => List(RuleBothExpensesError.copy(paths = Some(Seq(path))))
        }
    }
  }

  def validate(expenses: NonFHLExpenses, path: String): List[MtdError] = {
    expenses.consolidatedExpenses match {
      case None => NoValidationErrors
      case Some(_) =>
        expenses match {
          case NonFHLExpenses(None, None, None, None, None, None, None, None, Some(_)) => NoValidationErrors
          case _                                                                       => List(RuleBothExpensesError.copy(paths = Some(Seq(path))))
        }
    }
  }

  def bothExpensesValidation(expenses: Expenses, additions: Option[Additions], path: String): List[MtdError] = {

    def bothExpensesError = List(RuleBothExpensesError.copy(paths = Some(Seq(path))))

    (expenses.consolidatedExpenses, additions) match {
      case (None, _) => NoValidationErrors
      case (Some(_), None) if expenses.hasOnlyConsolidatedExpenses => NoValidationErrors
      case (Some(_), None) => bothExpensesError
      case (Some(_), Some(adds)) if !expenses.hasOnlyConsolidatedExpenses || adds.nonEmpty => bothExpensesError
      case (Some(_), Some(_)) => NoValidationErrors
    }
  }
}
