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

package v2.models.response.retrieveBsasAdjustments.selfEmployment

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v2.fixtures.selfEmployment.RetrieveSelfEmploymentAdjustmentsFixtures._
import v2.models.utils.JsonErrorValidators

class AdditionsBreakdownSpec extends UnitSpec with JsonErrorValidators {

  val mtdJson: JsValue = Json.parse(
    """{
      |     "costOfGoodsBoughtDisallowable": 100.49,
      |     "cisPaymentsToSubcontractorsDisallowable": 100.49,
      |     "staffCostsDisallowable": 100.49,
      |     "travelCostsDisallowable": 100.49,
      |     "premisesRunningCostsDisallowable": 100.49,
      |     "maintenanceCostsDisallowable": 100.49,
      |     "adminCostsDisallowable": 100.49,
      |     "advertisingCostsDisallowable": 100.49,
      |     "businessEntertainmentCostsDisallowable": 100.49,
      |     "interestDisallowable": 100.49,
      |     "financialChargesDisallowable": 100.49,
      |     "badDebtDisallowable": 100.49,
      |     "professionalFeesDisallowable": 100.49,
      |     "depreciationDisallowable": 100.49,
      |     "otherDisallowable": 100.49
      | }
    """.stripMargin)

  val desJson: JsValue = Json.parse(
    """{
      |   "costOfGoodsDisallowable" : 100.49,
      |   "paymentsToSubcontractorsDisallowable" : 100.49,
      |    "wagesAndStaffCostsDisallowable" : 100.49,
      |    "carVanTravelExpensesDisallowable" : 100.49,
      |    "premisesRunningCostsDisallowable" : 100.49,
      |    "maintenanceCostsDisallowable" : 100.49,
      |    "adminCostsDisallowable" : 100.49,
      |    "advertisingCostsDisallowable" : 100.49,
      |    "businessEntertainmentCostsDisallowable" : 100.49,
      |    "interestOnBankOtherLoansDisallowable" : 100.49,
      |    "financeChargesDisallowable" : 100.49,
      |    "irrecoverableDebtsDisallowable" : 100.49,
      |    "professionalFeesDisallowable" : 100.49,
      |    "depreciationDisallowable" : 100.49,
      |    "otherExpensesDisallowable" : 100.49
      |}""".stripMargin)

  "AdditionBreakdown" when {
    "reading from valid JSON" should {
      "return the appropriate model" in {
        desJson.as[AdditionsBreakdown] shouldBe additionsBreakdownModel
      }
    }

    "writing to valid json" should {
      "return valid json" in {
        additionsBreakdownModel.toJson shouldBe mtdJson
      }
    }
  }
}
