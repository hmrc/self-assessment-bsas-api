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

package v4.models.response.retrieveBsas.foreignProperty

import play.api.libs.json.{Json, OWrites, Reads}

case class SummaryCalculationExpenses(consolidatedExpenses: Option[BigDecimal],
                                      premisesRunningCosts: Option[BigDecimal],
                                      repairsAndMaintenance: Option[BigDecimal],
                                      financialCosts: Option[BigDecimal],
                                      professionalFees: Option[BigDecimal],
                                      costOfServices: Option[BigDecimal],
                                      residentialFinancialCost: Option[BigDecimal],
                                      broughtFwdResidentialFinancialCost: Option[BigDecimal],
                                      other: Option[BigDecimal],
                                      travelCosts: Option[BigDecimal])

object SummaryCalculationExpenses {
  implicit val reads: Reads[SummaryCalculationExpenses] = Json.reads[SummaryCalculationExpenses]

  implicit val writes: OWrites[SummaryCalculationExpenses] = Json.writes[SummaryCalculationExpenses]
}
