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

package v3.models.response.retrieveBsas.foreignProperty

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v3.models.domain.{IncomeSourceType, TypeOfBusiness}

case class Inputs(businessId: String,
                  typeOfBusiness: TypeOfBusiness,
                  businessName: Option[String],
                  accountingPeriodStartDate: String,
                  accountingPeriodEndDate: String,
                  source: String,
                  submissionPeriods: Seq[SubmissionPeriods])

object Inputs {
  implicit val reads: Reads[Inputs] = (
    (JsPath \ "incomeSourceId").read[String] and
      (JsPath \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness) and
      (JsPath \ "incomeSourceName").readNullable[String] and
      (JsPath \ "accountingPeriodStartDate").read[String] and
      (JsPath \ "accountingPeriodEndDate").read[String] and
      (JsPath \ "source").read[String] and
      (JsPath \ "submissionPeriods").read[Seq[SubmissionPeriods]]
  )(Inputs.apply _)

  implicit val writes: OWrites[Inputs] = Json.writes[Inputs]
}
