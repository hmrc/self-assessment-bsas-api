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

package v6.ukPropertyBsas.retrieve.def2.model.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class SummaryCalculationDeductions(
    zeroEmissionGoods: Option[BigDecimal],
    annualInvestmentAllowance: Option[BigDecimal],
    costOfReplacingDomesticItems: Option[BigDecimal],
    businessPremisesRenovationAllowance: Option[BigDecimal],
    propertyAllowance: Option[BigDecimal],
    otherCapitalAllowance: Option[BigDecimal],
    rarReliefClaimed: Option[BigDecimal],
    structuredBuildingAllowance: Option[BigDecimal],
    enhancedStructuredBuildingAllowance: Option[BigDecimal],
    zeroEmissionsCarAllowance: Option[BigDecimal]
)

object SummaryCalculationDeductions {

  val reads: Reads[SummaryCalculationDeductions] = (
    (JsPath \ "zeroEmissionsGoodsVehicleAllowance").readNullable[BigDecimal] and
      (JsPath \ "annualInvestmentAllowance").readNullable[BigDecimal] and
      (JsPath \ "costOfReplacingDomesticItems").readNullable[BigDecimal] and
      (JsPath \ "businessPremisesRenovationAllowance").readNullable[BigDecimal] and
      (JsPath \ "propertyAllowance").readNullable[BigDecimal] and
      (JsPath \ "otherCapitalAllowance").readNullable[BigDecimal] and
      (JsPath \ "rarReliefClaimed").readNullable[BigDecimal] and
      (JsPath \ "structuredBuildingAllowance").readNullable[BigDecimal] and
      (JsPath \ "enhancedStructuredBuildingAllowance").readNullable[BigDecimal] and
      (JsPath \ "zeroEmissionsCarAllowance").readNullable[BigDecimal]
  )(SummaryCalculationDeductions.apply)

  implicit val writes: OWrites[SummaryCalculationDeductions] = Json.writes[SummaryCalculationDeductions]
}
