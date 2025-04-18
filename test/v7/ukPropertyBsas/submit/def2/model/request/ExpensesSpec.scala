/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.ukPropertyBsas.submit.def2.model.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec

class ExpensesSpec extends UnitSpec {

  private val json = Json.parse("""
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
      |""".stripMargin)

  private val expenses = Expenses(
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

  private val emptyExpenses = Expenses(None, None, None, None, None, None, None, None, None)

  "reads" when {
    "given MTD json" should {
      "return the expected Expenses" in {
        json.as[Expenses] shouldBe expenses
      }
    }

    "given an empty JSON object" should {
      "return an empty Expenses" in {
        JsObject.empty.as[Expenses] shouldBe emptyExpenses
      }
    }
  }

  "writes" when {
    "given a Expenses" should {
      "return the downstream JSON" in {
        Json.toJson(expenses) shouldBe json
      }
    }

    "given an empty Expenses" should {
      "return an empty JSON object" in {
        Json.toJson(emptyExpenses) shouldBe JsObject.empty
      }
    }
  }

}
