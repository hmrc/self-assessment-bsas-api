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

package v6.foreignPropertyBsas.retrieve.def1.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class SummaryCalculationCountryLevelDetail(countryCode: String,
                                                totalIncome: Option[BigDecimal],
                                                income: Option[SummaryCalculationIncome],
                                                totalExpenses: Option[BigDecimal],
                                                expenses: Option[SummaryCalculationExpenses],
                                                netProfit: Option[BigDecimal],
                                                netLoss: Option[BigDecimal],
                                                totalAdditions: Option[BigDecimal],
                                                additions: Option[SummaryCalculationAdditions],
                                                totalDeductions: Option[BigDecimal],
                                                deductions: Option[SummaryCalculationDeductions],
                                                taxableProfit: Option[BigDecimal],
                                                adjustedIncomeTaxLoss: Option[BigDecimal])

object SummaryCalculationCountryLevelDetail {

  implicit val reads: Reads[SummaryCalculationCountryLevelDetail] = (
    (JsPath \ "countryCode").read[String] and
      (JsPath \ "totalIncome").readNullable[BigDecimal] and
      (JsPath \ "income").readNullable[SummaryCalculationIncome](SummaryCalculationIncome.reads) and
      (JsPath \ "totalExpenses").readNullable[BigDecimal] and
      (JsPath \ "expenses").readNullable[SummaryCalculationExpenses] and
      (JsPath \ "netProfit").readNullable[BigDecimal] and
      (JsPath \ "netLoss").readNullable[BigDecimal] and
      (JsPath \ "totalAdditions").readNullable[BigDecimal] and
      (JsPath \ "additions").readNullable[SummaryCalculationAdditions] and
      (JsPath \ "totalDeductions").readNullable[BigDecimal] and
      (JsPath \ "deductions").readNullable[SummaryCalculationDeductions](SummaryCalculationDeductions.reads) and
      (JsPath \ "taxableProfit").readNullable[BigDecimal] and
      (JsPath \ "adjustedIncomeTaxLoss").readNullable[BigDecimal]
  )(SummaryCalculationCountryLevelDetail.apply _)

  implicit val writes: OWrites[SummaryCalculationCountryLevelDetail] = Json.writes[SummaryCalculationCountryLevelDetail]
}
