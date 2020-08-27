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

package v2.models.response.retrieveBsasAdjustments.foreignProperty

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v2.fixtures.foreignProperty.RetrieveForeignPropertyAdjustmentsFixtures._
import v2.models.utils.JsonErrorValidators

class ExpensesBreakdownSpec extends UnitSpec with JsonErrorValidators {

  val fhlDesJson: JsValue = Json.parse(
    """{
      |         "premisesRunningCosts": 100.49,
      |         "repairsAndMaintenance": 100.49,
      |         "financialCosts": 100.49,
      |         "professionalFees": 100.49,
      |         "travelCosts": 100.49,
      |         "costOfServices": 100.49,
      |         "other": 100.49,
      |         "consolidatedExpenses": 100.49
      |}""".stripMargin)

  val nonFhlDesJson: JsValue = Json.parse(
    """{
      |         "premisesRunningCosts": 100.49,
      |         "repairsAndMaintenance": 100.49,
      |         "financialCosts": 100.49,
      |         "professionalFees": 100.49,
      |         "travelCosts": 100.49,
      |         "costOfServices": 100.49,
      |         "residentialFinancialCost" : 100.49,
      |         "other": 100.49,
      |         "consolidatedExpenses": 100.49
      |}""".stripMargin)

  val fhlMtdJson: JsValue = Json.parse(
    """{
      |  "premisesRunningCosts": 100.49,
      |  "repairsAndMaintenance": 100.49,
      |  "financialCosts": 100.49,
      |  "professionalFees": 100.49,
      |  "travelCosts": 100.49,
      |  "costOfServices": 100.49,
      |  "other": 100.49,
      |  "consolidatedExpenses":100.49
      |}
    """.stripMargin)

  val nonFhlMtdJson: JsValue = Json.parse(
    """{
      |  "premisesRunningCosts": 100.49,
      |  "repairsAndMaintenance": 100.49,
      |  "financialCosts": 100.49,
      |  "professionalFees": 100.49,
      |  "travelCosts": 100.49,
      |  "costOfServices": 100.49,
      |  "residentialFinancialCost": 100.49,
      |  "other": 100.49,
      |  "consolidatedExpenses":100.49
      |}
    """.stripMargin)


  "ExpensesBreakdown" when {
    "reading from valid JSON" should {
      "return the appropriate fhl model" in {
        fhlDesJson.as[ExpensesBreakdown](ExpensesBreakdown.fhlReads) shouldBe fhlExpenseBreakdownModel
      }

      "return the appropriate non fhl model" in {
        nonFhlDesJson.as[ExpensesBreakdown](ExpensesBreakdown.fhlReads) shouldBe nonFhlExpenseBreakdownModel
      }
    }

    "writing to valid json" should {
      "return valid fhl json" in {
        fhlExpenseBreakdownModel.toJson shouldBe fhlMtdJson
      }

      "return valid non fhl json" in {
        nonFhlExpenseBreakdownModel.toJson shouldBe nonFhlMtdJson
      }
    }
  }
}
