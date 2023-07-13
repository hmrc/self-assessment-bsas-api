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

package v2.models.response.retrieveBsasAdjustments.foreignProperty

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v2.fixtures.foreignProperty.RetrieveForeignPropertyAdjustmentsFixtures._
import v2.models.utils.JsonErrorValidators

class BsasDetailSpec extends UnitSpec with JsonErrorValidators {

  val fhlDesJson: JsValue = Json.parse(
    """{
      |    "income": {
      |      "rent": 100.49
      |    },
      |    "expenses" : {
      |      "premisesRunningCosts": 100.49,
      |      "repairsAndMaintenance": 100.49,
      |      "financialCosts": 100.49,
      |      "professionalFees": 100.49,
      |      "travelCosts": 100.49,
      |      "costOfServices": 100.49,
      |      "other": 100.49,
      |      "consolidatedExpenses":100.49
      |   }
      |}
    """.stripMargin)

  val nonFhlDesJson: JsValue = Json.parse(
    """{
      |    "countryCode": "FRA",
      |    "income": {
      |      "rent": 100.49,
      |      "premiumsOfLeaseGrant": 100.49,
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
      |      "consolidatedExpenses":100.49
      |   }
      |}
      |""".stripMargin)

  val fhlMtdJson: JsValue = Json.parse(
    """
    {
      "incomes": {
        "rentIncome": 100.49
      },
      "expenses": {
        "premisesRunningCosts": 100.49,
        "repairsAndMaintenance": 100.49,
        "financialCosts": 100.49,
        "professionalFees": 100.49,
        "travelCosts": 100.49,
        "costOfServices": 100.49,
        "other": 100.49,
        "consolidatedExpenses":100.49
      }
    }
    """.stripMargin)

  val nonFhlMtdJson: JsValue = Json.parse(
    """{
      | "countryCode": "FRA",
      | "incomes": {
      |    "rentIncome": 100.49,
      |    "premiumsOfLeaseGrant": 100.49,
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
        fhlDesJson.as[BsasDetail](BsasDetail.fhlReads) shouldBe fhlBsasDetailModel
      }

      "return the appropriate non-FHL model" in {
        nonFhlDesJson.as[BsasDetail](BsasDetail.nonFhlReads) shouldBe nonFhlBsasDetailModel
      }

      "not return fields when nested object optional fields are not present" in {
        val desJson = Json.parse(
          """
            |{
            |     "income": {},
            |     "expenses": {}
            |}
            |""".stripMargin)

        desJson.as[BsasDetail](BsasDetail.nonFhlReads) shouldBe BsasDetail(None, None, None)
        desJson.as[BsasDetail](BsasDetail.fhlReads) shouldBe BsasDetail(None, None, None)
      }
    }

    "writing to valid json" should {
      "return valid non fhl json" in {
        nonFhlBsasDetailModel.toJson shouldBe nonFhlMtdJson
      }

      "return valid fhl json" in {
        fhlBsasDetailModel.toJson shouldBe fhlMtdJson
      }
    }
  }
}
