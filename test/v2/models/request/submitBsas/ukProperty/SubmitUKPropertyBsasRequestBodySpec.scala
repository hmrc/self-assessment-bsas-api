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

package v2.models.request.submitBsas.ukProperty

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import support.UnitSpec
import v2.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._

class SubmitUKPropertyBsasRequestBodySpec extends UnitSpec{

  val nonFHLDesJson: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceType":"02",
      |  "income": {
      |       "totalRentsReceived": 1000.45,
      |       "premiumsOfLeaseGrant": 1000.45,
      |       "reversePremiums": 1000.45,
      |       "otherPropertyIncome": 1000.45
      |     },
      |     "expenses": {
      |       "premisesRunningCosts": 1000.45,
      |       "repairsAndMaintenance": 1000.45,
      |       "financialCosts": 1000.45,
      |       "professionalFees": 1000.45,
      |       "travelCosts": 1000.45,
      |       "costOfServices": 1000.45,
      |       "residentialFinancialCost": 1000.45,
      |       "other": 1000.45,
      |       "consolidatedExpenses": 1000.45
      |     }
      |   }
      |""".stripMargin)

  val fhlDesJson: JsValue = Json.parse(
    """
      |{
      | "incomeSourceType":"04",
      | "income": {
      |     "rentReceived": 1000.45
      |   },
      |   "expenses": {
      |     "premisesRunningCosts": 1000.45,
      |     "repairsAndMaintenance": 1000.45,
      |     "financialCosts": 1000.45,
      |     "professionalFees": 1000.45,
      |     "costOfServices": 1000.45,
      |     "travelCosts": 1000.45,
      |     "other": 1000.45,
      |     "consolidatedExpenses": 1000.45
      |   }
      |}
      |""".stripMargin)

  "SubmitUKPropertyBsasRequestBody" when {
    "read from valid JSON" should {
      "return the expected SubmitUKPropertyRequestBody with a nonFHL" in {
        nonFHLInputJson.validate[SubmitUKPropertyBsasRequestBody] shouldBe JsSuccess(nonFHLBody)
      }

      "return the expected SubmitUKPropertyRequestBody with a FHL" in {
        fhlInputJson.validate[SubmitUKPropertyBsasRequestBody] shouldBe JsSuccess(fhlBody)
      }
    }

    "read from invalid JSON" should {
      "return the expected SubmitUKPropertyRequestBody with a nonFHL" in {
        nonFHLInvalidJson.validate[SubmitUKPropertyBsasRequestBody] shouldBe a[JsError]
      }

      "return the expected SubmitUKPropertyRequestBody with a FHL" in {
        fhlInvalidJson.validate[SubmitUKPropertyBsasRequestBody] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected SubmitUKPropertyRequestBody with a nonFHL" in {
        Json.toJson(nonFHLBody) shouldBe nonFHLDesJson
      }

      "return the expected SubmitUKPropertyRequestBody with a FHL" in {
        Json.toJson(fhlBody) shouldBe fhlDesJson
      }
    }
  }
}
