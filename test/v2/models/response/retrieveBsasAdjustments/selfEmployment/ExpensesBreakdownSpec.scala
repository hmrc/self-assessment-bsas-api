/*
 * Copyright 2020 HM Revenue & Customs
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

package v2.models.response.retrieveBsasAdjustments.selfEmployment

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v2.fixtures.selfEmployment.RetrieveSelfEmploymentAdjustmentsFixtures._
import v2.models.utils.JsonErrorValidators

class ExpensesBreakdownSpec extends UnitSpec with JsonErrorValidators {

  val desJson: JsValue = Json.parse(
    """{
      |     "costOfGoodsAllowable" : 100.49,
      |     "paymentsToSubcontractorsAllowable" :100.49,
      |     "wagesAndStaffCostsAllowable" :100.49,
      |     "carVanTravelExpensesAllowable" :100.49,
      |     "premisesRunningCostsAllowable" :100.49,
      |     "maintenanceCostsAllowable" :100.49,
      |     "adminCostsAllowable" :100.49,
      |     "advertisingCostsAllowable" :100.49,
      |     "businessEntertainmentCostsAllowable" :100.49,
      |     "interestOnBankOtherLoansAllowable" :100.49,
      |     "financeChargesAllowable" :100.49,
      |     "irrecoverableDebtsAllowable" :100.49,
      |     "professionalFeesAllowable" :100.49,
      |     "depreciationAllowable" :100.49,
      |     "otherExpensesAllowable" :100.49,
      |     "consolidatedExpenses" :100.49
      |}""".stripMargin)

  val mtdJson: JsValue = Json.parse(
    """{
      |  "costOfGoodsBought" : 100.49,
      |  "cisPaymentsToSubcontractors" : 100.49,
      |  "staffCosts" : 100.49,
      |  "travelCosts" : 100.49,
      |  "premisesRunningCosts" : 100.49,
      |  "maintenanceCosts" : 100.49,
      |  "adminCosts" : 100.49,
      |  "advertisingCosts" : 100.49,
      |  "businessEntertainmentCosts" : 100.49,
      |  "interest" : 100.49,
      |  "financialCharges" : 100.49,
      |  "badDebt" : 100.49,
      |  "professionalFees" : 100.49,
      |  "depreciation" : 100.49,
      |  "other" : 100.49,
      |  "consolidatedExpenses": 100.49
      |}
    """.stripMargin)


  "ExpensesBreakdown" when {
    "reading from valid JSON" should {
      "return the appropriate model" in {
        desJson.as[ExpensesBreakdown] shouldBe expenseBreakdownModel
      }
    }

    "writing to valid json" should {
      "return valid json" in {
        expenseBreakdownModel.toJson shouldBe mtdJson
      }
    }
  }
}
