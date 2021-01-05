/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.models.response.retrieveBsas.ukProperty

import play.api.libs.json.Json
import support.UnitSpec
import v2.models.utils.JsonErrorValidators

class ExpensesBreakdownSpec extends UnitSpec with JsonErrorValidators{

  val mtdJson = Json.parse(
    """{
      |  "premisesRunningCosts": 100.49,
      |  "repairsAndMaintenance": 100.49,
      |  "financialCosts": 100.49,
      |  "professionalFees": 100.49,
      |  "travelCosts": 100.49,
      |  "costOfServices": 100.49,
      |  "residentialFinancialCost": 100.49,
      |  "broughtFwdResidentialFinancialCost": 100.49,
      |  "other": 100.49,
      |  "consolidatedExpenses": 100.49
      |}""".stripMargin)

  val desJson = Json.parse(
    """{
      |  "premisesRunningCosts": 100.49,
      |  "repairsAndMaintenance": 100.49,
      |  "financialCosts": 100.49,
      |  "professionalFees": 100.49,
      |  "travelCosts": 100.49,
      |  "costOfServices": 100.49,
      |  "residentialFinancialCost": 100.49,
      |  "broughtFwdResidentialFinancialCost": 100.49,
      |  "other": 100.49,
      |  "consolidatedExpenses": 100.49
      |}""".stripMargin)

  val model = ExpensesBreakdown(Some(100.49),Some(100.49),Some(100.49), Some(100.49),
    Some(100.49),Some(100.49),Some(100.49), Some(100.49), Some(100.49),Some(100.49))

  "reads" should {
    "return a valid model" when {

      testPropertyType[ExpensesBreakdown](desJson)(
        path = "/premisesRunningCosts",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      testPropertyType[ExpensesBreakdown](desJson)(
        path = "/repairsAndMaintenance",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      testPropertyType[ExpensesBreakdown](desJson)(
        path = "/financialCosts",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      testPropertyType[ExpensesBreakdown](desJson)(
        path = "/professionalFees",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      testPropertyType[ExpensesBreakdown](desJson)(
        path = "/travelCosts",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      testPropertyType[ExpensesBreakdown](desJson)(
        path = "/costOfServices",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      testPropertyType[ExpensesBreakdown](desJson)(
        path = "/residentialFinancialCost",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      testPropertyType[ExpensesBreakdown](desJson)(
        path = "/broughtFwdResidentialFinancialCost",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      testPropertyType[ExpensesBreakdown](desJson)(
        path = "/other",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      testPropertyType[ExpensesBreakdown](desJson)(
        path = "/consolidatedExpenses",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      "a valid json with all fields are supplied" in {
        desJson.as[ExpensesBreakdown] shouldBe model
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid model is supplied" in {
        model.toJson shouldBe mtdJson
      }
    }
  }
}
