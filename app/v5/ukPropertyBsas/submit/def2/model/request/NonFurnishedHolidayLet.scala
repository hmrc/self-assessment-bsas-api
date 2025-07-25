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

package v5.ukPropertyBsas.submit.def2.model.request

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class NonFurnishedHolidayLet(income: Option[NonFHLIncome], expenses: Option[NonFHLExpenses])

object NonFurnishedHolidayLet {

  implicit val reads: Reads[NonFurnishedHolidayLet] = (
    (JsPath \ "income").readNullable[NonFHLIncome] and
      (JsPath \ "expenses").readNullable[NonFHLExpenses]
  )(NonFurnishedHolidayLet.apply)

  implicit val writes: OWrites[NonFurnishedHolidayLet] = Json.writes[NonFurnishedHolidayLet]
}
