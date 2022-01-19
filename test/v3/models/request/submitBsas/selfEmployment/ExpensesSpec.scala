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

package v3.models.request.submitBsas.selfEmployment

import play.api.libs.json.Json
import support.UnitSpec
import v3.fixtures.selfEmployment.ExpensesFixture._
import v3.models.domain.EmptyJsonBody

class ExpensesSpec extends UnitSpec {

  val expensesModelWithoutCosts: Expenses =
    Expenses(
      costOfGoodsBought = None,
      cisPaymentsToSubcontractors = Some(2000.50),
      staffCosts = None,
      travelCosts = None,
      premisesRunningCosts = None,
      maintenanceCosts = None,
      adminCosts = None,
      advertisingCosts = None,
      businessEntertainmentCosts = None,
      interest = Some(-2001.25),
      financialCharges = Some(-2001.50),
      badDebt = Some(-2001.75),
      professionalFees = Some(2002.25),
      depreciation = Some(2002.50),
      other = Some(2002.75),
      None
    )

  val emptyExpensesModel: Expenses =
    Expenses(
      costOfGoodsBought = None,
      cisPaymentsToSubcontractors = None,
      staffCosts = None,
      travelCosts = None,
      premisesRunningCosts = None,
      maintenanceCosts = None,
      adminCosts = None,
      advertisingCosts = None,
      businessEntertainmentCosts = None,
      interest = None,
      financialCharges = None,
      badDebt = None,
      professionalFees = None,
      depreciation = None,
      other = None,
      consolidatedExpenses = None
    )

  "Expenses" when {
    "read from valid vendor JSON" should {
      "produce the expected Expenses object" in {
        expensesFromMtdJson(expensesModel).as[Expenses] shouldBe expensesModel
      }
    }

    "written to DES JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(expensesModel) shouldBe expensesToDesJson(expensesModel)
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        expensesFromMtdJson(expensesModelWithoutCosts).as[Expenses] shouldBe expensesModelWithoutCosts
      }

      "not write those fields to JSON" in {
        Json.toJson(expensesModelWithoutCosts) shouldBe expensesToDesJson(expensesModelWithoutCosts)
      }
    }


    "no fields as supplied" should {
      "read to an empty Expenses object" in {
        expensesFromMtdJson(emptyExpensesModel).as[Expenses] shouldBe emptyExpensesModel
      }

      "write to empty JSON" in {
        Json.toJson(emptyExpensesModel) shouldBe Json.toJson(EmptyJsonBody)
      }
    }

    "isEmpty is called" should {
      "return true when all empty fields are supplied" in {
        expensesFromMtdJson(emptyExpensesModel).as[Expenses].isEmpty shouldBe true
      }

      "return false when non-empty fields is supplied" in {
        expensesFromMtdJson(emptyExpensesModel.copy(Some(1000.49))).as[Expenses].isEmpty shouldBe false
      }
    }

    "isNonConsolidatedExpensesEmpty is called" should {
      "return true if non consolidated expenses is present" in {
        emptyExpensesModel.copy(costOfGoodsBought = Some(100.49)).isConsolidatedExpensesEmpty shouldBe true
      }

      "return true if only consolidated expenses is present" in {
        emptyExpensesModel.copy(consolidatedExpenses = Some(100.49)).isNonConsolidatedExpensesEmpty shouldBe true
      }
    }
  }
}
