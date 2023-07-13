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

package v2.controllers.requestParsers.validators.validations

import api.controllers.requestParsers.validators.validations.NoValidationErrors
import api.models.errors.MtdError
import v2.models.errors._

object BothExpensesValidation {

  def validate(expensesAdjustments: Option[Map[String, BigDecimal]]): List[MtdError] = {

    expensesAdjustments match {
      case Some(expenses) =>
        (expenses.contains("consolidatedExpenses"), expenses.contains("residentialFinancialCost"), expenses.size) match {
          case (true, true, size) if size == 2 => NoValidationErrors
          case (true, true, size) if size > 2 => List(RuleBothExpensesError)
          case (true, _, size) if size > 1 => List(RuleBothExpensesError)
          case (_, _, _) => NoValidationErrors
        }
      case None => NoValidationErrors
    }
  }
}
