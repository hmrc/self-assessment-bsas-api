/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.models.response.retrieveBsas.selfEmployment

import play.api.libs.functional.syntax._
import play.api.libs.json._
import v2.models.response.retrieveBsas.{Loss, Profit, TotalBsas}

case class BsasDetail(total: TotalBsas,
                      accountingAdjustments: Option[BigDecimal],
                      profit: Option[Profit],
                      loss: Option[Loss],
                      incomeBreakdown: Option[IncomeBreakdown],
                      expensesBreakdown: Option[ExpensesBreakdown],
                      additionsBreakdown: Option[AdditionsBreakdown])

object BsasDetail {
  implicit val reads: Reads[BsasDetail] = (
    JsPath.read[TotalBsas] and
      (JsPath \ "accountingAdjustments").readNullable[BigDecimal] and
      JsPath.readNullable[Profit].map{
        case Some(Profit(None, None)) => None
        case profit => profit
      } and
      JsPath.readNullable[Loss].map{
        case Some(Loss(None, None)) => None
        case loss => loss
      } and
      (JsPath \ "income").readNullable[IncomeBreakdown].map {
        case Some(IncomeBreakdown(None, None)) => None
        case income => income
      } and
      (JsPath \ "expenses").readNullable[ExpensesBreakdown].map {
        case Some(ExpensesBreakdown(None, None, None, None, None, None, None, None, None,
        None, None, None, None, None, None, None)) => None
        case expenses => expenses
      } and
      (JsPath \ "additions").readNullable[AdditionsBreakdown].map {
        case Some(AdditionsBreakdown(None, None, None, None, None, None, None, None, None,
        None, None, None, None, None, None)) => None
        case additions => additions
      }
    )(BsasDetail.apply _)

  implicit val writes: OWrites[BsasDetail] = Json.writes[BsasDetail]
}

