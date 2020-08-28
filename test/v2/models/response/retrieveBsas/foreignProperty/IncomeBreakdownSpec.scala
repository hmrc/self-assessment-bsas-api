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

package v2.models.response.retrieveBsas.foreignProperty

import v2.fixtures.foreignProperty.RetrieveForeignPropertyBsasBodyFixtures._
import play.api.libs.json.Json
import support.UnitSpec
import v2.models.utils.JsonErrorValidators

class IncomeBreakdownSpec extends UnitSpec with JsonErrorValidators{

  val nonFhlMtdJson = Json.parse(
    """{
      |  "rentIncome": 100.49,
      |  "premiumsOfLeaseGrant": 100.49,
      |  "otherPropertyIncome": 100.49,
      |  "foreignTaxTakenOff": 100.49,
      |  "specialWithholdingTaxOrUKTaxPaid": 100.49
      |}""".stripMargin
  )

  val fhlMtdJson = Json.parse(
    """{
      |  "rentIncome": 100.49
      |}""".stripMargin
  )

  val nonFhlDesJson = Json.parse(
    """{
      |  "rentIncome": 100.49,
      |  "premiumsOfLeaseGrant": 100.49,
      |  "otherPropertyIncome": 100.49,
      |  "foreignTaxTakenOff": 100.49,
      |  "specialWithholdingTaxOrUKTaxPaid": 100.49
      |}""".stripMargin
  )

  val fhlDesJson = Json.parse(
    """{
      |  "rentIncome": 100.49
      |}""".stripMargin
  )

  "reads" should {
    "return a valid model" when {
      "a valid non-fhl json with all fields are supplied" in {
        nonFhlDesJson.as[IncomeBreakdown](IncomeBreakdown.nonFhlReads) shouldBe nonFhlIncomeModel
      }

      "a valid fhl json with all fields are supplied" in {
        fhlDesJson.as[IncomeBreakdown](IncomeBreakdown.fhlReads) shouldBe fhlIncomeModel
      }
    }
  }

  "writes" should {
    "return a valid json" when {
      "a valid non-fhl model is supplied" in {
        nonFhlIncomeModel.toJson shouldBe nonFhlMtdJson
      }

      "a valid fhl model is supplied" in {
        fhlIncomeModel.toJson shouldBe fhlMtdJson
      }
    }
  }
}
