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

package v7.foreignPropertyBsas.submit.def3.model.request

import play.api.libs.json.*
import shared.utils.JsonWritesUtil
import v7.foreignPropertyBsas.submit.model.request.SubmitForeignPropertyBsasRequestBody

case class Def3_SubmitForeignPropertyBsasRequestBody(
    foreignProperty: Option[ForeignProperty]
) extends SubmitForeignPropertyBsasRequestBody

object Def3_SubmitForeignPropertyBsasRequestBody extends JsonWritesUtil {
  implicit val reads: Reads[Def3_SubmitForeignPropertyBsasRequestBody] = Json.reads

  implicit val writes: OWrites[Def3_SubmitForeignPropertyBsasRequestBody] = new OWrites[Def3_SubmitForeignPropertyBsasRequestBody] {

    override def writes(o: Def3_SubmitForeignPropertyBsasRequestBody): JsObject =
      o.foreignProperty.map(writeForeignProperty).getOrElse(JsObject.empty)

    private def writeForeignProperty(foreignProperty: ForeignProperty): JsObject =
      foreignProperty.countryLevelDetail match {
        case Some(details) =>
          Json.obj("incomeSourceType" -> "15", "adjustments" -> details)

        case None =>
          Json.obj(
            "incomeSourceType" -> "15",
            "adjustments"      -> Json.obj("zeroAdjustments" -> foreignProperty.zeroAdjustments)
          )
      }

  }

}
