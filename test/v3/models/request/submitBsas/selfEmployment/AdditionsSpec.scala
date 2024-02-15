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

package v3.models.request.submitBsas.selfEmployment

import play.api.libs.json.Json
import shared.UnitSpec
import shared.models.domain.EmptyJsonBody
import v3.fixtures.selfEmployment.AdditionsFixture._

class AdditionsSpec extends UnitSpec {

  val additionsModelWithoutCosts: Additions =
    Additions(
      costOfGoodsDisallowable = None,
      paymentsToSubcontractorsDisallowable = Some(3000.2),
      wagesAndStaffCostsDisallowable = None,
      carVanTravelExpensesDisallowable = None,
      premisesRunningCostsDisallowable = None,
      maintenanceCostsDisallowable = None,
      adminCostsDisallowable = None,
      advertisingCostsDisallowable = None,
      businessEntertainmentCostsDisallowable = None,
      interestOnBankOtherLoansDisallowable = Some(-3000.5),
      financeChargesDisallowable = Some(3000.6),
      irrecoverableDebtsDisallowable = Some(-3000.6),
      professionalFeesDisallowable = Some(3000.7),
      depreciationDisallowable = Some(-3000.7),
      otherExpensesDisallowable = Some(3000.8)
    )

  val emptyAdditionsModel: Additions =
    Additions(
      costOfGoodsDisallowable = None,
      paymentsToSubcontractorsDisallowable = None,
      wagesAndStaffCostsDisallowable = None,
      carVanTravelExpensesDisallowable = None,
      premisesRunningCostsDisallowable = None,
      maintenanceCostsDisallowable = None,
      adminCostsDisallowable = None,
      advertisingCostsDisallowable = None,
      businessEntertainmentCostsDisallowable = None,
      interestOnBankOtherLoansDisallowable = None,
      financeChargesDisallowable = None,
      irrecoverableDebtsDisallowable = None,
      professionalFeesDisallowable = None,
      depreciationDisallowable = None,
      otherExpensesDisallowable = None
    )

  "Additions" when {
    "read from valid vendor JSON" should {
      "produce the expected Additions object" in {
        additionsFromVendorJson(additionsModel).as[Additions] shouldBe additionsModel
      }
    }

    "written to DES JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(additionsModel) shouldBe additionsToDesJson(additionsModel)
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        additionsFromVendorJson(additionsModelWithoutCosts).as[Additions] shouldBe additionsModelWithoutCosts
      }

      "not write those fields to JSON" in {
        Json.toJson(additionsModelWithoutCosts) shouldBe additionsToDesJson(additionsModelWithoutCosts)
      }
    }

    "no fields as supplied" should {
      "read to an empty Additions object" in {
        additionsFromVendorJson(emptyAdditionsModel).as[Additions] shouldBe emptyAdditionsModel
      }

      "write to empty JSON" in {
        Json.toJson(emptyAdditionsModel) shouldBe Json.toJson(EmptyJsonBody)
      }
    }
  }

}
