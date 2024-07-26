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

package v3.models.request.submitBsas.ukProperty

import shared.models.domain.{CalculationId, Nino, TaxYear}
import play.api.libs.json._
import shared.utils.JsonWritesUtil

case class SubmitUkPropertyBsasRequestData(nino: Nino, calculationId: CalculationId, taxYear: Option[TaxYear], body: SubmitUKPropertyBsasRequestBody)

case class SubmitUKPropertyBsasRequestBody(nonFurnishedHolidayLet: Option[NonFurnishedHolidayLet], furnishedHolidayLet: Option[FurnishedHolidayLet])

object SubmitUKPropertyBsasRequestBody extends JsonWritesUtil {
  implicit val reads: Reads[SubmitUKPropertyBsasRequestBody] = Json.reads[SubmitUKPropertyBsasRequestBody]

  implicit val writes: OWrites[SubmitUKPropertyBsasRequestBody] = new OWrites[SubmitUKPropertyBsasRequestBody] {

    override def writes(o: SubmitUKPropertyBsasRequestBody): JsObject = {
      writeIfPresent(o.nonFurnishedHolidayLet, incomeSourceType = "02")
        .orElse(writeIfPresent(o.furnishedHolidayLet, incomeSourceType = "04"))
        .getOrElse(JsObject.empty)
    }

    private def writeIfPresent[A: Writes](oa: Option[A], incomeSourceType: String): Option[JsObject] =
      oa.map { a =>
        Json.obj("incomeSourceType" -> incomeSourceType, "adjustments" -> a)
      }

  }

}
