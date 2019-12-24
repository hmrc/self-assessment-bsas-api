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

package v1.models.request.submitBsas

import play.api.libs.json.{JsError, JsSuccess, Json}
import v1.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._
import support.UnitSpec

class SubmitUKPropertyBsasRequestBodySpec extends UnitSpec{

  "SubmitUKPropertyBsasRequestBody" when {
    "read from valid JSON" should {
      "return the expected SubmitUKPropertyRequestBody with a nonFHL" in {
        nonFHLInputJson.validate[SubmitUKPropertyBsasRequestBody] shouldBe JsSuccess(nonFHLBody)
      }

      "return the expected SubmitUKPropertyRequestBody with a FHL" in {
        fhlInputJson.validate[SubmitUKPropertyBsasRequestBody] shouldBe JsSuccess(fhlBody)
      }
    }

    "read from invalid JSON" should {
      "return the expected SubmitUKPropertyRequestBody with a nonFHL" in {
        nonFHLInvalidJson.validate[SubmitUKPropertyBsasRequestBody] shouldBe a[JsError]
      }

      "return the expected SubmitUKPropertyRequestBody with a FHL" in {
        fhlInvalidJson.validate[SubmitUKPropertyBsasRequestBody] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected SubmitUKPropertyRequestBody with a nonFHL" in {
        Json.toJson(nonFHLBody) shouldBe nonFHLRequestJson
      }

      "return the expected SubmitUKPropertyRequestBody with a FHL" in {
        Json.toJson(fhlBody) shouldBe fhlRequestJson
      }
    }
  }
}
