/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.response.retrieveBsasAdjustments.selfEmployment

import play.api.libs.functional.syntax._
import play.api.libs.json._
import v1.models.domain.{IncomeSourceType, TypeOfBusiness}
import v1.models.request.DesTaxYear
import v1.models.response.retrieveBsas.AccountingPeriod

case class Metadata(typeOfBusiness: TypeOfBusiness,
                    selfEmploymentId: Option[String],
                    accountingPeriod: AccountingPeriod,
                    taxYear: String,
                    requestedDateTime: String,
                    bsasId: String,
                    summaryStatus: String,
                    adjustedSummary: Boolean)

object Metadata {

  implicit val reads: Reads[Metadata] = (
    (JsPath \ "inputs" \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness) and
      (JsPath \ "inputs" \ "incomeSourceId").readNullable[String] and
      JsPath.read[AccountingPeriod] and
      (JsPath \ "metadata" \ "taxYear").read[Int].map(DesTaxYear.fromDesIntToString) and
      (JsPath \ "metadata" \ "requestedDateTime").read[String] and
      (JsPath \ "metadata" \ "calculationId").read[String] and
      (JsPath \ "metadata" \ "status").read[String] and
      Reads.pure(true)
    )(Metadata.apply _)

  implicit val writes: OWrites[Metadata] = Json.writes[Metadata]
}
