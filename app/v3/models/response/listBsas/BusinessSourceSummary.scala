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

package v3.models.response.listBsas

import api.models.domain.TaxYear
import play.api.libs.functional.syntax._
import play.api.libs.json._
import v3.models.domain.{ IncomeSourceType, TypeOfBusiness }

case class BusinessSourceSummary[I](businessId: String,
                                    typeOfBusiness: TypeOfBusiness,
                                    accountingPeriod: AccountingPeriod,
                                    taxYear: TaxYear,
                                    summaries: Seq[I])

object BusinessSourceSummary {
  implicit def reads[I: Reads]: Reads[BusinessSourceSummary[I]] =
    (
      (JsPath \ "incomeSourceId").read[String] and
        (JsPath \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness) and
        JsPath.read[AccountingPeriod] and
        (JsPath \ "taxYear").read[Int].map(TaxYear.fromDownstreamInt) and
        (JsPath \ "ascCalculations").read[Seq[I]]
    )(BusinessSourceSummary(_, _, _, _, _))

  implicit def writes[I: Writes]: OWrites[BusinessSourceSummary[I]] = Json.writes[BusinessSourceSummary[I]]
}
