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

  val mtdJson = Json.parse(
    """{
      |		"income": {
      |			"totalRentsReceived": 0.12,
      |			"premiumsOfLeaseGrant": 0.12,
      |			"otherPropertyIncome": 0.12
      |		},
      |		"expenses": {
      |     "consolidatedExpenses": 0.12,
      |			"premisesRunningCosts": 0.12,
      |			"repairsAndMaintenance": 0.12,
      |			"financialCosts": 0.12,
      |			"professionalFees": 0.12,
      |			"travelCosts": 0.12,
      |			"costOfServices": 0.12,
      |			"residentialFinancialCost": 0.12,
      |			"broughtFwdResidentialFinancialCost": 0.12,
      |			"other": 0.12
      |		}
      |}""".stripMargin
  )

  val mtdJsonArray = Json.parse(
    """{[{
      |			"countryCode": "CYM",
      |			"income": {
      |				"totalRentsReceived": 0.12,
      |				"premiumsOfLeaseGrant": 0.12,
      |				"otherPropertyIncome": 0.12
      |			},
      |			"expenses": {
      |				"consolidatedExpenses": 0.12,
      |				"premisesRunningCosts": 0.12,
      |				"repairsAndMaintenance": 0.12,
      |				"financialCosts": 0.12,
      |				"professionalFees": 0.12,
      |				"travelCosts": 0.12,
      |				"costOfServices": 0.12,
      |				"residentialFinancialCost": 0.12,
      |				"broughtFwdResidentialFinancialCost": 0.12,
      |				"other": 0.12
      |			}
      |		}]
      |}""".stripMargin
  )

  val desJson = Json.parse(
    """{
      |		"income": {
      |			"rent": 0.12,
      |			"premiumsOfLeaseGrant": 0.12,
      |			"otherPropertyIncome": 0.12
      |		},
      |		"expenses": {
      |     "consolidatedExpenses": 0.12,
      |			"premisesRunningCosts": 0.12,
      |			"repairsAndMaintenance": 0.12,
      |			"financialCosts": 0.12,
      |			"professionalFees": 0.12,
      |			"travelCosts": 0.12,
      |			"costOfServices": 0.12,
      |			"residentialFinancialCost": 0.12,
      |			"broughtFwdResidentialFinancialCost": 0.12,
      |			"other": 0.12
      |		}
      |}""".stripMargin
  )

  val desJsonArray = Json.parse(
    """[{
      | 	"countryCode": "CYM",
      | 	"income": {
      | 		"rentReceived": 99999999999.99,
      | 		"premiumsOfLeaseGrant": 99999999999.99,
      | 		"otherPropertyIncome": 99999999999.99
      | 	},
      | 	"expenses": {
      | 		"premisesRunningCosts": 99999999999.99,
      | 		"repairsAndMaintenance": 99999999999.99,
      | 		"financialCosts": 99999999999.99,
      | 		"professionalFees": 99999999999.99,
      | 		"travelCosts": 99999999999.99,
      | 		"costOfServices": 99999999999.99,
      | 		"residentialFinancialCost": 99999999999.99,
      | 		"other": 99999999999.99
      | 	}
      |}]""".stripMargin
  )

  "reads" should {
    "return a valid adjustments model" when {
      "a valid json with country code are supplied" in {
        desJsonArray.as[Adjustments] shouldBe adjustmentsArrayModel
      }

      "a valid json without a country code are supplied" in {
        desJson.as[Adjustments] shouldBe adjustmentsModel
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid model is supplied" in {
        adjustmentsModel.toJson shouldBe mtdJson
      }
    }
  }
}
