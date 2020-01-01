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

package v1.models.request.triggerBsas

import play.api.libs.json.{JsError, JsSuccess, Json}
import support.UnitSpec
import v1.fixtures.TriggerBsasRequestBodyFixtures._

class TriggerBsasRequestBodySpec extends UnitSpec {

  "TriggerBsasRequestBody" when {
    "read from valid JSON" should {
      "return the expected TriggerBsasRequestBody object" in {
        seRequestBodyMtd.validate[TriggerBsasRequestBody] shouldBe JsSuccess(seBody)
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidJson.validate[TriggerBsasRequestBody] shouldBe a[JsError]
      }
    }

    "written to JSON (self-employment)" should {
      "return the expected JsValue" in {
        Json.toJson(seBody) shouldBe seRequestBodyDes
      }
    }

    "written to JSON (uk property)" should {
      "return the expected JsValue for FHL properties" in {
        Json.toJson(fhlBody) shouldBe fhlRequestBodyDes
      }
      "return the expected JsValue for non-FHL properties" in {
        Json.toJson(nonFhlBody) shouldBe nonFhlRequestBodyDes
      }
    }
  }
}
