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

package v6.bsas.list.def2.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json._
import shared.models.domain.TaxYear
import v6.common.model.{IncomeSourceType, TypeOfBusiness}

case class BusinessSource(
    businessId: String,
    typeOfBusiness: TypeOfBusiness,
    accountingPeriod: AccountingPeriod,
    taxYear: TaxYear,
    summaries: Seq[BsasSummary]
)

object BusinessSource {

  implicit val reads: Reads[BusinessSource] =
    (
      (JsPath \ "incomeSourceId").read[String] and
        (JsPath \ "incomeSourceType").read[IncomeSourceType].map(_.toTypeOfBusiness) and
        JsPath.read[AccountingPeriod] and
        (JsPath \ "taxYear").read[Int].map(TaxYear.fromDownstreamInt) and
        (JsPath \ "ascCalculations").read[Seq[BsasSummary]]
    )(BusinessSource(_, _, _, _, _))

  implicit val writes: OWrites[BusinessSource] = Json.writes[BusinessSource]
}
