/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.request.submitBsas.selfEmployment

import play.api.libs.json.{Json, OWrites}

case class SeExpenses(costOfGoodsBought: Option[BigDecimal],
                      cisPaymentsToSubcontractors: Option[BigDecimal],
                      staffCosts: Option[BigDecimal],
                      travelCosts: Option[BigDecimal],
                      premisesRunningCosts: Option[BigDecimal],
                      maintenanceCosts: Option[BigDecimal],
                      adminCosts: Option[BigDecimal],
                      advertisingCosts: Option[BigDecimal],
                      businessEntertainmentCosts: Option[BigDecimal],
                      interest: Option[BigDecimal],
                      financialCharges: Option[BigDecimal],
                      badDebt: Option[BigDecimal],
                      professionalFees: Option[BigDecimal],
                      depreciation: Option[BigDecimal],
                      other: Option[BigDecimal],
                      consolidatedExpenses: Option[BigDecimal])

object SeExpenses {

  implicit val writes: OWrites[SeExpenses] = Json.writes[SeExpenses]
}
