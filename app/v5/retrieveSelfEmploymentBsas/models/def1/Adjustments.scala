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

package v5.retrieveSelfEmploymentBsas.models.def1

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Adjustments(
                        income: Option[AdjustmentsIncome],
                        expenses: Option[AdjustmentsExpenses],
                        additions: Option[AdjustmentsAdditions],
                      )

object Adjustments {
  implicit val reads: Reads[Adjustments] = (
    (JsPath \ "income").readNullable[AdjustmentsIncome] and
      (JsPath \ "expenses").readNullable[AdjustmentsExpenses] and
      (JsPath \ "additions").readNullable[AdjustmentsAdditions]
    ) (Adjustments.apply _)

  implicit val writes: OWrites[Adjustments] = Json.writes[Adjustments]
}
