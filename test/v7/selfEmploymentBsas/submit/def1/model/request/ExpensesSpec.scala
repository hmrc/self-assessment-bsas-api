/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.selfEmploymentBsas.submit.def1.model.request

import play.api.libs.json.Json
import shared.models.domain.EmptyJsonBody
import shared.utils.UnitSpec
import v7.selfEmploymentBsas.submit.def1.model.request.fixtures.ExpensesFixture._

class ExpensesSpec extends UnitSpec {

  val expensesWithoutCosts: Expenses =
    Expenses(
      costOfGoodsAllowable = None,
      paymentsToSubcontractorsAllowable = Some(2000.50),
      wagesAndStaffCostsAllowable = None,
      carVanTravelExpensesAllowable = None,
      premisesRunningCostsAllowable = None,
      maintenanceCostsAllowable = None,
      adminCostsAllowable = None,
      advertisingCostsAllowable = None,
      businessEntertainmentCostsAllowable = None,
      interestOnBankOtherLoansAllowable = Some(-2001.25),
      financeChargesAllowable = Some(-2001.50),
      irrecoverableDebtsAllowable = Some(-2001.75),
      professionalFeesAllowable = Some(2002.25),
      depreciationAllowable = Some(2002.50),
      otherExpensesAllowable = Some(2002.75),
      consolidatedExpenses = None
    )

  val emptyExpenses: Expenses =
    Expenses(
      costOfGoodsAllowable = None,
      paymentsToSubcontractorsAllowable = None,
      wagesAndStaffCostsAllowable = None,
      carVanTravelExpensesAllowable = None,
      premisesRunningCostsAllowable = None,
      maintenanceCostsAllowable = None,
      adminCostsAllowable = None,
      advertisingCostsAllowable = None,
      businessEntertainmentCostsAllowable = None,
      interestOnBankOtherLoansAllowable = None,
      financeChargesAllowable = None,
      irrecoverableDebtsAllowable = None,
      professionalFeesAllowable = None,
      depreciationAllowable = None,
      otherExpensesAllowable = None,
      consolidatedExpenses = None
    )

  "Expenses" when {
    "read from valid vendor JSON" should {
      "produce the expected Expenses object" in {
        expensesFromMtdJson(expenses).as[Expenses] shouldBe expenses
      }
    }

    "written to DES JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(expenses) shouldBe expensesToDesJson(expenses)
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        expensesFromMtdJson(expensesWithoutCosts).as[Expenses] shouldBe expensesWithoutCosts
      }

      "not write those fields to JSON" in {
        Json.toJson(expensesWithoutCosts) shouldBe expensesToDesJson(expensesWithoutCosts)
      }
    }

    "no fields as supplied" should {
      "read to an empty Expenses object" in {
        expensesFromMtdJson(emptyExpenses).as[Expenses] shouldBe emptyExpenses
      }

      "write to empty JSON" in {
        Json.toJson(emptyExpenses) shouldBe Json.toJson(EmptyJsonBody)
      }
    }
  }

}
