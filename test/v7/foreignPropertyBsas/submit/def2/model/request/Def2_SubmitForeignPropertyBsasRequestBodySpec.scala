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

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec
import v7.foreignPropertyBsas.submit.def2.model.request.SubmitForeignPropertyBsasFixtures.*

class Def2_SubmitForeignPropertyBsasRequestBodySpec extends UnitSpec {

  private val parsedEmptyRequestBody = Def2_SubmitForeignPropertyBsasRequestBody(None, None)

  private val parsedRequestBody =
    Def2_SubmitForeignPropertyBsasRequestBody(
      foreignProperty = Some(
        ForeignProperty(
          countryLevelDetail = Some(Seq(CountryLevelDetail(countryCode = "FRA", income = None, expenses = None))),
          zeroAdjustments = None
        )
      ),
      foreignFhlEea = None
    )

  private val parsedFhlRequestBody = Def2_SubmitForeignPropertyBsasRequestBody(
    foreignProperty = None,
    foreignFhlEea = Some(FhlEea(None, None, None))
  )

  "reads" when {
    "given a simple non-fhl body" should {
      "return the expected non-fhl data object" in {
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
          .as[Def2_SubmitForeignPropertyBsasRequestBody] shouldBe parsedRequestBody
      }
    }

    "given a simple fhl body" should {
      "return the expected fhl data object" in {
        Json
          .parse(
            """
            |{
            |    "foreignFhlEea": {}
            |}
          """.stripMargin
          )
          .as[Def2_SubmitForeignPropertyBsasRequestBody] shouldBe parsedFhlRequestBody
      }
    }

    "given a full non-fhl body" should {
      "return the expected non-fhl data object" in {
        mtdRequestFull.as[Def2_SubmitForeignPropertyBsasRequestBody] shouldBe requestFull
      }
    }

    "given a full fhl body" should {
      "return the expected fhl data object" in {
        mtdRequestFhlFull.as[Def2_SubmitForeignPropertyBsasRequestBody] shouldBe requestFhlFull
      }
    }

    "given an empty body" should {
      "return an empty data object" in {
        JsObject.empty.as[Def2_SubmitForeignPropertyBsasRequestBody] shouldBe parsedEmptyRequestBody
      }
    }
  }

  "writes" when {
    "given a simple non-fhl data object" should {
      "return the downstream JSON" in {
        Json.toJson(parsedRequestBody) shouldBe Json.parse(
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

    "given a simple fhl data object" should {
      "return the downstream JSON" in {
        Json.toJson(parsedFhlRequestBody) shouldBe Json.parse(
          """
            |{
            |    "incomeSourceType": "03",
            |    "adjustments": {}
            |}
          """.stripMargin
        )
      }
    }

    "given a full non-fhl data object" should {
      "return the downstream JSON" in {
        Json.toJson(requestFull) shouldBe downstreamRequestFull
      }
    }

    "given a full fhl data object" should {
      "return the downstream JSON" in {
        Json.toJson(requestFhlFull) shouldBe downstreamRequestFhlFull
      }
    }

    "given an empty data object" should {
      "return an empty JSON object" in {
        Json.toJson(parsedEmptyRequestBody) shouldBe JsObject.empty
      }
    }
  }

}
