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

package v1.models.response.retrieveBsas

import play.api.libs.json.Json
import support.UnitSpec
import v1.models.utils.JsonErrorValidators
import v1.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures._

class ProfitSpec extends UnitSpec with JsonErrorValidators{

  val mtdJson = Json.parse(
    """{
      |  "net": 100.49,
      |  "taxable": 100.49
      |}""".stripMargin)

  val desJson = Json.parse(
    """{
      |  "netProfit": 100.49,
      |  "taxableProfit": 100.49
      |}""".stripMargin)

  "reads" should {
    "return a valid model" when {

      testPropertyType[Profit](desJson)(
        path = "/netProfit",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      testPropertyType[Profit](desJson)(
        path = "/taxableProfit",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      "a valid json with all fields are supplied" in {
        desJson.as[Profit] shouldBe profitModel
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid model is supplied" in {
        profitModel.toJson shouldBe mtdJson
      }
    }
  }
}
