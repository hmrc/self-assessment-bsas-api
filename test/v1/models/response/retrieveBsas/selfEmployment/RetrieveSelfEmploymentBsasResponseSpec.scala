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
import v1.fixtures.RetrieveSelfEmploymentBsasFixtures.{
  desRetrieveSelfEmploymentBsasResponseJsonAdjusted,
  desRetrieveSelfEmploymentBsasResponseJsonAdjustable,
  mtdRetrieveSelfEmploymentBsasResponseJson,
  retrieveSelfEmploymentBsasResponseModelAdjusted,
  retrieveSelfEmploymentBsasResponseModelAdjustable
}
import v1.models.utils.JsonErrorValidators

class RetrieveSelfEmploymentBsasResponseSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return a valid model" when {
      "passed valid JSON with adjustedSummaryCalculation" in {
        desRetrieveSelfEmploymentBsasResponseJsonAdjusted.as[RetrieveSelfEmploymentBsasResponse] shouldBe retrieveSelfEmploymentBsasResponseModelAdjusted
      }
      "passed valid JSON with adjustableSummaryCalculation" in {
        desRetrieveSelfEmploymentBsasResponseJsonAdjustable.as[RetrieveSelfEmploymentBsasResponse] shouldBe retrieveSelfEmploymentBsasResponseModelAdjustable
      }
    }
  }

  "writes" should {
    "return valid JSON" when {
      "passed a valid model with adjustedSummary = true" in {
         retrieveSelfEmploymentBsasResponseModelAdjusted.toJson shouldBe mtdRetrieveSelfEmploymentBsasResponseJson(true)
      }
      "passed a valid model with adjustedSummary = false" in {
        retrieveSelfEmploymentBsasResponseModelAdjustable.toJson shouldBe mtdRetrieveSelfEmploymentBsasResponseJson(false)
      }
    }
  }

}
