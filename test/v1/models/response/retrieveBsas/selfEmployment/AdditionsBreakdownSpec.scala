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
import v1.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures.{desAdditionsBreakdownJson, additionsBreakdownModel, mtdAdditionsBreakdownJson}
import v1.models.utils.JsonErrorValidators

class AdditionsBreakdownSpec extends UnitSpec with JsonErrorValidators {

  "reads" when {
    "passed valid JSON" when {
      "return a valid model" in {
        desAdditionsBreakdownJson.as[AdditionsBreakdown] shouldBe additionsBreakdownModel
      }
    }

    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/costOfGoodsDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/paymentsToSubContractorsDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/wagesAndStaffCostsDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/carVanTravelExpensesDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/premisesRunningCostsDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/maintenanceCostsDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/adminCostsDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/advertisingCostsDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/businessEntertainmentCostsDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/interestOnBankOtherLoansDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/financeChargesDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/irrecoverableDebtsDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/professionalFeesDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/depreciationDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[AdditionsBreakdown](desAdditionsBreakdownJson)(
      path = "/otherExpensesDisallowable",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
  }

  "writes" when {
    "passed a valid model" should {
      "return valid json" in {
        additionsBreakdownModel.toJson shouldBe mtdAdditionsBreakdownJson
      }
    }
  }

}
