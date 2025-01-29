/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.bsas.trigger.model

import play.api.libs.json.{JsError, Json}
import shared.utils.UnitSpec
import v7.bsas.trigger.def1.model.response.Def1_TriggerBsasResponse
import v7.bsas.trigger.def2.model.response.Def2_TriggerBsasResponse

class TriggerBsasResponseSpec extends UnitSpec {

  val triggerBsasResponseDef1: Def1_TriggerBsasResponse = Def1_TriggerBsasResponse("anId")
  val triggerBsasResponseDef2: Def2_TriggerBsasResponse = Def2_TriggerBsasResponse("anId")

  "TriggerBsasResponseDef1" when {
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
          .as[Def1_TriggerBsasResponse] shouldBe triggerBsasResponseDef1
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
        Json.toJson(triggerBsasResponseDef1) shouldBe Json.parse("""
            |{
            |   "calculationId" : "anId"
            |}
            |""".stripMargin)
      }
    }
  }

  "TriggerBsasResponseDef2" when {
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
          .as[Def2_TriggerBsasResponse] shouldBe triggerBsasResponseDef2
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
          .validate[Def2_TriggerBsasResponse] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(triggerBsasResponseDef2) shouldBe Json.parse("""
            |{
            |   "calculationId" : "anId"
            |}
            |""".stripMargin)
      }
    }
  }

}
