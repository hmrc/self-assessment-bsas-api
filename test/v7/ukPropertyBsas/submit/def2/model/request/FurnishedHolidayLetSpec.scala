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

package v7.ukPropertyBsas.submit.def2.model.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec

class FurnishedHolidayLetSpec extends UnitSpec {

  // Use simple case as formats for contents of income/expenses are tested elsewhere...
  private val json = Json.parse("""
      |{
      |  "income": {
      |  },
      |  "expenses": {
      |  }
      |}
      |""".stripMargin)

  private val furnishedHolidayLet: FurnishedHolidayLet = FurnishedHolidayLet(
    Some(FHLIncome(None)),
    Some(FHLExpenses(None, None, None, None, None, None, None, None)),
    None
  )

  private val emptyFurnishedHolidayLet: FurnishedHolidayLet = FurnishedHolidayLet(None, None, None)

  "reads" when {
    "given MTD json" should {
      "return the expected FurnishedHolidayLet" in {
        json.as[FurnishedHolidayLet] shouldBe furnishedHolidayLet
      }
    }

    "given an empty JSON object" should {
      "return an empty FurnishedHolidayLet" in {
        JsObject.empty.as[FurnishedHolidayLet] shouldBe emptyFurnishedHolidayLet
      }
    }
  }

  "writes" when {
    "given a FurnishedHolidayLet" should {
      "return the downstream JSON" in {
        Json.toJson(furnishedHolidayLet) shouldBe json
      }
    }

    "given an empty FurnishedHolidayLet" should {
      "return an empty JSON object" in {
        Json.toJson(emptyFurnishedHolidayLet) shouldBe JsObject.empty
      }
    }
  }

}
