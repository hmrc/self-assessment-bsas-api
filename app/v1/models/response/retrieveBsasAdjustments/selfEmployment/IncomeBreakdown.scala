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

package v1.models.response.retrieveBsasAdjustments.selfEmployment

import play.api.libs.functional.syntax._
import play.api.libs.json._
import utils.NestedJsonReads


case class IncomeBreakdown( turnover: Option[BigDecimal],
                            other: Option[BigDecimal])

object IncomeBreakdown extends NestedJsonReads {

  implicit val reads: Reads[IncomeBreakdown] = (
    (JsPath \ "turnover").readNullable[BigDecimal] and
      (JsPath \ "other").readNullable[BigDecimal]
    )(IncomeBreakdown.apply _)

  implicit val writes: OWrites[IncomeBreakdown] = Json.writes[IncomeBreakdown]
}
