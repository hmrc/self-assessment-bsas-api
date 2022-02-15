/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import play.api.libs.functional.syntax._


case class AdjustableSummaryCalculation(totalIncome: Option[BigDecimal],
                                        income: Option[Income],
                                        totalExpenses: Option[BigDecimal],
                                        expenses: Option[Expenses],
                                        netProfit: Option[BigDecimal],
                                        netLoss: Option[BigDecimal],
                                        totalAdditions: Option[BigDecimal],
                                        additions: Option[Additions],
                                        totalDeductions: Option[BigDecimal],
                                        deductions: Option[Deductions],
                                        taxableProfit: Option[BigDecimal],
                                        adjustedIncomeTaxLoss: Option[BigDecimal],
                                        countryLevelDetail: Option[Seq[CountryLevelDetail]]
                                       )


object AdjustableSummaryCalculation {
  implicit val reads: Reads[AdjustableSummaryCalculation] = (
    (JsPath \ "totalIncome").readNullable[BigDecimal] and
      (JsPath \ "income").readNullable[Income] and
      (JsPath \ "totalExpenses").readNullable[BigDecimal] and
      (JsPath \ "expenses").readNullable[Expenses] and
      (JsPath \ "netProfit").readNullable[BigDecimal] and
      (JsPath \ "netLoss").readNullable[BigDecimal] and
      (JsPath \ "totalAdditions").readNullable[BigDecimal] and
      (JsPath \ "additions").readNullable[Additions] and
      (JsPath \ "totalDeductions").readNullable[BigDecimal] and
      (JsPath \ "deductions").readNullable[Deductions] and
      (JsPath \ "taxableProfit").readNullable[BigDecimal] and
      (JsPath \ "adjustedIncomeTaxLoss").readNullable[BigDecimal] and
    (JsPath \ "countryLevelDetail").readNullable[Seq[CountryLevelDetail]]
    ) (AdjustableSummaryCalculation.apply _)

  implicit val writes: OWrites[AdjustableSummaryCalculation] = Json.writes[AdjustableSummaryCalculation]
}
