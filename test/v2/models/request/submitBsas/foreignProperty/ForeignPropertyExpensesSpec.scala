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

package v2.models.request.submitBsas.foreignProperty

import play.api.libs.json.Json
import support.UnitSpec

class ForeignPropertyExpensesSpec extends UnitSpec {

  val mtdJson = Json.parse(
    """
      |{
      |  "premisesRunningCosts": 123.12,
      |  "repairsAndMaintenance": 123.13,
      |  "financialCosts": 123.14,
      |  "professionalFees": 123.15,
      |  "travelCosts": 123.16,
      |  "costOfServices": 123.17,
      |  "residentialFinancialCost": 123.18,
      |  "other": 123.19,
      |  "consolidatedExpenses": 123.20
      |}
      |""".stripMargin)

  val desJson = Json.parse(
    """
      |{
      |  "premisesRunningCosts": 123.12,
      |  "repairsAndMaintenance": 123.13,
      |  "financialCosts": 123.14,
      |  "professionalFees": 123.15,
      |  "travelCosts": 123.16,
      |  "costOfServices": 123.17,
      |  "residentialFinancialCost": 123.18,
      |  "other": 123.19,
      |  "consolidatedExpense": 123.20
      |}
      |""".stripMargin)

  val emptyJson = Json.parse("""{}""")

  val validModel = ForeignPropertyExpenses(
    Some(123.12),
    Some(123.13),
    Some(123.14),
    Some(123.15),
    Some(123.16),
    Some(123.17),
    Some(123.18),
    Some(123.19),
    Some(123.20))

  val emptyModel = ForeignPropertyExpenses(None,None,None,None,None,None,None,None,None)



  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        mtdJson.as[ForeignPropertyExpenses] shouldBe validModel
      }
    }
  }
  "reads from an empty JSON" when{
    "passed an empty JSON" should {
      "return an empty model" in {
        emptyJson.as[ForeignPropertyExpenses] shouldBe emptyModel
      }
    }
  }
  "writes" when {
    "passed valid model" should {
      "return valid JSON" in {
        Json.toJson(validModel) shouldBe desJson
      }
    }
  }
  "write from an empty body" when {
    "passed an empty model" should {
      "return an empty JSON" in {
        Json.toJson(emptyModel) shouldBe emptyJson
      }
    }
  }

  "isEmpty" when {
    "passed a non empty model" should {
      "return false" in {
        validModel.isEmpty shouldBe false
      }
    }
    "passed an empty model" should {
      "return true" in {
        emptyModel.isEmpty shouldBe true
      }
    }
  }
}