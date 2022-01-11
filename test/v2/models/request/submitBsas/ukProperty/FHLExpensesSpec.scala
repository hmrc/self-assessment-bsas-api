/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import support.UnitSpec

class FHLExpensesSpec extends UnitSpec {

  val inputJson: JsValue = Json.parse(
    """
      |{
      |  "premisesRunningCosts": -1000.49,
      |  "repairsAndMaintenance": 1000.49,
      |  "financialCosts": 1000.49,
      |  "professionalFees": 1000.49,
      |  "costOfServices": 1000.49,
      |  "travelCosts": 1000.49,
      |  "other": 1000.49,
      |  "consolidatedExpenses": 1000.49
      |}
      |""".stripMargin
  )

  val requestJson: JsValue = Json.parse(
    """
      |{
      |  "premisesRunningCosts": -1000.49,
      |  "repairsAndMaintenance": 1000.49,
      |  "financialCosts": 1000.49,
      |  "professionalFees": 1000.49,
      |  "costOfServices": 1000.49,
      |  "travelCosts": 1000.49,
      |  "other": 1000.49,
      |  "consolidatedExpenses": 1000.49
      |}
      |""".stripMargin
  )

  val invalidJson: JsValue = Json.parse(
    """
      |{
      |  "premisesRunningCosts": -1000.45,
      |  "repairsAndMaintenance": 1000.45,
      |  "financialCosts": 1000.45,
      |  "professionalFees": true,
      |  "costOfServices": -1000.45,
      |  "travelCosts": false,
      |  "other": 1000.45,
      |  "consolidatedExpenses": 1000.45
      |}
      |""".stripMargin
  )

  val model: FHLExpenses = FHLExpenses(Some(-1000.49), Some(1000.49),
                                      Some(1000.49), Some(1000.49),
                                      Some(1000.49), Some(1000.49),
                                      Some(1000.49), Some(1000.49))

  "FHLExpenses" when {
    "read from valid JSON" should {
      "return the full expected FHLExpenses object" in {
        inputJson.validate[FHLExpenses] shouldBe JsSuccess(model)
      }

    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidJson.validate[FHLExpenses] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(model) shouldBe requestJson
      }
    }
  }
}
