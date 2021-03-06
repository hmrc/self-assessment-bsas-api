/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.models.request.submitBsas.ukProperty

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class NonFurnishedHolidayLet(income: Option[NonFHLIncome], expenses: Option[NonFHLExpenses]) {
  def isEmpty: Boolean = (income.isEmpty && expenses.isEmpty) ||
    (income.isDefined && income.get.isEmpty) ||
    (expenses.isDefined && expenses.get.isEmpty)
}

object NonFurnishedHolidayLet {

  implicit val reads: Reads[NonFurnishedHolidayLet] = (
    (JsPath \ "income").readNullable[NonFHLIncome] and
      (JsPath \ "expenses").readNullable[NonFHLExpenses]
  )(NonFurnishedHolidayLet.apply _)

  implicit  val writes: OWrites[NonFurnishedHolidayLet] = Json.writes[NonFurnishedHolidayLet]
}
