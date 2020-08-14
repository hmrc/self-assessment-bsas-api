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

package v2.models.request.submitForeignProperty

import play.api.libs.json.Json
import support.UnitSpec

class IncomeSpec extends UnitSpec {

  val validJson = Json.parse(
    """
      |{
      |   "rentIncome": 123.12,
      |   "premiumsOfLeaseGrant": 123.12,
      |   "foreignTaxTakenOff": 123.12,
      |   "otherPropertyIncome": 123.12
      |}
      |""".stripMargin)

  val emptyJson = Json.parse("""{}""")

  val validModel = Income(Some(123.12),Some(123.12),Some(123.12),Some(123.12))

  val emptyModel = Income(None,None,None,None)


  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        validModel shouldBe validJson.as[Income]
      }
    }
  }
  "reads from an empty JSON" when{
    "passed an empty JSON" should {
      "return an empty model" in {
        emptyModel shouldBe emptyJson.as[Income]
      }
    }
  }
  "writes" when {
    "passed valid model" should {
      "return valid JSON" in {
        Json.toJson(validModel) shouldBe validJson
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
}