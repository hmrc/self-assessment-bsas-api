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

package v1.models.response.retrieveBsasAdjustments.selfEmployment

import play.api.libs.json.Json
import support.UnitSpec
import v1.fixtures.selfEmployment.RetrieveBsasSelfEmploymentAdjustmentsFixtures._
import v1.models.utils.JsonErrorValidators


class BsasDetailSpec extends UnitSpec with JsonErrorValidators {

  val desJson = Json.parse(
    """{
      | "adjustments" : {
      |    "income": {
      |       "turnover": 100.49,
      |       "other": 100.49
      |    },
      |    "expenses" : {
      |     "costOfGoodsAllowable" : 100.49,
      |     "paymentsToSubContractorsAllowable" :100.49,
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
      |   },
      |   "additions" :{
      |     "costOfGoodsDisallowable" : 100.49,
      |     "paymentsToSubContractorsDisallowable" : 100.49,
      |     "wagesAndStaffCostsDisallowable" : 100.49,
      |     "carVanTravelExpensesDisallowable" : 100.49,
      |     "premisesRunningCostsDisallowable" : 100.49,
      |     "maintenanceCostsDisallowable" : 100.49,
      |     "adminCostsDisallowable" : 100.49,
      |     "advertisingCostsDisallowable" : 100.49,
      |     "businessEntertainmentCostsDisallowable" : 100.49,
      |     "interestOnBankOtherLoansDisallowable" : 100.49,
      |     "financeChargesDisallowable" : 100.49,
      |     "irrecoverableDebtsDisallowable" : 100.49,
      |     "professionalFeesDisallowable" : 100.49,
      |     "depreciationDisallowable" : 100.49,
      |     "otherExpensesDisallowable" : 100.49
      |   }
      | }
      |}
    """.stripMargin)

  val desJsonWithoutAdditions = Json.parse(
    """{
      | "adjustments" : {
      |    "income": {
      |       "turnover": 100.49,
      |       "other": 100.49
      |    },
      |    "expenses" : {
      |     "costOfGoodsAllowable" : 100.49,
      |     "paymentsToSubContractorsAllowable" :100.49,
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
      |   }
      | }
      |}
    """.stripMargin)

  val desJsonWithoutExpenses = Json.parse(
    """{
      | "adjustments" : {
      |    "income": {
      |       "turnover": 100.49,
      |       "other": 100.49
      |    },
      |   "additions" :{
      |     "costOfGoodsDisallowable" : 100.49,
      |     "paymentsToSubContractorsDisallowable" : 100.49,
      |     "wagesAndStaffCostsDisallowable" : 100.49,
      |     "carVanTravelExpensesDisallowable" : 100.49,
      |     "premisesRunningCostsDisallowable" : 100.49,
      |     "maintenanceCostsDisallowable" : 100.49,
      |     "adminCostsDisallowable" : 100.49,
      |     "advertisingCostsDisallowable" : 100.49,
      |     "businessEntertainmentCostsDisallowable" : 100.49,
      |     "interestOnBankOtherLoansDisallowable" : 100.49,
      |     "financeChargesDisallowable" : 100.49,
      |     "irrecoverableDebtsDisallowable" : 100.49,
      |     "professionalFeesDisallowable" : 100.49,
      |     "depreciationDisallowable" : 100.49,
      |     "otherExpensesDisallowable" : 100.49
      |   }
      | }
      |}
    """.stripMargin)

  val desJsonWithNoAdjustments = Json.parse(
    """{
      | "adjustments" : {
      |
      | }
      |}
    """.stripMargin)

  val mtdJson = Json.parse(
    """
      | {
      | "income": {
      |         "turnover": 100.49,
      |         "other": 100.49
      |      },
      |      "expenses": {
      |         "costOfGoodsBought": 100.49,
      |         "cisPaymentsToSubcontractors": 100.49,
      |         "staffCosts": 100.49,
      |         "travelCosts": 100.49,
      |         "premisesRunningCosts": 100.49,
      |         "maintenanceCosts": 100.49,
      |         "adminCosts": 100.49,
      |         "advertisingCosts": 100.49,
      |         "businessEntertainmentCosts": 100.49,
      |         "interest": 100.49,
      |         "financialCharges": 100.49,
      |         "badDebt": 100.49,
      |         "professionalFees": 100.49,
      |         "depreciation": 100.49,
      |         "other": 100.49,
      |         "consolidatedExpenses":100.49
      |      },
      |      "additions": {
      |         "costOfGoodsBoughtDisallowable": 100.49,
      |         "cisPaymentsToSubcontractorsDisallowable": 100.49,
      |         "staffCostsDisallowable": 100.49,
      |         "travelCostsDisallowable": 100.49,
      |         "premisesRunningCostsDisallowable": 100.49,
      |         "maintenanceCostsDisallowable": 100.49,
      |         "adminCostsDisallowable": 100.49,
      |         "advertisingCostsDisallowable": 100.49,
      |         "businessEntertainmentCostsDisallowable": 100.49,
      |         "interestDisallowable": 100.49,
      |         "financialChargesDisallowable": 100.49,
      |         "badDebtDisallowable": 100.49,
      |         "professionalFeesDisallowable": 100.49,
      |         "depreciationDisallowable": 100.49,
      |         "otherDisallowable": 100.49
      |      }
      |}
    """.stripMargin)

  "BsasDetail" when {
    "reading from valid JSON" should {
      "return the appropriate model" in {
        desJson.as[BsasDetail] shouldBe bsasDetailModel
      }

      "return the appropriate model when adjustments have no additions" in {
        desJsonWithoutAdditions.as[BsasDetail] shouldBe bsasDetailModel.copy(additions = None)
      }

      "return the appropriate model when adjustments have no expenses" in {
        desJsonWithoutExpenses.as[BsasDetail] shouldBe bsasDetailModel.copy(expenses = None)
      }

      "return the appropriate model with no adjustments" in {
        desJsonWithNoAdjustments.as[BsasDetail] shouldBe BsasDetail(None, None, None)
      }
    }

    "writing to valid json" should {
      "return valid json" in {
        bsasDetailModel.toJson shouldBe mtdJson
      }
    }
  }

}
