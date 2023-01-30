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

package v3.models.request.submitBsas.foreignProperty

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class ForeignPropertySpec extends UnitSpec {

  // Use simple case as formats for contents of income/expenses are tested elsewhere...
  val json: JsValue = Json.parse("""
      |{
      |  "countryCode": "FRA",
      |  "income": {
      |  },
      |  "expenses": {
      |  }
      |}
      |""".stripMargin)

  val minimalJson: JsValue = Json.parse("""{
      |  "countryCode": "FRA"
      |}""".stripMargin)

  val model: ForeignProperty = ForeignProperty(
    "FRA",
    Some(ForeignPropertyIncome(None, None, None)),
    Some(ForeignPropertyExpenses(None, None, None, None, None, None, None, None, None))
  )

  val minimalModel: ForeignProperty = ForeignProperty("FRA", None, None)

  "reads" when {
    "passed mtd json" should {
      "return the corresponding model" in {
        json.as[ForeignProperty] shouldBe model
      }
    }

    "passed an empty JSON" should {
      "return an empty model" in {
        minimalJson.as[ForeignProperty] shouldBe minimalModel
      }
    }
  }

  "writes" when {
    "passed a model" should {
      "return the downstream JSON" in {
        Json.toJson(model) shouldBe json
      }
    }

    "passed an empty model" should {
      "return an empty JSON" in {
        Json.toJson(minimalModel) shouldBe minimalJson
      }
    }
  }
}
