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
import v5.ukPropertyBsas.submit.def1.model.request.SubmitUKPropertyBsasRequestBodyFixtures.*

class Def1_SubmitUkPropertyBsasRequestBodySpec extends UnitSpec {

  private val emptyParsedRequestBody = Def1_SubmitUkPropertyBsasRequestBody(None, None)

  private val nonFhlParsedRequestBody = Def1_SubmitUkPropertyBsasRequestBody(Some(NonFurnishedHolidayLet(None, None)), None)

  private val fhlParsedRequestBody = Def1_SubmitUkPropertyBsasRequestBody(None, Some(FurnishedHolidayLet(None, None)))

  "reads" when {
    "reading a simple non-fhl body" should {
      "return the expected non-fhl model" in {
        Json
          .parse("""
            |{
            |  "nonFurnishedHolidayLet": {
            |  }
            |}
            |""".stripMargin)
          .as[Def1_SubmitUkPropertyBsasRequestBody] shouldBe nonFhlParsedRequestBody
      }
    }

    "reading a simple fhl body" should {
      "return the expected fhl model" in {
        Json
          .parse(
            """
              |{
              |  "furnishedHolidayLet": {
              |  }
              |}
              |""".stripMargin
          )
          .as[Def1_SubmitUkPropertyBsasRequestBody] shouldBe fhlParsedRequestBody
      }
    }

    "reading a full non-fhl body" should {
      "return the expected non-fhl model" in {
        mtdRequestNonFhlFull.as[Def1_SubmitUkPropertyBsasRequestBody] shouldBe requestNonFhlFull
      }
    }

    "reading a full fhl body" should {
      "return the expected fhl model" in {
        mtdRequestFhlFull.as[Def1_SubmitUkPropertyBsasRequestBody] shouldBe requestFhlFull
      }
    }

    "reading an empty body" should {
      "return an empty model" in {
        JsObject.empty.as[Def1_SubmitUkPropertyBsasRequestBody] shouldBe emptyParsedRequestBody
      }
    }
  }

  "writes" when {
    "writing a simple non-fhl model" should {
      "return the downstream JSON" in {
        Json.toJson(nonFhlParsedRequestBody) shouldBe Json.parse("""
            |{
            |  "incomeSourceType": "02",
            |  "adjustments": {
            |  }
            |}
            |""".stripMargin)
      }
    }

    "writing a simple fhl model" should {
      "return the downstream JSON" in {
        Json.toJson(fhlParsedRequestBody) shouldBe Json.parse(
          """
            |{
            |  "incomeSourceType": "04",
            |  "adjustments": {
            |  }
            |}
            |""".stripMargin
        )
      }
    }

    "writing a full non-fhl model" should {
      "return the downstream JSON" in {
        Json.toJson(requestNonFhlFull) shouldBe downstreamRequestNonFhlFull
      }
    }

    "writing a full fhl model" should {
      "return the downstream JSON" in {
        Json.toJson(requestFhlFull) shouldBe downstreamRequestFhlFull
      }
    }

    "passed an empty model" should {
      "return an empty JSON" in {
        Json.toJson(emptyParsedRequestBody) shouldBe JsObject.empty
      }
    }
  }

}
