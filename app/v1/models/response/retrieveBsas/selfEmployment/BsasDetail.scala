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

package v1.models.response.retrieveBsas.selfEmployment

import play.api.libs.functional.syntax._
import play.api.libs.json._
import v1.models.response.retrieveBsas.{Loss, Profit, TotalBsas}

case class BsasDetail(total: TotalBsas,
                      accountingAdjustments: Option[BigDecimal],
                      profit: Option[Profit],
                      loss: Option[Loss],
                      incomeBreakdown: IncomeBreakdown,
                      expensesBreakdown: Option[ExpensesBreakdown],
                      additionsBreakdown: Option[AdditionsBreakdown])

object BsasDetail {
  implicit val reads: Reads[BsasDetail] = (
    JsPath.read[TotalBsas] and
      (JsPath \ "accountingAdjustments").readNullable[BigDecimal] and
      JsPath.readNullable[Profit] and
      JsPath.readNullable[Loss] and
      (JsPath \ "income").read[IncomeBreakdown] and
      (JsPath \ "expenses").readNullable[ExpensesBreakdown] and
      (JsPath \ "additions").readNullable[AdditionsBreakdown]
    )(BsasDetail.apply _)

  implicit val writes: OWrites[BsasDetail] = Json.writes[BsasDetail]
}

