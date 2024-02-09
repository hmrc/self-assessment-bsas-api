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

package v3.models.request.submitBsas.foreignProperty

import play.api.libs.json.{JsObject, Json}
import shared.UnitSpec
import v3.fixtures.foreignProperty.SubmitForeignPropertyBsasFixtures._

class SubmitForeignPropertyBsasRequestBodySpec extends UnitSpec {

  val emptyModel: SubmitForeignPropertyBsasRequestBody = SubmitForeignPropertyBsasRequestBody(None, None)

  val nonFhlModel: SubmitForeignPropertyBsasRequestBody = SubmitForeignPropertyBsasRequestBody(Some(Seq(ForeignProperty("FRA", None, None))), None)

  val fhlModel: SubmitForeignPropertyBsasRequestBody = SubmitForeignPropertyBsasRequestBody(None, Some(FhlEea(None, None)))

  "reads" when {
    "reading a simple non-fhl body" should {
      "return the expected non-fhl model" in {
        Json
          .parse("""
            |{
            |  "nonFurnishedHolidayLet": [
            |    {
            |      "countryCode": "FRA"
            |    }
            |  ]
            |}
            |""".stripMargin)
          .as[SubmitForeignPropertyBsasRequestBody] shouldBe nonFhlModel
      }
    }

    "reading a simple fhl body" should {
      "return the expected fhl model" in {
        Json
          .parse(
            """
            |{
            |  "foreignFhlEea": {
            |  }
            |}
            |""".stripMargin
          )
          .as[SubmitForeignPropertyBsasRequestBody] shouldBe fhlModel
      }
    }

    "reading a full non-fhl body" should {
      "return the expected non-fhl model" in {
        mtdRequestNonFhlFull.as[SubmitForeignPropertyBsasRequestBody] shouldBe requestNonFhlFullModel
      }
    }

    "reading a full fhl body" should {
      "return the expected fhl model" in {
        mtdRequestFhlFull.as[SubmitForeignPropertyBsasRequestBody] shouldBe requestFhlFullModel
      }
    }

    "reading an empty body" should {
      "return an empty model" in {
        JsObject.empty.as[SubmitForeignPropertyBsasRequestBody] shouldBe emptyModel
      }
    }
  }

  "writes" when {
    "writing a simple non-fhl model" should {
      "return the downstream JSON" in {
        Json.toJson(nonFhlModel) shouldBe Json.parse("""
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

    "writing a simple fhl model" should {
      "return the downstream JSON" in {
        Json.toJson(fhlModel) shouldBe Json.parse(
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
