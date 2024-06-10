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

package v5.bsas.trigger.model

import play.api.libs.json.{JsError, Json}
import shared.utils.UnitSpec
import v5.bsas.trigger.def1.model.response.Def1_TriggerBsasResponse

class TriggerBsasResponseSpec extends UnitSpec {

  val triggerBsasResponse: Def1_TriggerBsasResponse = Def1_TriggerBsasResponse("anId")

  "TriggerBsasResponse" when {
    "read from valid JSON" should {
      "return the expected TriggerBsasResponse object" in {
        Json
          .parse("""
            |{
            |   "metadata" : {
            |       "calculationId" : "anId"
            |   }
            |}
            |""".stripMargin)
          .as[Def1_TriggerBsasResponse] shouldBe triggerBsasResponse
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        Json
          .parse("""
            |{
            |   "calculationId" : 3
            |}
            |""".stripMargin)
          .validate[Def1_TriggerBsasResponse] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(triggerBsasResponse) shouldBe Json.parse("""
            |{
            |   "calculationId" : "anId"
            |}
            |""".stripMargin)
      }
    }
  }

}
