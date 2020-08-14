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

package v2.models.response.retrieveBsasAdjustments.ukProperty

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v2.models.utils.JsonErrorValidators
import v2.fixtures.ukProperty.RetrieveUkPropertyAdjustmentsFixtures._

class IncomeBreakdownSpec extends UnitSpec with JsonErrorValidators {

  val fhlDesJson: JsValue = Json.parse(
    """{
      |         "rentReceived": 100.49,
      |         "premiumsOfLeaseGrant": 100.49,
      |         "reversePremiums": 100.49,
      |         "otherPropertyIncome": 100.49
      | }
    """.stripMargin
  )

  val nonFhlDesJson: JsValue = Json.parse(
    """{
      |         "totalRentsReceived": 100.49,
      |         "premiumsOfLeaseGrant": 100.49,
      |         "reversePremiums": 100.49,
      |         "otherPropertyIncome": 100.49
      | }
    """.stripMargin
  )

  val mtdJson: JsValue = Json.parse(
    """{
      |         "rentIncome": 100.49,
      |         "premiumsOfLeaseGrant": 100.49,
      |         "reversePremiums": 100.49,
      |         "otherPropertyIncome": 100.49
      | }
    """.stripMargin
  )

  "IncomeBreakdown" when {
    "reading from valid JSON" should {
      "return the fhl model" in {
        fhlDesJson.as[IncomeBreakdown](IncomeBreakdown.fhlReads) shouldBe incomeModel
      }

      "return the non-fhl model" in {
        nonFhlDesJson.as[IncomeBreakdown](IncomeBreakdown.nonFhlReads) shouldBe incomeModel
      }
    }

    "writing to valid json" should {
      "return valid json" in {
        incomeModel.toJson shouldBe mtdJson
      }
    }
  }
}
