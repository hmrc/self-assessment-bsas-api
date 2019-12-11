/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.response.retrieveBsas.selfEmployment

import support.UnitSpec
import v1.fixtures.RetrieveSelfEmploymentBsasFixtures.{desExpensesBreakdownSelfEmploymentJson, expensesBreakdownSelfEmploymentModel, mtdExpensesBreakdownSelfEmploymentJson}
import v1.models.utils.JsonErrorValidators

class ExpensesBreakdownSelfEmploymentSpec extends UnitSpec with JsonErrorValidators {

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        desExpensesBreakdownSelfEmploymentJson.as[ExpensesBreakdownSelfEmployment] shouldBe expensesBreakdownSelfEmploymentModel
      }
    }
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/costOfGoodsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/paymentsToSubContractorsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/wagesAndStaffCostsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/carVanTravelExpensesAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/premisesRunningCostsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/maintenanceCostsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/adminCostsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/advertisingCostsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/businessEntertainmentCostsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/interestOnBankOtherLoansAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/financeChargesAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/irrecoverableDebtsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/professionalFeesAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/depreciationAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/otherExpensesAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdownSelfEmployment](desExpensesBreakdownSelfEmploymentJson)(
      path = "/consolidatedExpenses",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
  }

  "writes" when {
    "passed a valid model" should {
      "return valid JSON" in {
        expensesBreakdownSelfEmploymentModel.toJson shouldBe mtdExpensesBreakdownSelfEmploymentJson
      }
    }
  }

}
