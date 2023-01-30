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

package v3.models.request.submitBsas.foreignProperty

import play.api.libs.json.{ JsObject, Json }
import support.UnitSpec

class ForeignPropertyExpensesSpec extends UnitSpec {

  val model: ForeignPropertyExpenses = ForeignPropertyExpenses(
    premisesRunningCosts = Some(1.12),
    repairsAndMaintenance = Some(2.12),
    financialCosts = Some(3.12),
    professionalFees = Some(4.12),
    travelCosts = Some(5.12),
    costOfServices = Some(6.12),
    residentialFinancialCost = Some(7.12),
    other = Some(8.12),
    consolidatedExpenses = Some(9.12)
  )

  val emptyModel: ForeignPropertyExpenses = ForeignPropertyExpenses(None, None, None, None, None, None, None, None, None)

  "reads" when {
    "passed mtd json" should {
      "return the corresponding model" in {
        Json.parse("""
            |{
            |  "premisesRunningCosts": 1.12,
            |  "repairsAndMaintenance": 2.12,
            |  "financialCosts": 3.12,
            |  "professionalFees": 4.12,
            |  "travelCosts": 5.12,
            |  "costOfServices": 6.12,
            |  "residentialFinancialCost": 7.12,
            |  "other": 8.12,
            |  "consolidatedExpenses": 9.12
            |}
            |""".stripMargin).as[ForeignPropertyExpenses] shouldBe model
      }
    }

    "passed an empty JSON" should {
      "return an empty model" in {
        JsObject.empty.as[ForeignPropertyExpenses] shouldBe emptyModel
      }
    }
  }

  "writes" when {
    "passed a model" should {
      "return the downstream JSON" in {
        Json.toJson(model) shouldBe Json.parse("""
            |{
            |  "premisesRunningCosts": 1.12,
            |  "repairsAndMaintenance": 2.12,
            |  "financialCosts": 3.12,
            |  "professionalFees": 4.12,
            |  "travelCosts": 5.12,
            |  "costOfServices": 6.12,
            |  "residentialFinancialCost": 7.12,
            |  "other": 8.12,
            |  "consolidatedExpense": 9.12
            |}
            |""".stripMargin)
      }
    }

    "passed an empty model" should {
      "return an empty JSON" in {
        Json.toJson(emptyModel) shouldBe JsObject.empty
      }
    }
  }
}
