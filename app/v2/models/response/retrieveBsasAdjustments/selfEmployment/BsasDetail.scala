/*
 * Copyright 2022 HM Revenue & Customs
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

package v2.models.response.retrieveBsasAdjustments.selfEmployment

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class BsasDetail(income: Option[IncomeBreakdown],
                      expenses: Option[ExpensesBreakdown],
                      additions: Option[AdditionsBreakdown])

object BsasDetail {
  implicit val reads: Reads[BsasDetail] = (
    (JsPath \ "adjustments" \ "income").readNullable[IncomeBreakdown].map {
      case Some(IncomeBreakdown(None, None)) => None
      case income => income
    } and
    (JsPath \ "adjustments" \ "expenses").readNullable[ExpensesBreakdown].map {
      case Some(ExpensesBreakdown(None, None, None, None, None, None, None, None, None,
      None, None, None, None, None, None, None)) => None
      case expenses => expenses
    } and
      (JsPath \ "adjustments" \ "additions").readNullable[AdditionsBreakdown].map {
        case Some(AdditionsBreakdown(None, None, None, None, None, None, None, None, None,
        None, None, None, None, None, None)) => None
        case additions => additions
      }
    ) (BsasDetail.apply _)

  implicit val writes: OWrites[BsasDetail] = Json.writes[BsasDetail]
}
