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

package v7.foreignPropertyBsas.submit.def2.model.request

import play.api.libs.json.{JsValue, Json}
import shared.utils.UnitSpec

class CountryLevelDetailSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      |    "countryCode": "FRA",
      |    "income": {},
      |    "expenses": {}
      |}
    """.stripMargin
  )

  val minimalJson: JsValue = Json.parse(
    """
      |{
      |    "countryCode": "FRA"
      |}
    """.stripMargin
  )

  val model: CountryLevelDetail = CountryLevelDetail(
    countryCode = "FRA",
    income = Some(ForeignPropertyIncome(None, None, None)),
    expenses = Some(ForeignPropertyExpenses(None, None, None, None, None, None, None, None, None))
  )

  val minimalModel: CountryLevelDetail = CountryLevelDetail("FRA", None, None)

  "reads" when {
    "passed mtd json" should {
      "return the corresponding model" in {
        json.as[CountryLevelDetail] shouldBe model
      }
    }

    "passed mtd JSON with only mandatory fields" should {
      "return the corresponding model" in {
        minimalJson.as[CountryLevelDetail] shouldBe minimalModel
      }
    }
  }

  "writes" when {
    "passed a model" should {
      "return the corresponding downstream JSON" in {
        Json.toJson(model) shouldBe json
      }
    }

    "passed a model with only mandatory fields" should {
      "return the corresponding downstream JSON" in {
        Json.toJson(minimalModel) shouldBe minimalJson
      }
    }
  }

}
