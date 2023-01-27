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

package v2.models.response.retrieveBsas.foreignProperty

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import v2.models.response.retrieveBsas.TotalBsas

case class CountryLevelDetail(countryCode: String,
                              total: TotalBsas,
                              incomeBreakdown: Option[IncomeBreakdown],
                              expensesBreakdown: Option[ExpensesBreakdown]
                             )

object CountryLevelDetail {

  val fhlReads: Reads[CountryLevelDetail] = (
    (JsPath \ "countryCode").read[String] and
      (JsPath \ "total").read[TotalBsas] and
      (JsPath \ "income").readNullable[IncomeBreakdown](IncomeBreakdown.fhlReads).map {
        case Some(IncomeBreakdown(None, None, None)) => None
        case income => income
      } and
      (JsPath \ "expenses").readNullable[ExpensesBreakdown](ExpensesBreakdown.fhlReads).map {
        case Some(ExpensesBreakdown(None, None, None, None, None, None, None, None, None, None)) => None
        case expenses => expenses
      }
    ) (CountryLevelDetail.apply _)

  val fhlReadsSeq: Reads[Seq[CountryLevelDetail]] = Reads.traversableReads[Seq, CountryLevelDetail](implicitly, fhlReads)

  val nonFhlReads: Reads[CountryLevelDetail] = (
    (JsPath \ "countryCode").read[String] and
      (JsPath \ "total").read[TotalBsas] and
      (JsPath \ "income").readNullable[IncomeBreakdown](IncomeBreakdown.nonFhlReads).map {
        case Some(IncomeBreakdown(None, None, None)) => None
        case income => income
      } and
      (JsPath \ "expenses").readNullable[ExpensesBreakdown](ExpensesBreakdown.nonFhlReads).map {
        case Some(ExpensesBreakdown(None, None, None, None, None, None, None, None, None, None)) => None
        case expenses => expenses
      }
    ) (CountryLevelDetail.apply _)

  val nonFhlReadsSeq: Reads[Seq[CountryLevelDetail]] = Reads.traversableReads[Seq, CountryLevelDetail](implicitly, nonFhlReads)

  implicit val writes: Writes[CountryLevelDetail] = Json.writes[CountryLevelDetail]
}