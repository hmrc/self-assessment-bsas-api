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

package v6.ukPropertyBsas.submit.def1.model.request

import play.api.libs.json._
import shared.utils.JsonWritesUtil
import v6.ukPropertyBsas.submit.model.request.SubmitUkPropertyBsasRequestBody

case class Def1_SubmitUkPropertyBsasRequestBody(
    ukProperty: Option[UkProperty],
    furnishedHolidayLet: Option[FurnishedHolidayLet]
) extends SubmitUkPropertyBsasRequestBody

object Def1_SubmitUkPropertyBsasRequestBody extends JsonWritesUtil {
  implicit val reads: Reads[Def1_SubmitUkPropertyBsasRequestBody] = Json.reads

  implicit val writes: OWrites[Def1_SubmitUkPropertyBsasRequestBody] = new OWrites[Def1_SubmitUkPropertyBsasRequestBody] {

    override def writes(o: Def1_SubmitUkPropertyBsasRequestBody): JsObject = {
      writeIfPresent(o.ukProperty, incomeSourceType = "02")
        .orElse(writeIfPresent(o.furnishedHolidayLet, incomeSourceType = "04"))
        .getOrElse(JsObject.empty)
    }

    private def writeIfPresent[A: Writes](oa: Option[A], incomeSourceType: String): Option[JsObject] =
      oa.map { a =>
        Json.obj("incomeSourceType" -> incomeSourceType, "adjustments" -> a)
      }

  }

}
