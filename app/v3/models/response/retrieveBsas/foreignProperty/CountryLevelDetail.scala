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

import play.api.libs.json.{Json, OFormat}

case class CountryLevelDetail(countryCode: String,
                              totalIncome: Option[BigDecimal],
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
                              adjustedIncomeTaxLoss: Option[BigDecimal]
                             )

object CountryLevelDetail {
  implicit val format: OFormat[CountryLevelDetail] = Json.format[CountryLevelDetail]
}