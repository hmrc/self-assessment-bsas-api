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

package v1.models.response.retrieveBsas.ukProperty

import play.api.libs.json.Json
import support.UnitSpec
import v1.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures._
import v1.models.utils.JsonErrorValidators

class BsasDetailSpec extends UnitSpec with JsonErrorValidators{

  val desNonFhlJson = Json.parse(
    """{
      |  "totalIncome": 100.49,
      |  "income": {
      |   "totalRentsReceived": 100.49,
      |   "premiumsOfLeaseGrant": 100.49,
      |   "reversePremiums": 100.49,
      |   "otherPropertyIncome": 100.49,
      |   "rarRentReceived": 100.49
      |  },
      |  "totalExpenses": 100.49,
      |  "expenses": {
      |   "premisesRunningCosts": 100.49,
      |   "repairsAndMaintenance": 100.49,
      |   "financialCosts": 100.49,
      |   "professionalFees": 100.49,
      |   "travelCosts": 100.49,
      |   "costOfServices": 100.49,
      |   "residentialFinancialCost": 100.49,
      |   "broughtFwdResidentialFinancialCost": 100.49,
      |   "other": 100.49
      |  },
      |  "netProfit": 100.49,
      |  "netLoss": 100.49,
      |  "totalAdditions": 100.49,
      |  "totalDeductions": 100.49,
      |  "taxableProfit": 100.49,
      |  "adjustedIncomeTaxLoss": 100.49
      |}""".stripMargin)

  val desFhlJson = Json.parse(
    """{
      |  "totalIncome": 100.49,
      |  "income": {
      |   "rentReceived": 100.49,
      |   "premiumsOfLeaseGrant": 100.49,
      |   "reversePremiums": 100.49,
      |   "otherPropertyIncome": 100.49,
      |   "rarRentReceived": 100.49
      |  },
      |  "totalExpenses": 100.49,
      |  "expenses": {
      |   "premisesRunningCosts": 100.49,
      |   "repairsAndMaintenance": 100.49,
      |   "financialCosts": 100.49,
      |   "professionalFees": 100.49,
      |   "travelCosts": 100.49,
      |   "costOfServices": 100.49,
      |   "residentialFinancialCost": 100.49,
      |   "broughtFwdResidentialFinancialCost": 100.49,
      |   "other": 100.49
      |  },
      |  "netProfit": 100.49,
      |  "netLoss": 100.49,
      |  "totalAdditions": 100.49,
      |  "totalDeductions": 100.49,
      |  "taxableProfit": 100.49,
      |  "adjustedIncomeTaxLoss": 100.49
      |}""".stripMargin)

  "reads" should {
    "return a valid model" when {

      "a valid non-FHL json with all fields are supplied" in {
        desNonFhlJson.as[BsasDetail](BsasDetail.nonFhlReads) shouldBe bsasDetailModel
      }

      "a valid FHL json with all fields are supplied" in {
        desFhlJson.as[BsasDetail](BsasDetail.fhlReads) shouldBe bsasDetailModel
      }
    }

    "not return fields when all nested object optional fields are not present" in {
      val desJson = Json.parse(
        """{
          |  "totalIncome": 100.49,
          |  "totalExpenses": 100.49,
          |  "totalAdditions": 100.49,
          |  "totalDeductions": 100.49
          |}""".stripMargin)

      desJson.as[BsasDetail](BsasDetail.nonFhlReads) shouldBe BsasDetail(totalBsasModel, None, None, None, None)
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid model is supplied" in {
        bsasDetailModel.toJson shouldBe mtdBsasDetailJson
      }
    }
  }
}
