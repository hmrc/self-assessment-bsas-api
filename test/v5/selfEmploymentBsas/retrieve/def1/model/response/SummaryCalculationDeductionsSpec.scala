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

package v5.selfEmploymentBsas.retrieve.def1.model.response

import play.api.libs.json.Json
import shared.UnitSpec
import shared.models.utils.JsonErrorValidators
import v5.selfEmploymentBsas.retrieve.def1.model.Def1_RetrieveSelfEmploymentBsasFixtures._

class SummaryCalculationDeductionsSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return a valid model" when {
      "passed valid JSON with periodId regex" in {
        downstreamSummaryCalculationDeductionsJson.as[SummaryCalculationDeductions] shouldBe summaryCalculationDeductionsModel
      }
    }
  }

  "writes" should {
    "return valid JSON" when {
      "passed a valid model with periodId" in {
        Json.toJson(summaryCalculationDeductionsModel) shouldBe mtdSummaryCalculationDeductionsJson
      }
    }
  }

}
