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

package v2.models.response.retrieveBsasAdjustments.foreignProperty

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class BsasDetail(incomes: Option[IncomeBreakdown], expenses: Option[ExpensesBreakdown])


object BsasDetail {

  val fhlReads: Reads[BsasDetail] = (
    (JsPath   \ "income").readNullable[IncomeBreakdown](IncomeBreakdown.fhlReads).map {
      case Some(IncomeBreakdown(None, None, None, None)) => None
      case incomeBreakdown => incomeBreakdown
    } and
      (JsPath  \ "expenses").readNullable[ExpensesBreakdown](ExpensesBreakdown.fhlReads).map {
        case Some(ExpensesBreakdown(None, None, None, None, None, None, None, None, None)) => None
        case expensesBreakdown => expensesBreakdown
      }
    ) (BsasDetail.apply _)

  val fhlSeqReads: Reads[Seq[BsasDetail]] = Reads.seq(fhlReads)

  val nonFhlReads: Reads[BsasDetail] = (
    (JsPath   \ "income").readNullable[IncomeBreakdown](IncomeBreakdown.nonFhlReads).map {
      case Some(IncomeBreakdown(None, None, None, None)) => None
      case incomeBreakdown => incomeBreakdown
    } and
      (JsPath  \ "expenses").readNullable[ExpensesBreakdown](ExpensesBreakdown.nonFhlReads).map {
        case Some(ExpensesBreakdown(None, None, None, None, None, None, None, None, None)) => None
        case expensesBreakdown => expensesBreakdown
      }
    ) (BsasDetail.apply _)

  val nonFhlSeqReads: Reads[Seq[BsasDetail]] = Reads.traversableReads[Seq, BsasDetail](implicitly, nonFhlReads)

  implicit val writes: OWrites[BsasDetail] = Json.writes[BsasDetail]
}
