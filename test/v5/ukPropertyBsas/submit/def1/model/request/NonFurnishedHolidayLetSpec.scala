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

package v5.ukPropertyBsas.submit.def1.model.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec

class NonFurnishedHolidayLetSpec extends UnitSpec {

  // Use simple case as formats for contents of income/expenses are tested elsewhere...
  private val json = Json.parse("""
      |{
      |  "income": {
      |  },
      |  "expenses": {
      |  }
      |}
      |""".stripMargin)

  private val nonFurnishedHolidayLet = NonFurnishedHolidayLet(
    Some(NonFHLIncome(None, None, None, None)),
    Some(NonFHLExpenses(None, None, None, None, None, None, None, None, None))
  )

  private val emptyNonFurnishedHolidayLet = NonFurnishedHolidayLet(None, None)

  "reads" when {
    "given MTD json" should {
      "return the corresponding NonFurnishedHolidayLet" in {
        json.as[NonFurnishedHolidayLet] shouldBe nonFurnishedHolidayLet
      }
    }

    "given an empty JSON object" should {
      "return an empty NonFurnishedHolidayLet" in {
        JsObject.empty.as[NonFurnishedHolidayLet] shouldBe emptyNonFurnishedHolidayLet
      }
    }
  }

  "writes" when {
    "given a NonFurnishedHolidayLet" should {
      "return the downstream JSON" in {
        Json.toJson(nonFurnishedHolidayLet) shouldBe json
      }
    }

    "given an empty NonFurnishedHolidayLet" should {
      "return an empty JSON object" in {
        Json.toJson(emptyNonFurnishedHolidayLet) shouldBe JsObject.empty
      }
    }
  }

}
