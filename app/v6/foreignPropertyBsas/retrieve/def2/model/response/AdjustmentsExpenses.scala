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

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class AdjustmentsExpenses(
    consolidatedExpenses: Option[BigDecimal],
    premisesRunningCosts: Option[BigDecimal],
    repairsAndMaintenance: Option[BigDecimal],
    financialCosts: Option[BigDecimal],
    professionalFees: Option[BigDecimal],
    costOfServices: Option[BigDecimal],
    residentialFinancialCost: Option[BigDecimal],
    other: Option[BigDecimal],
    travelCosts: Option[BigDecimal]
)

object AdjustmentsExpenses {

  val reads: Reads[AdjustmentsExpenses] = (
    (JsPath \ "consolidatedExpenses").readNullable[BigDecimal] and
      (JsPath \ "premisesRunningCosts").readNullable[BigDecimal] and
      (JsPath \ "repairsAndMaintenance").readNullable[BigDecimal] and
      (JsPath \ "financialCosts").readNullable[BigDecimal] and
      (JsPath \ "professionalFees").readNullable[BigDecimal] and
      (JsPath \ "costOfServices").readNullable[BigDecimal] and
      (JsPath \ "residentialFinancialCost").readNullable[BigDecimal] and
      (JsPath \ "other").readNullable[BigDecimal] and
      (JsPath \ "travelCosts").readNullable[BigDecimal]
  )(AdjustmentsExpenses.apply _)

  implicit val writes: OWrites[AdjustmentsExpenses] = Json.writes[AdjustmentsExpenses]
}
