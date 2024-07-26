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

package v3.models.response.retrieveBsas

import play.api.libs.functional.FunctionalBuilder
import play.api.libs.functional.syntax._
import play.api.libs.json._

package object selfEmployment {

  private type SummaryCalculationReads = FunctionalBuilder[Reads]#CanBuild14[
    Option[BigDecimal],
    Option[SummaryCalculationIncome],
    Option[BigDecimal],
    Option[SummaryCalculationExpenses],
    Option[BigDecimal],
    Option[BigDecimal],
    Option[BigDecimal],
    Option[SummaryCalculationAdditions],
    Option[BigDecimal],
    Option[SummaryCalculationDeductions],
    Option[BigDecimal],
    Option[SummaryCalculationAccountingAdjustments],
    Option[BigDecimal],
    Option[BigDecimal]]

  val summaryCalculationReads: SummaryCalculationReads =
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
      (JsPath \ "accountingAdjustments").readNullable[BigDecimal] and
      (JsPath \ "selfEmploymentAccountingAdjustments").readNullable[SummaryCalculationAccountingAdjustments] and
      (JsPath \ "taxableProfit").readNullable[BigDecimal] and
      (JsPath \ "adjustedIncomeTaxLoss").readNullable[BigDecimal]

}
