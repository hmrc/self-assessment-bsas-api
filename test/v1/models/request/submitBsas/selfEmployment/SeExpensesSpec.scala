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

package v1.models.request.submitBsas.selfEmployment

import play.api.libs.json.Json
import support.UnitSpec
import v1.fixtures.request.submitBsas.selfEmployment.SeExpensesFixture._
import v1.models.domain.EmptyJsonBody


class SeExpensesSpec extends UnitSpec {

  val seExpensesModelWithoutCosts: SeExpenses =
    seExpensesModel.copy(
      staffCosts = None,
      travelCosts = None,
      premisesRunningCosts = None,
      maintenanceCosts = None,
      adminCosts = None,
      advertisingCosts = None,
      businessEntertainmentCosts = None
    )

  val emptySeExpensesModel: SeExpenses =
    SeExpenses(
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

  "SeExpenses" when {
    "read from valid JSON" should {
      "produce the expected SeExpenses object" in {
        seExpensesDesJson(seExpensesModel).as[SeExpenses] shouldBe seExpensesModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(seExpensesModel) shouldBe seExpensesMtdJson(seExpensesModel)
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        seExpensesDesJson(seExpensesModelWithoutCosts).as[SeExpenses] shouldBe seExpensesModelWithoutCosts
      }

      "not write those fields to JSON" in {
        Json.toJson(seExpensesModelWithoutCosts) shouldBe seExpensesMtdJson(seExpensesModelWithoutCosts)
      }
    }


    "no fields as supplied" should {
      "read to an empty SeExpenses object" in {
        seExpensesDesJson(emptySeExpensesModel).as[SeExpenses] shouldBe emptySeExpensesModel
      }

      "write to empty JSON" in {
        Json.toJson(emptySeExpensesModel) shouldBe Json.toJson(EmptyJsonBody)
      }
    }
  }
}
