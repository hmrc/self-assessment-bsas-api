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

package v3.models.response.retrieveBsas.foreignProperty

import play.api.libs.json.Json
import support.UnitSpec
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._
import v3.models.utils.JsonErrorValidators

class AdjustmentsSpec extends UnitSpec with JsonErrorValidators{



  val mtdFhlEeaJson = Json.parse(
    """{
      |		"income": {
      |			"totalRentsReceived": 99999999999.99,
      |			"premiumsOfLeaseGrant": 99999999999.99,
      |			"otherPropertyIncome": 99999999999.99
      |		},
      |		"expenses": {
      |     "consolidatedExpenses": 99999999999.99,
      |			"premisesRunningCosts": 99999999999.99,
      |			"repairsAndMaintenance": 99999999999.99,
      |			"financialCosts": 99999999999.99,
      |			"professionalFees": 99999999999.99,
      |			"travelCosts": 99999999999.99,
      |			"costOfServices": 99999999999.99,
      |			"residentialFinancialCost": 99999999999.99,
      |			"broughtFwdResidentialFinancialCost": 99999999999.99,
      |			"other": 99999999999.99
      |		}
      |}""".stripMargin
  )

  val desFhlEeaJson = Json.parse(
    """{
      |		"income": {
      |			"rent": 99999999999.99,
      |			"premiumsOfLeaseGrant": 99999999999.99,
      |			"otherPropertyIncome": 99999999999.99
      |		},
      |		"expenses": {
      |     "consolidatedExpenses": 99999999999.99,
      |			"premisesRunningCosts": 99999999999.99,
      |			"repairsAndMaintenance": 99999999999.99,
      |			"financialCosts": 99999999999.99,
      |			"professionalFees": 99999999999.99,
      |			"travelCosts": 99999999999.99,
      |			"costOfServices": 99999999999.99,
      |			"residentialFinancialCost": 99999999999.99,
      |			"broughtFwdResidentialFinancialCost": 99999999999.99,
      |			"other": 99999999999.99
      |		}
      |}""".stripMargin
  )

  val mtdNonFhlJson = Json.parse(
    """{
      |	"countryLevelDetail": [{
      |		"countryCode": "CYM",
      |		"income": {
      |			"totalRentsReceived": 99999999999.99,
      |			"premiumsOfLeaseGrant": 99999999999.99,
      |			"otherPropertyIncome": 99999999999.99
      |		},
      |		"expenses": {
      |			"consolidatedExpenses": 99999999999.99,
      |			"premisesRunningCosts": 99999999999.99,
      |			"repairsAndMaintenance": 99999999999.99,
      |			"financialCosts": 99999999999.99,
      |			"professionalFees": 99999999999.99,
      |			"travelCosts": 99999999999.99,
      |			"costOfServices": 99999999999.99,
      |			"residentialFinancialCost": 99999999999.99,
      |			"broughtFwdResidentialFinancialCost": 99999999999.99,
      |			"other": 99999999999.99
      |		}
      |	}]
      |}""".stripMargin
  )


  "reads" should {
    "return a valid adjustments model" when {
      "a valid json for fhlEea is supplied" in {
        desFhlEeaJson.as[Adjustments](Adjustments.reads) shouldBe adjustmentsFhlEeaModel
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid fhlEea model is supplied" in {
        adjustmentsFhlEeaModel.toJson shouldBe mtdFhlEeaJson
      }

      "a valid nonFhl model is supplied" in {
        adjustmentsNonFhlModel.toJson shouldBe mtdNonFhlJson
      }
    }
  }
}
