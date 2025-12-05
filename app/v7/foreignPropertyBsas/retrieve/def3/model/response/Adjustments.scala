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

package v7.foreignPropertyBsas.retrieve.def3.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Adjustments(propertyLevelDetail: Option[Seq[Adjustments]],
                       propertyId: String,
                       income: Option[AdjustmentsIncome],
                       expenses: Option[AdjustmentsExpenses],
                       zeroAdjustments: Option[Boolean])

object Adjustments {

  implicit val reads: Reads[Adjustments] = (
    Reads.pure(None) and
      (JsPath \ "propertyId").read[String] and
      (JsPath \ "income").readNullable[AdjustmentsIncome](AdjustmentsIncome.reads) and
      (JsPath \ "expenses").readNullable[AdjustmentsExpenses](AdjustmentsExpenses.reads) and
      Reads.pure(None)
  )(Adjustments.apply _)

  val readsZeroAdjustments: Reads[Adjustments] = (
    Reads.pure(None) and
      (JsPath \ "propertyId").read[String] and
      Reads.pure(None) and
      Reads.pure(None) and
      (JsPath \ "zeroAdjustments").readNullable[Boolean]
  )(Adjustments.apply _)

  implicit val writes: OWrites[Adjustments] = Json.writes[Adjustments]
}
