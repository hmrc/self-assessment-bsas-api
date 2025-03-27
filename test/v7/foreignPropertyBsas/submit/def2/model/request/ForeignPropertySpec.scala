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

import play.api.libs.json.{JsObject, JsValue, Json}
import shared.utils.UnitSpec

class ForeignPropertySpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      |    "countryLevelDetail": [
      |        {
      |            "countryCode": "FRA",
      |            "income": {},
      |            "expenses": {}
      |        }
      |    ]
      |}
    """.stripMargin
  )

  val emptyJson: JsValue = JsObject.empty

  val model: ForeignProperty = ForeignProperty(
    countryLevelDetail = Some(
      Seq(
        CountryLevelDetail(
          countryCode = "FRA",
          income = Some(ForeignPropertyIncome(None, None, None)),
          expenses = Some(ForeignPropertyExpenses(None, None, None, None, None, None, None, None, None))
        )
      )
    ),
    zeroAdjustments = None
  )

  val emptyModel: ForeignProperty = ForeignProperty(None, None)

  "reads" when {
    "passed mtd json" should {
      "return the corresponding model" in {
        json.as[ForeignProperty] shouldBe model
      }
    }

    "passed an empty JSON" should {
      "return an empty model" in {
        emptyJson.as[ForeignProperty] shouldBe emptyModel
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
        Json.toJson(emptyModel) shouldBe emptyJson
      }
    }
  }

}
