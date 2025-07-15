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

package v5.selfEmploymentBsas.retrieve.def1.model.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class SummaryCalculationDeductions(
    tradingAllowance: Option[BigDecimal],
    annualInvestmentAllowance: Option[BigDecimal],
    capitalAllowanceMainPool: Option[BigDecimal],
    capitalAllowanceSpecialRatePool: Option[BigDecimal],
    zeroEmissionGoods: Option[BigDecimal],
    businessPremisesRenovationAllowance: Option[BigDecimal],
    enhancedCapitalAllowance: Option[BigDecimal],
    allowanceOnSales: Option[BigDecimal],
    capitalAllowanceSingleAssetPool: Option[BigDecimal],
    includedNonTaxableProfits: Option[BigDecimal],
    electricChargePointAllowance: Option[BigDecimal],
    structuredBuildingAllowance: Option[BigDecimal],
    enhancedStructuredBuildingAllowance: Option[BigDecimal],
    zeroEmissionsCarAllowance: Option[BigDecimal]
)

object SummaryCalculationDeductions {

  implicit val reads: Reads[SummaryCalculationDeductions] = (
    (JsPath \ "tradingAllowance").readNullable[BigDecimal] and
      (JsPath \ "annualInvestmentAllowance").readNullable[BigDecimal] and
      (JsPath \ "capitalAllowanceMainPool").readNullable[BigDecimal] and
      (JsPath \ "capitalAllowanceSpecialRatePool").readNullable[BigDecimal] and
      (JsPath \ "zeroEmissionGoods").readNullable[BigDecimal] and
      (JsPath \ "businessPremisesRenovationAllowance").readNullable[BigDecimal] and
      (JsPath \ "enhancedCapitalAllowance").readNullable[BigDecimal] and
      (JsPath \ "allowanceOnSales").readNullable[BigDecimal] and
      (JsPath \ "capitalAllowanceSingleAssetPool").readNullable[BigDecimal] and
      (JsPath \ "includedNonTaxableProfits").readNullable[BigDecimal] and
      (JsPath \ "electricChargePointAllowance").readNullable[BigDecimal] and
      (JsPath \ "structuredBuildingAllowance").readNullable[BigDecimal] and
      (JsPath \ "enhancedStructuredBuildingAllowance").readNullable[BigDecimal] and
      (JsPath \ "zeroEmissionsCarAllowance").readNullable[BigDecimal]
  )(SummaryCalculationDeductions.apply _)

  implicit val writes: OWrites[SummaryCalculationDeductions] = Json.writes[SummaryCalculationDeductions]
}
