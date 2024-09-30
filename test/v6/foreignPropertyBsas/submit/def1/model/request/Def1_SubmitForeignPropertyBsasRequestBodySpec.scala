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

package v6.foreignPropertyBsas.submit.def1.model.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec
import v6.foreignPropertyBsas.submit.def1.model.request.SubmitForeignPropertyBsasFixtures._

class Def1_SubmitForeignPropertyBsasRequestBodySpec extends UnitSpec {

  private val parsedEmptyRequestBody = Def1_SubmitForeignPropertyBsasRequestBody(None, None)

  private val parsedRequestBody =
    Def1_SubmitForeignPropertyBsasRequestBody(Some(List(ForeignProperty("FRA", None, None))), None)

  private val parsedFhlRequestBody = Def1_SubmitForeignPropertyBsasRequestBody(None, Some(FhlEea(None, None)))

  "reads" when {
    "given a simple non-fhl body" should {
      "return the expected non-fhl data object" in {
        Json
          .parse("""
            |{
            |  "foreignProperty": [
            |    {
            |      "countryCode": "FRA"
            |    }
            |  ]
            |}
            |""".stripMargin)
          .as[Def1_SubmitForeignPropertyBsasRequestBody] shouldBe parsedRequestBody
      }
    }

    "given a simple fhl body" should {
      "return the expected fhl data object" in {
        Json
          .parse(
            """
            |{
            |  "foreignFhlEea": {
            |  }
            |}
            |""".stripMargin
          )
          .as[Def1_SubmitForeignPropertyBsasRequestBody] shouldBe parsedFhlRequestBody
      }
    }

    "given a full non-fhl body" should {
      "return the expected non-fhl data object" in {
        mtdRequestFull.as[Def1_SubmitForeignPropertyBsasRequestBody] shouldBe requestFull
      }
    }

    "given a full fhl body" should {
      "return the expected fhl data object" in {
        mtdRequestFhlFull.as[Def1_SubmitForeignPropertyBsasRequestBody] shouldBe requestFhlFull
      }
    }

    "given an empty body" should {
      "return an empty data object" in {
        JsObject.empty.as[Def1_SubmitForeignPropertyBsasRequestBody] shouldBe parsedEmptyRequestBody
      }
    }
  }

  "writes" when {
    "given a simple non-fhl data object" should {
      "return the downstream JSON" in {
        Json.toJson(parsedRequestBody) shouldBe Json.parse("""
            |{
            |  "incomeSourceType": "15",
            |  "adjustments": [
            |    {
            |      "countryCode": "FRA"
            |    }
            |  ]
            |}
            |""".stripMargin)
      }
    }

    "given a simple fhl data object" should {
      "return the downstream JSON" in {
        Json.toJson(parsedFhlRequestBody) shouldBe Json.parse(
          """
            |{
            |  "incomeSourceType": "03",
            |  "adjustments": {
            |  }
            |}
            |""".stripMargin
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
