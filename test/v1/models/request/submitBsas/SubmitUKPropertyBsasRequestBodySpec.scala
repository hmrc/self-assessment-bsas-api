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

import play.api.libs.json.JsSuccess
import v1.fixtures.SubmitUKPropertyBsasRequestBodyFixtures._
import support.UnitSpec

class SubmitUKPropertyBsasRequestBodySpec extends UnitSpec{

  "SubmitUKPropertyBsasRequestBody" when {
    "read from valid JSON" should {
      "return the expected SubmitUKPropertyRequestBody" in {
        nonFHLRequestBodyMtd.validate[SubmitUKPropertyBsasRequestBody] shouldBe JsSuccess(nonFHLBody)
      }
    }
  }
}
