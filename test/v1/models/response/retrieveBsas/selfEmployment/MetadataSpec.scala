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
import v1.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures.{
  desRetrieveBsasResponseJsonAdjustable,
  desRetrieveBsasResponseJsonAdjusted,
  metadataModel,
  mtdMetadataJson
}
import v1.models.utils.JsonErrorValidators

class MetadataSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return a valid model" when {
      "passed valid JSON with adjustedSummaryCalculation" in {
        desRetrieveBsasResponseJsonAdjusted.as[Metadata] shouldBe metadataModel(true)
      }
      "passed valid JSON with adjustableSummaryCalculation" in {
        desRetrieveBsasResponseJsonAdjustable.as[Metadata] shouldBe metadataModel(false)
      }
    }
  }

  "writes" should {
    "return valid JSON" when {
      "passed a valid model" in {
        metadataModel(true).toJson shouldBe mtdMetadataJson(true)
      }
    }
  }

}
