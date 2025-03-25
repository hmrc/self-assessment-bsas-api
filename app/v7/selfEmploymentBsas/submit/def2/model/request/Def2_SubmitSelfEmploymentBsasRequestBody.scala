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

package v7.selfEmploymentBsas.submit.def2.model.request

import play.api.libs.json.{JsObject, Json, OWrites, Reads}
import shared.utils.JsonWritesUtil
import v7.selfEmploymentBsas.submit.model.request.SubmitSelfEmploymentBsasRequestBody

case class Def2_SubmitSelfEmploymentBsasRequestBody(
    income: Option[Income],
    expenses: Option[Expenses],
    additions: Option[Additions],
    zeroAdjustments: Option[Boolean]
) extends SubmitSelfEmploymentBsasRequestBody {

  def isEmpty: Boolean = income.isEmpty && expenses.isEmpty && additions.isEmpty && zeroAdjustments.isEmpty
}

object Def2_SubmitSelfEmploymentBsasRequestBody extends JsonWritesUtil {

  implicit val reads: Reads[Def2_SubmitSelfEmploymentBsasRequestBody] = Json.reads

  implicit val writes: OWrites[Def2_SubmitSelfEmploymentBsasRequestBody] = (o: Def2_SubmitSelfEmploymentBsasRequestBody) =>
    if (o.isEmpty) {
      JsObject.empty
    } else {
      filterNull(
        Json.obj(
          "incomeSourceType" -> "01",
          "adjustments" -> filterNull(
            Json.obj(
              "income"          -> o.income,
              "expenses"        -> o.expenses,
              "additions"       -> o.additions,
              "zeroAdjustments" -> o.zeroAdjustments
            ))
        ))
    }

}
