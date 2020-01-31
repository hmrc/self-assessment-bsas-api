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

package v1.models.response.retrieveBsas.ukProperty

import play.api.libs.json.Json
import support.UnitSpec
import v1.models.utils.JsonErrorValidators

class FhlIncomeBreakdownSpec extends UnitSpec with JsonErrorValidators{

  val mtdJson = Json.parse(
    """{
      |  "rentIncome": 100.49,
      |  "premiumsOfLeaseGrant": 100.49,
      |  "reversePremiums": 100.49,
      |  "otherPropertyIncome": 100.49,
      |  "rarRentReceived": 100.49
      |}""".stripMargin)

  val desJsonforFhl = Json.parse(
    """{
      |  "rentReceived": 100.49,
      |  "premiumsOfLeaseGrant": 100.49,
      |  "reversePremiums": 100.49,
      |  "otherPropertyIncome": 100.49,
      |  "rarRentReceived": 100.49
      |}""".stripMargin)

  val model = IncomeBreakdown(Some(100.49),Some(100.49),Some(100.49),Some(100.49), Some(100.49))

  "reads" should {
    "return a valid model" when {

      testPropertyType[IncomeBreakdown](desJsonforFhl)(
        path = "/rentReceived",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )(IncomeBreakdown.fhlReads)

      testPropertyType[IncomeBreakdown](desJsonforFhl)(
        path = "/premiumsOfLeaseGrant",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )(IncomeBreakdown.fhlReads)

      testPropertyType[IncomeBreakdown](desJsonforFhl)(
        path = "/reversePremiums",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )(IncomeBreakdown.fhlReads)

      testPropertyType[IncomeBreakdown](desJsonforFhl)(
        path = "/otherPropertyIncome",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )(IncomeBreakdown.fhlReads)

      testPropertyType[IncomeBreakdown](desJsonforFhl)(
        path = "/rarRentReceived",
        replacement = "test".toJson,
        expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
      )(IncomeBreakdown.fhlReads)

      "a valid Fhl json with all fields are supplied" in {
        desJsonforFhl.as[IncomeBreakdown](IncomeBreakdown.fhlReads) shouldBe model
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid model is supplied" in {
        model.toJson shouldBe mtdJson
      }
    }
  }
}
