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

package v5.selfEmploymentBsas.retrieve.def1.model.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.Reads.*
import play.api.libs.json.*
import shared.models.domain.Source
import v5.common.model.{IncomeSourceType, TypeOfBusiness}

case class Inputs(
    typeOfBusiness: TypeOfBusiness,
    businessId: String,
    businessName: Option[String],
    accountingPeriodStartDate: String,
    accountingPeriodEndDate: String,
    source: Source,
    submissionPeriods: Seq[SubmissionPeriod]
)

object Inputs {

  implicit val reads: Reads[Inputs] = (
    (JsPath \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness) and
      (JsPath \ "incomeSourceId").read[String] and
      (JsPath \ "incomeSourceName").readNullable[String] and
      (JsPath \ "accountingPeriodStartDate").read[String] and
      (JsPath \ "accountingPeriodEndDate").read[String] and
      (JsPath \ "source").read[Source] and
      (JsPath \ "submissionPeriods").read[Seq[SubmissionPeriod]]
  )(Inputs.apply)

  implicit val writes: OWrites[Inputs] = Json.writes[Inputs]
}
