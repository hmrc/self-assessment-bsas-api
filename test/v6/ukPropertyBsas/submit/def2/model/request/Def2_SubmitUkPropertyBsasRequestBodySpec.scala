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

package v6.ukPropertyBsas.submit.def2.model.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec
import v6.ukPropertyBsas.submit.def2.model.request.SubmitUKPropertyBsasRequestBodyFixtures._

class Def2_SubmitUkPropertyBsasRequestBodySpec extends UnitSpec {

  private val emptyParsedRequestBody = Def2_SubmitUkPropertyBsasRequestBody(None, None)

  private val ukPropertyParsedRequestBody = Def2_SubmitUkPropertyBsasRequestBody(Some(UkProperty(None, None)), None)

  private val fhlParsedRequestBody = Def2_SubmitUkPropertyBsasRequestBody(None, Some(FurnishedHolidayLet(None, None)))

  "reads" when {
    "reading a simple ukProperty body" should {
      "return the expected ukProperty model" in {
        Json
          .parse("""
            |{
            |  "ukProperty": {
            |  }
            |}
            |""".stripMargin)
          .as[Def2_SubmitUkPropertyBsasRequestBody] shouldBe ukPropertyParsedRequestBody
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
          .as[Def2_SubmitUkPropertyBsasRequestBody] shouldBe fhlParsedRequestBody
      }
    }

    "reading a full ukProperty body" should {
      "return the expected ukProperty model" in {
        mtdRequestUkPropertyFull.as[Def2_SubmitUkPropertyBsasRequestBody] shouldBe requestUkPropertyFull
      }
    }

    "reading a full fhl body" should {
      "return the expected fhl model" in {
        mtdRequestFhlFull.as[Def2_SubmitUkPropertyBsasRequestBody] shouldBe requestFhlFull
      }
    }

    "reading an empty body" should {
      "return an empty model" in {
        JsObject.empty.as[Def2_SubmitUkPropertyBsasRequestBody] shouldBe emptyParsedRequestBody
      }
    }
  }

  "writes" when {
    "writing a simple ukProperty model" should {
      "return the downstream JSON" in {
        Json.toJson(ukPropertyParsedRequestBody) shouldBe Json.parse("""
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

    "writing a full ukProperty model" should {
      "return the downstream JSON" in {
        Json.toJson(requestUkPropertyFull) shouldBe downstreamRequestNonFhlFull
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
