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

package v1.models.response

import play.api.libs.json.{JsError, JsValue, Json}
import support.UnitSpec

class SubmitBsasResponseSpec extends UnitSpec{

  val desJson: JsValue = Json.parse(
    """
      |{
      |   "metadata" : {
      |       "calculationId" : "anId"
      |   }
      |}
  """.stripMargin)

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |   "id" : "anId"
      |}
  """.stripMargin)

  val invalidDesJson: JsValue = Json.parse(
    """
      |{
      |   "id" : 3
      |}
  """.stripMargin)

  val submitBsasResponse: SubmitBsasResponse = SubmitBsasResponse("anId")

  "SubmitBsasResponse" when {
    "read from valid JSON" should {
      "return the expected SubmitBsasResponse object" in {
        desJson.as[SubmitBsasResponse] shouldBe submitBsasResponse
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidDesJson.validate[SubmitBsasResponse] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(submitBsasResponse) shouldBe mtdJson
      }
    }
  }
}
