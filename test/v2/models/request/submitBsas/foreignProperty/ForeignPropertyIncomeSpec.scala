/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.models.request.submitBsas.foreignProperty

import play.api.libs.json.Json
import support.UnitSpec

class ForeignPropertyIncomeSpec extends UnitSpec {

  val validReadJson = Json.parse(
    """
      |{
      |   "rentIncome": 123.12,
      |   "premiumsOfLeaseGrant": 123.12,
      |   "otherPropertyIncome": 123.12
      |}
      |""".stripMargin)

  val validWriteJson = Json.parse(
    """
      |{
      |   "rent": 123.12,
      |   "premiumsOfLeaseGrant": 123.12,
      |   "otherPropertyIncome": 123.12
      |}
      |""".stripMargin)

  val emptyJson = Json.parse("""{}""")

  val validModel = ForeignPropertyIncome(Some(123.12), Some(123.12), Some(123.12))

  val emptyModel = ForeignPropertyIncome(None, None, None)


  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        validReadJson.as[ForeignPropertyIncome] shouldBe validModel
      }
    }
  }
  "reads from an empty JSON" when{
    "passed an empty JSON" should {
      "return an empty model" in {
        emptyJson.as[ForeignPropertyIncome] shouldBe emptyModel
      }
    }
  }
  "writes" when {
    "passed valid model" should {
      "return valid JSON" in {
        Json.toJson(validModel) shouldBe validWriteJson
      }
    }
  }
  "write from an empty body" when {
    "passed an empty model" should {
      "return an empty JSON" in {
        Json.toJson(emptyModel) shouldBe emptyJson
      }
    }
  }

  "isEmpty" when {
    "passed a non empty model" should {
      "return false" in {
        validModel.isEmpty shouldBe false
      }
    }
    "passed an empty model" should {
      "return true" in {
        emptyModel.isEmpty shouldBe true
      }
    }
  }
}