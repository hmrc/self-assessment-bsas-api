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
import v3.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures.expensesModel
import v3.models.utils.JsonErrorValidators

class ExpensesSpec extends UnitSpec with JsonErrorValidators{

  val mtdJson = Json.parse(
    """{
      | "consolidatedExpenses": 0.12,
      |  "premisesRunningCosts": 0.12,
      |  "repairsAndMaintenance": 0.12,
      |  "financialCosts": 0.12,
      |  "professionalFees": 0.12,
      |  "travelCosts": 0.12,
      |  "costOfServices": 0.12,
      |  "residentialFinancialCost": 0.12,
      |  "broughtFwdResidentialFinancialCost": 0.12,
      |  "other": 0.12
      |}""".stripMargin
  )

  val desJson = Json.parse(
    """{
      | "consolidatedExpenses": 0.12,
      |  "premisesRunningCosts": 0.12,
      |  "repairsAndMaintenance": 0.12,
      |  "financialCosts": 0.12,
      |  "professionalFees": 0.12,
      |  "travelCosts": 0.12,
      |  "costOfServices": 0.12,
      |  "residentialFinancialCost": 0.12,
      |  "broughtFwdResidentialFinancialCost": 0.12,
      |  "other": 0.12
      |}""".stripMargin
  )

  "reads" should {
    "return a valid expenses model" when {
      "a valid json with all fields are supplied" in {
        desJson.as[Expenses] shouldBe expensesModel
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid model is supplied" in {
        expensesModel.toJson shouldBe mtdJson
      }
    }
  }
}

