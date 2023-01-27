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

package v2.models.request.submitBsas.foreignProperty

import play.api.libs.json.{JsObject, Json, OWrites, Reads}

case class ForeignProperty(countryCode: String,
                           income: Option[ForeignPropertyIncome],
                           expenses: Option[ForeignPropertyExpenses]) {

  def isEmpty: Boolean =
    (income.isEmpty && expenses.isEmpty) ||
      (income.isDefined && income.get.isEmpty) ||
      (expenses.isDefined && expenses.get.isEmpty)
}

object ForeignProperty {
  implicit val reads: Reads[ForeignProperty] = Json.reads[ForeignProperty]
  implicit val writes: OWrites[ForeignProperty] = new OWrites[ForeignProperty] {
    override def writes(o: ForeignProperty): JsObject =
      if (o.isEmpty) JsObject.empty
      else Json.obj(
        "countryCode" -> o.countryCode,
        "income" -> o.income,
        "expenses" -> o.expenses)
  }
}