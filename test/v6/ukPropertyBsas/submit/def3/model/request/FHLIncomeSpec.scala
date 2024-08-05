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

package v6.ukPropertyBsas.submit.def3.model.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec
import v6.ukPropertyBsas.submit.def3.model.request

class FHLIncomeSpec extends UnitSpec {

  private val fhlIncome: request.FHLIncome = FHLIncome(Some(123.12))

  private val emptyFhlIncome: request.FHLIncome = FHLIncome(None)

  "reads" when {
    "given MTD json" should {
      "return the expected parsed object" in {
        Json
          .parse("""
              |{
              |   "totalRentsReceived": 123.12
              |}
              |""".stripMargin)
          .as[request.FHLIncome] shouldBe fhlIncome
      }
    }

    "given an empty JSON object" should {
      "return an empty FHLIncome" in {
        JsObject.empty.as[request.FHLIncome] shouldBe emptyFhlIncome
      }
    }
  }

  "writes" when {
    "given an FHLIncome object" should {
      "return the downstream JSON" in {
        Json.toJson(fhlIncome) shouldBe
          Json.parse("""
              |{
              |   "rentReceived": 123.12
              |}
              |""".stripMargin)
      }
    }

    "given an empty FHLIncome" should {
      "return an empty JSON object" in {
        Json.toJson(emptyFhlIncome) shouldBe JsObject.empty
      }
    }
  }

}
