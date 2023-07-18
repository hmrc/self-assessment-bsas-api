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

package v2.models.response.retrieveBsasAdjustments.foreignProperty

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v2.fixtures.foreignProperty.RetrieveForeignPropertyAdjustmentsFixtures._
import v2.models.utils.JsonErrorValidators

class IncomeBreakdownSpec extends UnitSpec with JsonErrorValidators {

  val fhlDesJson: JsValue = Json.parse(
    """{
      |         "rent": 100.49
      | }
    """.stripMargin
  )

  val nonFhlDesJson: JsValue = Json.parse(
    """{
      |         "rent": 100.49,
      |         "premiumsOfLeaseGrant": 100.49,
      |         "otherPropertyIncome": 100.49,
      |         "foreignPropertyTaxTakenOff": 100.49
      | }
    """.stripMargin
  )

  val fhlMtdJson: JsValue = Json.parse(
    """{
      |         "rentIncome": 100.49
      | }
    """.stripMargin
  )

  val nonFhlMtdJson: JsValue = Json.parse(
    """{
      |         "rentIncome": 100.49,
      |         "premiumsOfLeaseGrant": 100.49,
      |         "otherPropertyIncome": 100.49
      | }
    """.stripMargin
  )

  "IncomeBreakdown" when {
    "reading from valid JSON" should {
      "return the fhl model" in {
        fhlDesJson.as[IncomeBreakdown](IncomeBreakdown.fhlReads) shouldBe fhlIncomeModel
      }

      "return the non-fhl model" in {
        nonFhlDesJson.as[IncomeBreakdown](IncomeBreakdown.nonFhlReads) shouldBe nonFhlIncomeModel
      }
    }

    "writing to valid json" should {
      "return valid non fhl json" in {
        nonFhlIncomeModel.toJson shouldBe nonFhlMtdJson
      }

      "return valid fhl json" in {
        fhlIncomeModel.toJson shouldBe fhlMtdJson
      }
    }
  }
}
