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

package v4.models.response.retrieveBsas.selfEmployment

import play.api.libs.json._

case class AdjustableSummaryCalculation(
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
    totalAccountingAdjustments: Option[BigDecimal],
    accountingAdjustments: Option[SummaryCalculationAccountingAdjustments],
    taxableProfit: Option[BigDecimal],
    adjustedIncomeTaxLoss: Option[BigDecimal]
)

object AdjustableSummaryCalculation {
  implicit val reads: Reads[AdjustableSummaryCalculation] = summaryCalculationReads(AdjustableSummaryCalculation.apply _)

  implicit val writes: OWrites[AdjustableSummaryCalculation] = Json.writes[AdjustableSummaryCalculation]
}
