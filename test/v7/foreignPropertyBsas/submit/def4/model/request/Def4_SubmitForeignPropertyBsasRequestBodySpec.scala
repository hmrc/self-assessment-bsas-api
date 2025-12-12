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

package v7.foreignPropertyBsas.submit.def4.model.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec
import v7.common.model.PropertyId
import SubmitForeignPropertyBsasFixtures.*

class Def4_SubmitForeignPropertyBsasRequestBodySpec extends UnitSpec {

  private val parsedEmptyRequestBody = Def4_SubmitForeignPropertyBsasRequestBody(None)

  private val parsedForeignPropertyRequestBody =
    Def4_SubmitForeignPropertyBsasRequestBody(
      foreignProperty = Some(
        ForeignProperty(
          propertyLevelDetail =
            Some(Seq(PropertyLevelDetail(propertyId = PropertyId("717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"), income = None, expenses = None))),
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
            |        "propertyLevelDetail": [
            |            {
            |                "propertyId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"
            |            }
            |        ]
            |    }
            |}
          """.stripMargin
          )
          .as[Def4_SubmitForeignPropertyBsasRequestBody] shouldBe parsedForeignPropertyRequestBody
      }
    }

    "given a full foreign property body" should {
      "return the expected foreign Property data object" in {
        mtdRequestForeignPropertyFull.as[Def4_SubmitForeignPropertyBsasRequestBody] shouldBe requestForeignPropertyFull
      }
    }

    "given an empty body" should {
      "return an empty data object" in {
        JsObject.empty.as[Def4_SubmitForeignPropertyBsasRequestBody] shouldBe parsedEmptyRequestBody
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
            |            "propertyId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4"
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
