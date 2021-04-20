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

package v1.models.request.submitBsas.selfEmployment

import play.api.libs.json._
import utils.JsonWritesUtil

case class SubmitSelfEmploymentBsasRequestBody(income: Option[Income], additions: Option[Additions], expenses: Option[Expenses]) {

  def isEmpty: Boolean = !(income.isDefined || additions.isDefined || expenses.isDefined)

  def isIncorrectOrEmptyBodyError: Boolean =
    isEmpty || (income.isDefined && income.get.isEmpty ||
    additions.isDefined && additions.get.isEmpty ||
    expenses.isDefined && expenses.get.isEmpty)
}

object SubmitSelfEmploymentBsasRequestBody extends JsonWritesUtil {

  implicit val reads: Reads[SubmitSelfEmploymentBsasRequestBody] = Json.reads[SubmitSelfEmploymentBsasRequestBody]

  implicit val writes: OWrites[SubmitSelfEmploymentBsasRequestBody] = (o: SubmitSelfEmploymentBsasRequestBody) =>
    if(o.isEmpty) JsObject.empty
    else filterNull(Json.obj(
      "incomeSourceType" -> "01",
      "adjustments" -> filterNull(Json.obj(
        "income" -> o.income,
        "expenses" -> o.expenses,
        "additions" -> o.additions
      ))
    ))
}
