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

package v6.bsas.trigger.def1.model.request

import play.api.libs.json.Json
import shared.utils.UnitSpec
import v6.bsas.trigger.def1.model.Def1_TriggerBsasFixtures.*

class Def1_TriggerBsasRequestBodySpec extends UnitSpec {

  "TriggerBsasRequestBody" when {
    "reads" should {
      "return the expected TriggerBsasRequestBody object" in {
        mtdJson.as[Def1_TriggerBsasRequestBody] shouldBe triggerBsasRequestBody
      }
    }

    "writes" should {
      "return the expected JSON" in {
        Json.toJson(triggerBsasRequestBody) shouldBe downstreamJson
      }
    }
  }

}
