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

package v7.foreignPropertyBsas.retrieve.def2.model.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class SummaryCalculation(totalIncome: Option[BigDecimal],
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
                              adjustedIncomeTaxLoss: Option[BigDecimal],
                              countryLevelDetail: Option[Seq[SummaryCalculationCountryLevelDetail]])

object SummaryCalculation {

  implicit val reads: Reads[SummaryCalculation] = (
    (JsPath \ "totalIncome").readNullable[BigDecimal] and
      (JsPath \ "income").readNullable[SummaryCalculationIncome] and
      (JsPath \ "totalExpenses").readNullable[BigDecimal] and
      (JsPath \ "expenses").readNullable[SummaryCalculationExpenses] and
      (JsPath \ "netProfit").readNullable[BigDecimal] and
      (JsPath \ "netLoss").readNullable[BigDecimal] and
      (JsPath \ "totalAdditions").readNullable[BigDecimal] and
      (JsPath \ "additions").readNullable[SummaryCalculationAdditions] and
      (JsPath \ "totalDeductions").readNullable[BigDecimal] and
      (JsPath \ "deductions").readNullable[SummaryCalculationDeductions] and
      (JsPath \ "taxableProfit").readNullable[BigDecimal] and
      (JsPath \ "adjustedIncomeTaxLoss").readNullable[BigDecimal] and
      (JsPath \ "countryLevelDetail").readNullable[Seq[SummaryCalculationCountryLevelDetail]]
  )(SummaryCalculation.apply _)

  implicit val writes: OWrites[SummaryCalculation] = Json.writes[SummaryCalculation]
}
