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

package v7.foreignPropertyBsas.submit.def3.model.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec
import v7.foreignPropertyBsas.submit.def3.model.request.SubmitForeignPropertyBsasFixtures._

class Def3_SubmitForeignPropertyBsasRequestBodySpec extends UnitSpec {

  private val parsedEmptyRequestBody = Def3_SubmitForeignPropertyBsasRequestBody(None)

  private val parsedForeignPropertyRequestBody =
    Def3_SubmitForeignPropertyBsasRequestBody(
      foreignProperty = Some(
        ForeignProperty(
          countryLevelDetail = Some(Seq(CountryLevelDetail(countryCode = "FRA", income = None, expenses = None))),
          zeroAdjustments = None
        )
      )
    )

  "reads" when {
    "given a simple foreign Property body" should {
      "return the expected foreign Property data object" in {
        Json
          .parse(
            """
            |{
            |    "foreignProperty": {
            |        "countryLevelDetail": [
            |            {
            |                "countryCode": "FRA"
            |            }
            |        ]
            |    }
            |}
          """.stripMargin
          )
          .as[Def3_SubmitForeignPropertyBsasRequestBody] shouldBe parsedForeignPropertyRequestBody
      }
    }

    "given a full foreign property body" should {
      "return the expected foreign Property data object" in {
        mtdRequestForeignPropertyFull.as[Def3_SubmitForeignPropertyBsasRequestBody] shouldBe requestForeignPropertyFull
      }
    }

    "given an empty body" should {
      "return an empty data object" in {
        JsObject.empty.as[Def3_SubmitForeignPropertyBsasRequestBody] shouldBe parsedEmptyRequestBody
      }
    }
  }

  "writes" when {
    "given a simple non-fhl data object" should {
      "return the downstream JSON" in {
        Json.toJson(parsedForeignPropertyRequestBody) shouldBe Json.parse(
          """
            |{
            |    "incomeSourceType": "15",
            |    "adjustments": [
            |        {
            |            "countryCode": "FRA"
            |        }
            |    ]
            |}
          """.stripMargin
        )
      }
    }

    "given a full non-fhl data object" should {
      "return the downstream JSON" in {
        Json.toJson(requestForeignPropertyFull) shouldBe downstreamRequestForeignPropertyFull
      }
    }

    "given an empty data object" should {
      "return an empty JSON object" in {
        Json.toJson(parsedEmptyRequestBody) shouldBe JsObject.empty
      }
    }
  }

}
