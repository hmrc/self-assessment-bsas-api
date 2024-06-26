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

package v4.models.request.triggerBsas

import play.api.libs.json.Json
import shared.utils.UnitSpec
import v4.fixtures.TriggerBsasRequestBodyFixtures._

class TriggerBsasRequestBodySpec extends UnitSpec {

  "TriggerBsasRequestBody" when {
    "reads" should {
      "return the expected TriggerBsasRequestBody object" in {
        mtdJson.as[TriggerBsasRequestBody] shouldBe model
      }
    }

    "writes" should {
      "return the expected JSON" in {
        Json.toJson(model) shouldBe downstreamJson
      }
    }
  }

}
