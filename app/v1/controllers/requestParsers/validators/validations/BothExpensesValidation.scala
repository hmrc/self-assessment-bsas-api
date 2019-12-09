/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators.validations

import v1.models.errors.{BothExpensesError, MtdError}

object BothExpensesValidation {

  def validate(expensesAdjustments: Option[Map[String, Option[BigDecimal]]]): List[MtdError] = {

    expensesAdjustments match {
      case Some(expenses) => (expenses.contains("consolidatedExpenses"), expenses.contains("residentialFinancialCost"),
        expenses.size) match {
        case (true, true, size) if size == 2 => List()
        case (true, true, size) if size > 2 => List(BothExpensesError)
        case (true, _, size) if size > 1 => List(BothExpensesError)
        case (_, _, _) => List()
      }
      case None => List()
    }
  }
}
