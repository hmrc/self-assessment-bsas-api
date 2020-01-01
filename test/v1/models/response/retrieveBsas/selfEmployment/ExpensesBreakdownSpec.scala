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

package v1.models.response.retrieveBsas.selfEmployment

import support.UnitSpec
import v1.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures.{desExpensesBreakdownJson, expensesBreakdownModel, mtdExpensesBreakdownJson}
import v1.models.utils.JsonErrorValidators

class ExpensesBreakdownSpec extends UnitSpec with JsonErrorValidators {

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        desExpensesBreakdownJson.as[ExpensesBreakdown] shouldBe expensesBreakdownModel
      }
    }
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/costOfGoodsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/paymentsToSubContractorsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/wagesAndStaffCostsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/carVanTravelExpensesAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/premisesRunningCostsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/maintenanceCostsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/adminCostsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/advertisingCostsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/businessEntertainmentCostsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/interestOnBankOtherLoansAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/financeChargesAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/irrecoverableDebtsAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/professionalFeesAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/depreciationAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/otherExpensesAllowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[ExpensesBreakdown](desExpensesBreakdownJson)(
      path = "/consolidatedExpenses",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
  }

  "writes" when {
    "passed a valid model" should {
      "return valid JSON" in {
        expensesBreakdownModel.toJson shouldBe mtdExpensesBreakdownJson
      }
    }
  }

}
