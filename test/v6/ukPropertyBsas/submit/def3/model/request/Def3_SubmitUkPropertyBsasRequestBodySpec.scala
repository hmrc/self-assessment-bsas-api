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

package v6.ukPropertyBsas.submit.def3.model.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec
import SubmitUKPropertyBsasRequestBodyFixtures._

class Def3_SubmitUkPropertyBsasRequestBodySpec extends UnitSpec {

  private val emptyParsedRequestBody = Def3_SubmitUkPropertyBsasRequestBody(None)

  private val fhlParsedRequestBody = Def3_SubmitUkPropertyBsasRequestBody(furnishedHolidayLet = Some(FurnishedHolidayLet(None, None)))

  "reads" when {

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
          .as[Def3_SubmitUkPropertyBsasRequestBody] shouldBe fhlParsedRequestBody
      }
    }

    "reading a full fhl body" should {
      "return the expected fhl model" in {
        mtdRequestFhlFull.as[Def3_SubmitUkPropertyBsasRequestBody] shouldBe requestFhlFull
      }
    }

    "reading an empty body" should {
      "return an empty model" in {
        JsObject.empty.as[Def3_SubmitUkPropertyBsasRequestBody] shouldBe emptyParsedRequestBody
      }
    }
  }

  "writes" when {
    "writing a simple fhl model" should {
      "return the downstream JSON" in {
        Json.toJson(fhlParsedRequestBody) shouldBe Json.parse(
          """
            |{
            |  "incomeSourceType": "04",
            |  "adjustments":{}
            |}
            |""".stripMargin
        )
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
