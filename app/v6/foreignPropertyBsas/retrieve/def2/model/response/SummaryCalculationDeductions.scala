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

package v6.foreignPropertyBsas.retrieve.def2.model.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class SummaryCalculationDeductions(annualInvestmentAllowance: Option[BigDecimal],
                                        costOfReplacingDomesticItems: Option[BigDecimal],
                                        zeroEmissionGoods: Option[BigDecimal],
                                        propertyAllowance: Option[BigDecimal],
                                        otherCapitalAllowance: Option[BigDecimal],
                                        structuredBuildingAllowance: Option[BigDecimal],
                                        zeroEmissionsCarAllowance: Option[BigDecimal])

object SummaryCalculationDeductions {

  implicit val reads: Reads[SummaryCalculationDeductions] = (
    (JsPath \ "annualInvestmentAllowance").readNullable[BigDecimal] and
      (JsPath \ "costOfReplacingDomesticItems").readNullable[BigDecimal] and
      (JsPath \ "zeroEmissionsGoodsVehicleAllowance").readNullable[BigDecimal] and
      (JsPath \ "propertyAllowance").readNullable[BigDecimal] and
      (JsPath \ "otherCapitalAllowance").readNullable[BigDecimal] and
      (JsPath \ "structuredBuildingAllowance").readNullable[BigDecimal] and
      (JsPath \ "zeroEmissionsCarAllowance").readNullable[BigDecimal]
  )(SummaryCalculationDeductions.apply _)

  implicit val writes: OWrites[SummaryCalculationDeductions] = Json.writes[SummaryCalculationDeductions]
}
