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

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import support.UnitSpec
import v1.models.response.triggerBsas.TriggerBsasResponse

class TriggerBsasResponseSpec extends UnitSpec {

  val desJson: JsValue = Json.parse("""
      |{
      |   "metadata" : {
      |       "calculationId" : "anId"
      |   }
      |}
  """.stripMargin)

  val mtdJson: JsValue = Json.parse("""
      |{
      |   "id" : "anId"
      |}
  """.stripMargin)

  val invalidJson: JsValue = Json.parse("""
      |{
      |   "id" : 3
      |}
  """.stripMargin)

  val response: TriggerBsasResponse = TriggerBsasResponse("anId")

  "TriggerBsasResponse" when {
    "read from valid JSON" should {
      "return the expected TriggerBsasResponse object" in {
        desJson.as[TriggerBsasResponse] shouldBe response
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidJson.validate[TriggerBsasResponse] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(response) shouldBe mtdJson
      }
    }
  }
}
