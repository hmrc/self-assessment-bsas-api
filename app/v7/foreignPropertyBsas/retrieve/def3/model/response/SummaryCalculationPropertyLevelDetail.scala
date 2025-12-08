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

package v7.foreignPropertyBsas.retrieve.def3.model.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class SummaryCalculationPropertyLevelDetail(propertyId: String,
                                                 totalIncome: Option[BigDecimal],
                                                 income: Option[SummaryCalculationPropertyLevelIncome],
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

object SummaryCalculationPropertyLevelDetail {

  given Reads[SummaryCalculationPropertyLevelDetail] = (
    (JsPath \ "propertyId").read[String] and
      (JsPath \ "totalIncome").readNullable[BigDecimal] and
      (JsPath \ "income").readNullable[SummaryCalculationPropertyLevelIncome] and
      (JsPath \ "totalExpenses").readNullable[BigDecimal] and
      (JsPath \ "expenses").readNullable[SummaryCalculationExpenses] and
      (JsPath \ "netProfit").readNullable[BigDecimal] and
      (JsPath \ "netLoss").readNullable[BigDecimal] and
      (JsPath \ "totalAdditions").readNullable[BigDecimal] and
      (JsPath \ "additions").readNullable[SummaryCalculationAdditions] and
      (JsPath \ "totalDeductions").readNullable[BigDecimal] and
      (JsPath \ "deductions").readNullable[SummaryCalculationDeductions] and
      (JsPath \ "taxableProfit").readNullable[BigDecimal] and
      (JsPath \ "adjustedIncomeTaxLoss").readNullable[BigDecimal]
  )(SummaryCalculationPropertyLevelDetail.apply)

  given OWrites[SummaryCalculationPropertyLevelDetail] = Json.writes[SummaryCalculationPropertyLevelDetail]
}
