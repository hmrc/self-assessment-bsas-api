/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.models.request.submitBsas.ukProperty

import play.api.libs.json.{JsObject, Json}
import support.UnitSpec
import v3.fixtures.ukProperty.SubmitUkPropertyBsasFixtures._

class SubmitUKPropertyBsasRequestBodySpec extends UnitSpec {

  val emptyModel: SubmitUKPropertyBsasRequestBody = SubmitUKPropertyBsasRequestBody(None, None)

  val nonFhlModel: SubmitUKPropertyBsasRequestBody = SubmitUKPropertyBsasRequestBody(Some(NonFurnishedHolidayLet(None, None)),None)

  val fhlModel: SubmitUKPropertyBsasRequestBody = SubmitUKPropertyBsasRequestBody(None, Some(FurnishedHolidayLet(None, None)))

  "reads" when {
    "reading a simple non-fhl body" should {
      "return the expected non-fhl model" in {
        Json.parse("""
            |{
            |  "nonFurnishedHolidayLet": {
            |  }
            |}
            |""".stripMargin).as[SubmitUKPropertyBsasRequestBody] shouldBe nonFhlModel
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
          .as[SubmitUKPropertyBsasRequestBody] shouldBe fhlModel
      }
    }

    "reading a full non-fhl body" should {
      "return the expected non-fhl model" in {
        mtdRequestNonFhlFull.as[SubmitUKPropertyBsasRequestBody] shouldBe requestNonFhlFullModel
      }
    }

    "reading a full fhl body" should {
      "return the expected fhl model" in {
        mtdRequestFhlFull.as[SubmitUKPropertyBsasRequestBody] shouldBe requestFhlFullModel
      }
    }

    "reading an empty body" should {
      "return an empty model" in {
        JsObject.empty.as[SubmitUKPropertyBsasRequestBody] shouldBe emptyModel
      }
    }
  }

  "writes" when {
    "writing a simple non-fhl model" should {
      "return the downstream JSON" in {
        Json.toJson(nonFhlModel) shouldBe Json.parse("""
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
        Json.toJson(fhlModel) shouldBe Json.parse(
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
        Json.toJson(requestNonFhlFullModel) shouldBe downstreamRequestNonFhlFull
      }
    }

    "writing a full fhl model" should {
      "return the downstream JSON" in {
        Json.toJson(requestFhlFullModel) shouldBe downstreamRequestFhlFull
      }
    }

    "passed an empty model" should {
      "return an empty JSON" in {
        Json.toJson(emptyModel) shouldBe JsObject.empty
      }
    }
  }
}
