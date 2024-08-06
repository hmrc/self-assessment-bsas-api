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

package v4.models.request.submitBsas.selfEmployment

import play.api.libs.json.Json
import shared.models.domain.EmptyJsonBody
import shared.utils.UnitSpec
import v4.fixtures.selfEmployment.ExpensesFixture._
import v4.models.request.submitBsas.selfEmployment.Expenses

class ExpensesSpec extends UnitSpec {

  val expensesModelWithoutCosts: Expenses =
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

  val emptyExpensesModel: Expenses =
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
  }

}
