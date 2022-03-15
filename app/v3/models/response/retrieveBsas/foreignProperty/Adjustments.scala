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

package v3.models.response.retrieveBsas.foreignProperty

import play.api.libs.json.{ JsPath, Json, OWrites, Reads }
import play.api.libs.functional.syntax._

case class Adjustments(countryLevelDetail: Option[Seq[Adjustments]],
                       countryCode: Option[String],
                       income: Option[AdjustmentsIncome],
                       expenses: Option[AdjustmentsExpenses])

object Adjustments {

  val readsFhl: Reads[Adjustments] = (
    Reads.pure(None) and
      Reads.pure(None) and
      (JsPath \ "income").readNullable[AdjustmentsIncome](AdjustmentsIncome.readsFhl) and
      (JsPath \ "expenses").readNullable[AdjustmentsExpenses](AdjustmentsExpenses.readsFhl)
  )(Adjustments.apply _)

  val readsNonFhl: Reads[Adjustments] = (
    Reads.pure(None) and
      (JsPath \ "countryCode").readNullable[String] and
      (JsPath \ "income").readNullable[AdjustmentsIncome](AdjustmentsIncome.readsNonFhl) and
      (JsPath \ "expenses").readNullable[AdjustmentsExpenses](AdjustmentsExpenses.readsNonFhl)
  )(Adjustments.apply _)

  val readsNonFhlSeq: Reads[Seq[Adjustments]] = Reads.traversableReads[Seq, Adjustments](implicitly, readsNonFhl)

  implicit val writes: OWrites[Adjustments] = Json.writes[Adjustments]
}
