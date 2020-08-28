/*
 * Copyright 2020 HM Revenue & Customs
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
import utils.JsonWritesUtil

case class SubmitForeignPropertyBsasRequestBody(foreignProperty: Option[ForeignProperty], foreignFhlEea: Option[FhlEea]) {

  def isEmpty: Boolean = (foreignProperty.isEmpty && foreignFhlEea.isEmpty) ||
    foreignFhlEea.flatMap(_.income.map(_.isEmpty)).getOrElse(false) ||
    foreignFhlEea.flatMap(_.expenses.map(_.isEmpty)).getOrElse(false) ||
    foreignFhlEea.exists(_.isEmpty) ||
    foreignProperty.flatMap(_.income.map(_.isEmpty)).getOrElse(false) ||
    foreignProperty.flatMap(_.expenses.map(_.isEmpty)).getOrElse(false) ||
    foreignProperty.exists(_.isEmpty)
}


object SubmitForeignPropertyBsasRequestBody extends JsonWritesUtil {
  implicit val reads: Reads[SubmitForeignPropertyBsasRequestBody] = Json.reads[SubmitForeignPropertyBsasRequestBody]
  implicit val writes: OWrites[SubmitForeignPropertyBsasRequestBody] = new OWrites[SubmitForeignPropertyBsasRequestBody] {
    override def writes(o: SubmitForeignPropertyBsasRequestBody): JsObject =
      o.foreignProperty.map (x =>
        filterNull(Json.obj(
          "incomeSourceType" -> "15",
          "income" -> x.income,
          "expenses" -> x.expenses
        ))).getOrElse(
        o.foreignFhlEea.map(x =>
          filterNull(Json.obj(
            "incomeSourceType" -> "03",
            "income" -> x.income,
            "expenses" -> x.expenses
          ))).getOrElse(Json.obj())
      )
  }
}
