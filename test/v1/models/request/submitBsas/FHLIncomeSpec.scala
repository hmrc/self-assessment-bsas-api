/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.request.submitBsas

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import support.UnitSpec

class FHLIncomeSpec extends UnitSpec {


  val inputJson: JsValue = Json.parse(
    """
      |{
      | "totalRentsReceived": 1000.45
      |}
      |""".stripMargin)

  val requestJson: JsValue = Json.parse(
    """
      |{
      | "totalRentsReceived": 1000.45
      |}
      |""".stripMargin)

  val invalidJson: JsValue = Json.parse(
    """
      |{
      |"rentIncome": true
      |}
      |""".stripMargin
  )

  val modelWithNoneValues: FHLIncome = FHLIncome(None)
  val model: FHLIncome = FHLIncome(Some(1000.45))

  "NonFHLIncome" when {
    "read from valid JSON" should {
      "return the expected NonFHLIncome object" in {
        inputJson.validate[FHLIncome] shouldBe JsSuccess(modelWithNoneValues)
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidJson.validate[FHLIncome] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(model) shouldBe requestJson
      }
    }
  }
}
