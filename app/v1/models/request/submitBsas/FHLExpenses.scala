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

package v1.models.request.submitBsas

import java.io

import play.api.libs.json.{JsObject, Json, OWrites, Reads}

case class FHLExpenses(premisesRunningCosts: Option[BigDecimal], repairsAndMaintenance: Option[BigDecimal],
                       financialCosts: Option[BigDecimal], professionalFees: Option[BigDecimal], costOfServices: Option[BigDecimal],
                       travelCosts: Option[BigDecimal], other: Option[BigDecimal], consolidatedExpenses: Option[BigDecimal]){

  val params: Map[String, Option[BigDecimal]] = Map(
    "premisesRunningCosts" -> premisesRunningCosts,
    "repairsAndMaintenance" -> repairsAndMaintenance,
    "financialCosts" -> financialCosts,
    "professionalFees" -> professionalFees,
    "costOfServices" -> costOfServices,
    "travelCosts" -> travelCosts,
    "other" -> other,
    "consolidatedExpenses" -> consolidatedExpenses
  ).filterNot {case (_, v) => v.isEmpty }

    def queryMap[A](as: Map[String, A]): Map[String, String] = as.map {
    case (k: String, Some(v)) => (k, v.toString)
  }

  val mappedPresentParams: Map[String, String] = queryMap(params)
}

object FHLExpenses {
  implicit val reads: Reads[FHLExpenses] = Json.reads[FHLExpenses]
  implicit val writes: OWrites[FHLExpenses] = new OWrites[FHLExpenses] {
    override def writes(o: FHLExpenses): JsObject = Json.toJsObject(o.mappedPresentParams)
  }
}