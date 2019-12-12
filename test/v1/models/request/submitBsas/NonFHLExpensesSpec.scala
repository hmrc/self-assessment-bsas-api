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

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import support.UnitSpec

class NonFHLExpensesSpec extends UnitSpec{

  val inputJson: JsValue = Json.parse(
    """
      |{
      |  "premisesRunningCosts": -1000.49,
      |  "repairsAndMaintenance": 1000.49,
      |  "financialCosts": 1000.49,
      |  "professionalFees": 1000.49,
      |  "travelCosts": 1000.49,
      |  "costOfServices": -1000.49,
      |  "residentialFinancialCost": 1000.49,
      |  "other": 1000.49,
      |  "consolidatedExpenses": 1000.49
      |}
      |""".stripMargin
  )

  val inputWithMissingValuesJson: JsValue = Json.parse(
    """
      |{
      |  "repairsAndMaintenance": 1000.49,
      |  "financialCosts": 1000.49,
      |  "professionalFees": 1000.49,
      |  "costOfServices": -1000.49,
      |  "residentialFinancialCost": 1000.49,
      |  "other": 1000.49,
      |  "consolidatedExpenses": 1000.49
      |}
      |""".stripMargin
  )

  val requestJson: JsValue = Json.parse(
    """
      |{
      |  "premisesRunningCosts": "-1000.49",
      |  "repairsAndMaintenance": "1000.49",
      |  "financialCosts": "1000.49",
      |  "professionalFees": "1000.49",
      |  "travelCosts": "1000.49",
      |  "costOfServices": "-1000.49",
      |  "residentialFinancialCost": "1000.49",
      |  "other": "1000.49",
      |    "consolidatedExpenses": "1000.49"
      |}
      |""".stripMargin
  )

  val requestWithMissingValuesJson: JsValue = Json.parse(
    """
      |{
      |  "repairsAndMaintenance": "1000.49",
      |  "financialCosts": "1000.49",
      |  "professionalFees": "1000.49",
      |  "costOfServices": "-1000.49",
      |  "residentialFinancialCost": "1000.49",
      |  "other": "1000.49",
      |  "consolidatedExpenses": "1000.49"
      |}
      |""".stripMargin
  )

  val invalidJson: JsValue = Json.parse(
    """
      |{
      |  "premisesRunningCosts": "-1000.49",
      |  "repairsAndMaintenance": "1000.49",
      |  "financialCosts": "1000.49",
      |  "professionalFees": true,
      |  "travelCosts": false,
      |  "costOfServices": "-1000.49",
      |  "residentialFinancialCost": "1000.49",
      |  "other": "1000.49",
      |    "consolidatedExpenses": "1000.49"
      |}
      |""".stripMargin
  )

  val model: NonFHLExpenses = NonFHLExpenses(Some(-1000.49), Some(1000.49),
                                            Some(1000.49), Some(1000.49),
                                            Some(1000.49), Some(-1000.49),
                                            Some(1000.49), Some(1000.49),
                                            Some(1000.49))

  val modelWithNoneValues: NonFHLExpenses = NonFHLExpenses(None, Some(1000.49),
                                            Some(1000.49), Some(1000.49),
                                            None, Some(-1000.49),
                                            Some(1000.49), Some(1000.49),
                                            Some(1000.49))

  "NonFHLExpenses" when {
    "read from valid JSON" should {
      "return the full expected NonFHLExpenses object" in {
        inputJson.validate[NonFHLExpenses] shouldBe JsSuccess(model)
      }

      "return part of the expected NonFHLExpenses object" in {
        inputWithMissingValuesJson.validate[NonFHLExpenses] shouldBe JsSuccess(modelWithNoneValues)
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidJson.validate[NonFHLExpenses] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(model) shouldBe requestJson
      }

      "return the expected JsValue without missing values" in {
        Json.toJson(modelWithNoneValues) shouldBe requestWithMissingValuesJson
      }
    }
  }
}
