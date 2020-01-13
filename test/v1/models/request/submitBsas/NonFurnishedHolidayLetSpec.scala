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

package v1.models.request.submitBsas

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import support.UnitSpec

class NonFurnishedHolidayLetSpec extends UnitSpec {

  val inputJson: JsValue = Json.parse(
    """
      |{
      |      "income": {
      |         "rentIncome": 1000.49,
      |         "premiumsOfLeaseGrant": 1000.49,
      |         "reversePremiums": 1000.49,
      |         "otherPropertyIncome": 1000.49
      |      },
      |      "expenses": {
      |         "premisesRunningCosts": -1000.49,
      |         "repairsAndMaintenance": 1000.49,
      |         "financialCosts": 1000.49,
      |         "professionalFees": 1000.49,
      |         "travelCosts": 1000.49,
      |         "costOfServices": -1000.49,
      |         "residentialFinancialCost": 1000.49,
      |         "other": 1000.49,
      |         "consolidatedExpenses": 1000.49
      |      }
      |}
      |""".stripMargin)

  val inputWithExpensesOnlyJson: JsValue = Json.parse(
    """
      |{
      |      "expenses": {
      |         "premisesRunningCosts": -1000.49,
      |         "repairsAndMaintenance": 1000.49,
      |         "financialCosts": 1000.49,
      |         "professionalFees": 1000.49,
      |         "travelCosts": 1000.49,
      |         "costOfServices": -1000.49,
      |         "residentialFinancialCost": 1000.49,
      |         "other": 1000.49,
      |         "consolidatedExpenses": 1000.49
      |      }
      |}
      |""".stripMargin)

  val requestJson: JsValue = Json.parse(
    """
      |{
      |      "income": {
      |         "totalRentsReceived": 1000.49,
      |         "premiumsOfLeaseGrant": 1000.49,
      |         "reversePremiums": 1000.49,
      |         "otherPropertyIncome": 1000.49
      |      },
      |      "expenses": {
      |         "premisesRunningCosts": -1000.49,
      |         "repairsAndMaintenance": 1000.49,
      |         "financialCosts": 1000.49,
      |         "professionalFees": 1000.49,
      |         "travelCosts": 1000.49,
      |         "costOfServices": -1000.49,
      |         "residentialFinancialCost": 1000.49,
      |         "other": 1000.49,
      |         "consolidatedExpenses": 1000.49
      |      }
      |}
      |""".stripMargin)

  val invalidJson: JsValue = Json.parse(
    """
      |{
      |      "income": {
      |         "rentIncome": 1000.45,
      |         "premiumsOfLeaseGrant": 1000.45,
      |         "reversePremiums": 1000.49,
      |         "otherPropertyIncome": 1000.45
      |      },
      |      "expenses": {
      |         "premisesRunningCosts": true,
      |         "repairsAndMaintenance": 1000.45,
      |         "financialCosts": 1000.45,
      |         "professionalFees": 1000.45,
      |         "travelCosts": 1000.45,
      |         "costOfServices": -1000.49,
      |         "residentialFinancialCost": 1000.45,
      |         "other": 1000.45,
      |         "consolidatedExpenses": 1000.49
      |      }
      |}
      |""".stripMargin)

  val model: NonFurnishedHolidayLet = NonFurnishedHolidayLet(
    Some(NonFHLIncome(
      Some(1000.49),
      Some(1000.49),
      Some(1000.49),
      Some(1000.49)
    )),
    Some(NonFHLExpenses(
      Some(-1000.49),
      Some(1000.49),
      Some(1000.49),
      Some(1000.49),
      Some(1000.49),
      Some(-1000.49),
      Some(1000.49),
      Some(1000.49),
      Some(1000.49)
    ))
  )

  val modelWithExpensesOnly: NonFurnishedHolidayLet = NonFurnishedHolidayLet(
    None,
    Some(NonFHLExpenses(
      Some(-1000.49),
      Some(1000.49),
      Some(1000.49),
      Some(1000.49),
      Some(1000.49),
      Some(-1000.49),
      Some(1000.49),
      Some(1000.49),
      Some(1000.49)
    ))
  )

  "NonFurnishedHolidayLet" when {
    "read from valid Json" should {
      "return the expected NonFurnishedHolidayLet object" in {
        inputJson.validate[NonFurnishedHolidayLet] shouldBe JsSuccess(model)
      }

      "return the expected NonFurnishedHolidayLet object without income" in {
        inputWithExpensesOnlyJson.validate[NonFurnishedHolidayLet] shouldBe JsSuccess(modelWithExpensesOnly)
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidJson.validate[NonFurnishedHolidayLet] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(model) shouldBe requestJson
      }
    }
  }
}
