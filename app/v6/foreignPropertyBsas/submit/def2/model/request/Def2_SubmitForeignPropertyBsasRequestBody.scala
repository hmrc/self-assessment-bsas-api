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

package v6.foreignPropertyBsas.submit.def2.model.request

import play.api.libs.json._
import shared.utils.JsonWritesUtil
import v6.foreignPropertyBsas.submit.model.request.SubmitForeignPropertyBsasRequestBody

case class Def2_SubmitForeignPropertyBsasRequestBody(
    nonFurnishedHolidayLet: Option[Seq[ForeignProperty]],
    foreignFhlEea: Option[FhlEea]
) extends SubmitForeignPropertyBsasRequestBody

object Def2_SubmitForeignPropertyBsasRequestBody extends JsonWritesUtil {
  implicit val reads: Reads[Def2_SubmitForeignPropertyBsasRequestBody] = Json.reads

  implicit val writes: OWrites[Def2_SubmitForeignPropertyBsasRequestBody] = new OWrites[Def2_SubmitForeignPropertyBsasRequestBody] {

    override def writes(o: Def2_SubmitForeignPropertyBsasRequestBody): JsObject = {
      writeIfPresent(o.nonFurnishedHolidayLet, incomeSourceType = "15")
        .orElse(writeIfPresent(o.foreignFhlEea, incomeSourceType = "03"))
        .getOrElse(JsObject.empty)
    }

    private def writeIfPresent[A: Writes](oa: Option[A], incomeSourceType: String): Option[JsObject] =
      oa.map { a =>
        Json.obj("incomeSourceType" -> incomeSourceType, "adjustments" -> a)
      }

  }

}
