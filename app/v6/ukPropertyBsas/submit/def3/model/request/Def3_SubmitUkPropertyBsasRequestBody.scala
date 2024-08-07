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

package v6.ukPropertyBsas.submit.def3.model.request

import play.api.libs.json._
import shared.utils.JsonWritesUtil
import v6.ukPropertyBsas.submit.model.request.SubmitUkPropertyBsasRequestBody

case class Def3_SubmitUkPropertyBsasRequestBody(
    income: Option[Income],
    expenses: Option[Expenses]
) extends SubmitUkPropertyBsasRequestBody

object Def3_SubmitUkPropertyBsasRequestBody extends JsonWritesUtil {

  val incomeSourceType = "02"

  implicit val reads: Reads[Def3_SubmitUkPropertyBsasRequestBody] = Json.reads

  implicit val writes: OWrites[Def3_SubmitUkPropertyBsasRequestBody] = new OWrites[Def3_SubmitUkPropertyBsasRequestBody] {

    override def writes(requestBody: Def3_SubmitUkPropertyBsasRequestBody): JsObject = {
      requestBody match {
        case Def3_SubmitUkPropertyBsasRequestBody(Some(income), Some(expenses)) =>
          Json.obj(
            "incomeSourceType" -> incomeSourceType,
            "adjustments"      -> (Json.obj("income" -> income) ++ Json.obj("expenses" -> expenses))
          )
        case Def3_SubmitUkPropertyBsasRequestBody(Some(income), None) =>
          Json.obj(
            "incomeSourceType" -> incomeSourceType,
            "adjustments"      -> income
          )
        case Def3_SubmitUkPropertyBsasRequestBody(None, Some(expenses)) =>
          Json.obj(
            "incomeSourceType" -> incomeSourceType,
            "adjustments"      -> expenses
          )
        case _ => JsObject.empty
      }
    }

  }

}
