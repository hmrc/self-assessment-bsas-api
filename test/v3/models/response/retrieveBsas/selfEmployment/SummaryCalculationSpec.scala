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

package v3.models.response.retrieveBsas.selfEmployment

import play.api.libs.json.Json
import shared.UnitSpec
import shared.models.utils.JsonErrorValidators
import v3.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures.{adjustableSummaryCalculationModel, adjustedSummaryCalculationModel, downstreamSummaryCalculationJson, mtdSummaryCalculationJson}

class SummaryCalculationSpec extends UnitSpec with JsonErrorValidators {

  "reads" when {
    "passed valid JSON" should {
      "return a valid adjustableSummaryCalculationModel" in {
        downstreamSummaryCalculationJson.as[AdjustableSummaryCalculation] shouldBe adjustableSummaryCalculationModel
      }
      "return a valid adjustedSummaryCalculationModel" in {
        downstreamSummaryCalculationJson.as[AdjustedSummaryCalculation] shouldBe adjustedSummaryCalculationModel
      }
    }
  }

  "writes" should {
    "return valid JSON" when {
      "passed a valid adjustableSummaryCalculationModel" in {
        Json.toJson(adjustableSummaryCalculationModel) shouldBe mtdSummaryCalculationJson
      }
      "passed a valid adjustedSummaryCalculationModel" in {
        Json.toJson(adjustedSummaryCalculationModel) shouldBe mtdSummaryCalculationJson
      }
    }
  }

}
