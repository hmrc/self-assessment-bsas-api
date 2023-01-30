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

package v2.models.response.retrieveBsasAdjustments.ukProperty

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v2.models.utils.JsonErrorValidators
import v2.fixtures.ukProperty.RetrieveUkPropertyAdjustmentsFixtures._

class BsasDetailSpec extends UnitSpec with JsonErrorValidators {

  val fhlDesJson: JsValue = Json.parse(
    """{
      | "adjustments" : {
      |    "income": {
      |      "rentReceived": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
      |      "reversePremiums": 100.49,
      |      "otherPropertyIncome": 100.49
      |    },
      |    "expenses" : {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "residentialFinancialCost" : 100.49,
      |      "other": 100.49,
      |      "consolidatedExpenses": 100.49
      |   }
      | }
      |}
    """.stripMargin)

  val nonFhlDesJson: JsValue = Json.parse(
    """{
      | "adjustments" : {
      |    "income": {
      |      "totalRentsReceived": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
      |      "reversePremiums": 100.49,
      |      "otherPropertyIncome": 100.49
      |    },
      |    "expenses" : {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "residentialFinancialCost" : 100.49,
      |      "other": 100.49,
      |      "consolidatedExpenses": 100.49
      |   }
      | }
      |}
    """.stripMargin)

  val mtdJson: JsValue = Json.parse(
    """{
      | "incomes": {
      |    "rentIncome": 100.49,
      |    "premiumsOfLeaseGrant": 100.49,
      |    "reversePremiums": 100.49,
      |    "otherPropertyIncome": 100.49
      | },
      | "expenses": {
      |    "premisesRunningCosts": 100.49,
      |    "repairsAndMaintenance": 100.49,
      |    "financialCosts": 100.49,
      |    "professionalFees": 100.49,
      |    "travelCosts": 100.49,
      |    "costOfServices": 100.49,
      |    "residentialFinancialCost": 100.49,
      |    "other": 100.49,
      |    "consolidatedExpenses":100.49
      | }
      |}
    """.stripMargin)

  "BsasDetail" when {
    "reading from valid JSON" should {
      "return the appropriate FHL model" in {
        fhlDesJson.as[BsasDetail](BsasDetail.fhlReads) shouldBe bsasDetailModel
      }

      "return the appropriate non-FHL model" in {
        nonFhlDesJson.as[BsasDetail](BsasDetail.nonFhlReads) shouldBe bsasDetailModel
      }

      "not return fields when nested object optional fields are not present" in {
        val desJson = Json.parse(
          """
            |{
            |  "adjustments": {
            |     "incomes": {},
            |     "expenses": {}
            |  }
            |}
            |""".stripMargin)

        desJson.as[BsasDetail](BsasDetail.nonFhlReads) shouldBe BsasDetail(None, None)
      }
    }

    "writing to valid json" should {
      "return valid json" in {
        bsasDetailModel.toJson shouldBe mtdJson
      }
    }
  }
}
