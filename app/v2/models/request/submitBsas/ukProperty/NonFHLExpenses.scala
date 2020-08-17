/*
 * Copyright 2020 HM Revenue & Customs
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

package v2.models.request.submitBsas.ukProperty

import play.api.libs.json.{Json, OWrites, Reads}

case class NonFHLExpenses(premisesRunningCosts: Option[BigDecimal], repairsAndMaintenance: Option[BigDecimal], financialCosts: Option[BigDecimal],
                          professionalFees: Option[BigDecimal], travelCosts: Option[BigDecimal], costOfServices: Option[BigDecimal],
                          residentialFinancialCost: Option[BigDecimal], other: Option[BigDecimal], consolidatedExpenses: Option[BigDecimal]) {

  val params: Map[String, BigDecimal] = Map(
    "premisesRunningCosts" -> premisesRunningCosts,
    "repairsAndMaintenance" -> repairsAndMaintenance,
    "financialCosts" -> financialCosts,
    "professionalFees" -> professionalFees,
    "travelCosts" -> travelCosts,
    "costOfServices" -> costOfServices,
    "residentialFinancialCost" -> residentialFinancialCost,
    "other" -> other,
    "consolidatedExpenses" -> consolidatedExpenses
  ).collect {case (k, Some(v)) => (k, v) }
}

object NonFHLExpenses {
  implicit val reads: Reads[NonFHLExpenses] = Json.reads[NonFHLExpenses]
  implicit val writes: OWrites[NonFHLExpenses] = (o: NonFHLExpenses) => Json.toJsObject(o.params)
}

