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

package v6.ukPropertyBsas.submit.def1.model.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec

class NonFHLIncomeSpec extends UnitSpec {

  private val json = Json.parse("""
        |{
        |   "totalRentsReceived": 1.12,
        |   "premiumsOfLeaseGrant": 2.12,
        |   "reversePremiums": 3.12,
        |   "otherPropertyIncome": 4.12
        |}
        |""".stripMargin)

  private val nonFhlIncome =
    NonFHLIncome(
      totalRentsReceived = Some(1.12),
      premiumsOfLeaseGrant = Some(2.12),
      reversePremiums = Some(3.12),
      otherPropertyIncome = Some(4.12)
    )

  private val emptyNonFHLIncome = NonFHLIncome(None, None, None, None)

  "reads" when {
    "given MTD json" should {
      "return the expected NonFHLIncome" in {
        json
          .as[NonFHLIncome] shouldBe nonFhlIncome
      }
    }

    "given an empty JSON object" should {
      "return an empty NonFHLIncome" in {
        JsObject.empty.as[NonFHLIncome] shouldBe emptyNonFHLIncome
      }
    }
  }

  "writes" when {
    "given a NonFHLIncome" should {
      "return the downstream JSON" in {
        Json.toJson(nonFhlIncome) shouldBe json
      }
    }

    "given an empty NonFHLIncome" should {
      "return an empty JSON object" in {
        Json.toJson(emptyNonFHLIncome) shouldBe JsObject.empty
      }
    }
  }

}
