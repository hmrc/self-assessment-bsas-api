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

package v1.models.request.submitBsas

import play.api.libs.json.{JsObject, Json, OWrites, Reads}
import utils.JsonWritesUtil

case class SubmitUKPropertyBsasRequestBody(nonFurnishedHolidayLet: Option[NonFurnishedHolidayLet],
                                           furnishedHolidayLet: Option[FurnishedHolidayLet])

object SubmitUKPropertyBsasRequestBody extends JsonWritesUtil{

  implicit val reads: Reads[SubmitUKPropertyBsasRequestBody] = Json.reads[SubmitUKPropertyBsasRequestBody]
  implicit val writes: OWrites[SubmitUKPropertyBsasRequestBody] = new OWrites[SubmitUKPropertyBsasRequestBody] {
    override def writes(o: SubmitUKPropertyBsasRequestBody): JsObject =
      o.nonFurnishedHolidayLet.map { x =>
        filterNull(Json.obj(
          "incomeSourceType" -> "02",
          "income" -> x.income,
          "expenses"-> x.expenses
        ))
      }.getOrElse(
        o.furnishedHolidayLet.map(x =>
          filterNull(Json.obj(
            "incomeSourceType" -> "04",
            "income" -> x.income,
            "expenses"-> x.expenses
          ))).get
      )
  }
}

