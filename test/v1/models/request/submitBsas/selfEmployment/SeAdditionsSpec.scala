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
import v1.fixtures.request.submitBsas.selfEmployment.SeAdditionsFixture._
import v1.models.domain.EmptyJsonBody

class SeAdditionsSpec extends UnitSpec {

  val seAdditionsModelWithoutCosts: SeAdditions =
    seAdditionsModel.copy(
      costOfGoodsBoughtDisallowable = None,
      staffCostsDisallowable = None,
      travelCostsDisallowable = None,
      premisesRunningCostsDisallowable = None,
      maintenanceCostsDisallowable = None,
      adminCostsDisallowable = None,
      advertisingCostsDisallowable = None,
      businessEntertainmentCostsDisallowable = None
    )

  val emptySeAdditionsModel: SeAdditions =
    SeAdditions(
      costOfGoodsBoughtDisallowable = None,
      cisPaymentsToSubcontractorsDisallowable = None,
      staffCostsDisallowable = None,
      travelCostsDisallowable = None,
      premisesRunningCostsDisallowable = None,
      maintenanceCostsDisallowable = None,
      adminCostsDisallowable = None,
      advertisingCostsDisallowable = None,
      businessEntertainmentCostsDisallowable = None,
      interestDisallowable = None,
      financialChargesDisallowable = None,
      badDebtDisallowable = None,
      professionalFeesDisallowable = None,
      depreciationDisallowable = None,
      otherDisallowable = None
    )

  "SeAdditions" when {
    "read from valid JSON" should {
      "produce the expected SeAdditions object" in {
        seAdditionsDesJson(seAdditionsModel).as[SeAdditions] shouldBe seAdditionsModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(seAdditionsModel) shouldBe seAdditionsMtdJson(seAdditionsModel)
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        seAdditionsDesJson(seAdditionsModelWithoutCosts).as[SeAdditions] shouldBe seAdditionsModelWithoutCosts
      }

      "not write those fields to JSON" in {
        Json.toJson(seAdditionsModelWithoutCosts) shouldBe seAdditionsMtdJson(seAdditionsModelWithoutCosts)
      }
    }


    "no fields as supplied" should {
      "read to an empty SeAdditions object" in {
        seAdditionsDesJson(emptySeAdditionsModel).as[SeAdditions] shouldBe emptySeAdditionsModel
      }

      "write to empty JSON" in {
        Json.toJson(emptySeAdditionsModel) shouldBe Json.toJson(EmptyJsonBody)
      }
    }
  }
}
