/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.response.retrieveBsas

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class AccountingPeriod(startDate: LocalDate, endDate: LocalDate)

object AccountingPeriod {

  implicit val writes: OWrites[AccountingPeriod] = Json.writes[AccountingPeriod]

  implicit val reads: Reads[AccountingPeriod] = (
    (JsPath \ "inputs" \ "accountingPeriodStartDate").read[LocalDate] and
      (JsPath \ "inputs" \ "accountingPeriodEndDate").read[LocalDate]
    )(AccountingPeriod.apply _)
}