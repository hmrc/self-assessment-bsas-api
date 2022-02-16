/*
 * Copyright 2022 HM Revenue & Customs
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

import v3.models.errors.{MtdError, RuleBothExpensesError}
import v3.models.request.submitBsas.foreignProperty.{FhlEeaExpenses, ForeignPropertyExpenses}
import v3.models.request.submitBsas.selfEmployment.{Additions, Expenses, SubmitSelfEmploymentBsasRequestBody}
import v3.models.request.submitBsas.ukProperty.{FHLExpenses, NonFHLExpenses}

object BothExpensesValidation {

  @deprecated(message = "Use validation specific to type instead")
  def validate(expensesAdjustments: Option[Map[String, BigDecimal]]): List[MtdError] = {

    expensesAdjustments match {
      case Some(expenses) =>
        (expenses.contains("consolidatedExpenses"), expenses.contains("residentialFinancialCost"), expenses.size) match {
          case (true, true, size) if size == 2 => NoValidationErrors
          case (true, true, size) if size > 2  => List(RuleBothExpensesError)
          case (true, _, size) if size > 1     => List(RuleBothExpensesError)
          case (_, _, _)                       => NoValidationErrors
        }
      case None => NoValidationErrors
    }
  }

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

  def bothExpensesValidation(expenses: Expenses, additions: Additions, path: String): List[MtdError] = {
    expenses.consolidatedExpenses match {
      case None => NoValidationErrors
      case Some(_) =>
        expenses match {
          case Expenses(
          None, None, None, None, None, None, None, None, None, None, None, None, None, None, None,
          Some(_)) => NoValidationErrors
          case _ => List(RuleBothExpensesError.copy(paths = Some(Seq(path))))
        }
        additions match {
          case Additions(
          None, None, None, None, None, None, None, None, None, None, None, None, None, None, None) => NoValidationErrors
          case _ => List(RuleBothExpensesError.copy(paths = Some(Seq(path))))
        }
    }
  }
}
