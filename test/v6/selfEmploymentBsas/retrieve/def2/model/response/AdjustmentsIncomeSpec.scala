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

package v6.selfEmploymentBsas.retrieve.def2.model.response

import play.api.libs.json.Json
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v6.selfEmploymentBsas.retrieve.def2.model.Def2_RetrieveSelfEmploymentBsasFixtures.*

class AdjustmentsIncomeSpec extends UnitSpec with JsonErrorValidators {

  "reads" should {
    "return a valid model" when {
      "passed valid JSON" in {
        downstreamAdjustmentsIncomeJson.as[AdjustmentsIncome] shouldBe adjustmentsIncome
      }
    }
  }

  "writes" should {
    "return valid JSON" when {
      "passed a valid model" in {
        Json.toJson(adjustmentsIncome) shouldBe mtdAdjustmentsIncomeJson
      }
    }
  }

}
