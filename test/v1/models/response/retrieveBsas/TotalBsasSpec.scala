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

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.fixtures.ukProperty.RetrieveUkPropertyBsasFixtures._
import v1.models.utils.JsonErrorValidators

class TotalBsasSpec extends UnitSpec with JsonErrorValidators{

  val mtdJson: JsValue = Json.parse(
    """{
      |  "income": 100.49,
      |  "expenses": 100.49,
      |  "additions": 100.49,
      |  "deductions": 100.49
      |}""".stripMargin)

  val desJson: JsValue = Json.parse(
    """{
      |  "totalIncome": 100.49,
      |  "totalExpenses": 100.49,
      |  "totalAdditions": 100.49,
      |  "totalDeductions": 100.49
      |}""".stripMargin)

  val desWithOnlyRequiredJson: JsValue = Json.parse(
    """{
      |  "totalIncome": 100.49
      |}""".stripMargin)

  "reads" should {
    "return a valid model" when {

      testMandatoryProperty[TotalBsas](desJson)("totalIncome")

      testPropertyType[TotalBsas](desJson)(
        path = "/totalExpenses",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      testPropertyType[TotalBsas](desJson)(
        path = "/totalAdditions",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      testPropertyType[TotalBsas](desJson)(
        path = "/totalDeductions",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )

      "a valid json with all fields are supplied" in {
        desJson.as[TotalBsas] shouldBe totalBsasModel
      }

      "a valid json with only mandatory fields are supplied" in {
        desWithOnlyRequiredJson.as[TotalBsas] shouldBe new TotalBsas(100.49, None, None, None)
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid model is supplied" in {
        totalBsasModel.toJson shouldBe mtdJson
      }
    }
  }
}
