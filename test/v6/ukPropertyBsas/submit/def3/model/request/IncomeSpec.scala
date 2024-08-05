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

package v6.ukPropertyBsas.submit.def3.model.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec
import v6.ukPropertyBsas.submit.def3.model.request

class IncomeSpec extends UnitSpec {

  private val income: request.Income = Income(Some(123.12), Some(123.12), Some(123.12), Some(123.12))

  private val emptyIncome: request.Income = Income(None, None, None, None)

  "reads" when {
    "given MTD json" should {
      "return the expected parsed object" in {
        Json
          .parse("""
              |{
              |   "totalRentsReceived": 123.12,
              |   "premiumsOfLeaseGrant": 123.12,
              |   "reversePremiums": 123.12,
              |   "otherPropertyIncome": 123.12
              |}
              |""".stripMargin)
          .as[request.Income] shouldBe income
      }
    }

    "given an empty JSON object" should {
      "return an empty FHLIncome" in {
        JsObject.empty.as[request.Income] shouldBe emptyIncome
      }
    }
  }

  "writes" when {
    "given an FHLIncome object" should {
      "return the downstream JSON" in {
        Json.toJson(income) shouldBe
          Json
            .parse("""
                |{
                |   "totalRentsReceived": 123.12,
                |   "premiumsOfLeaseGrant": 123.12,
                |   "reversePremiums": 123.12,
                |   "otherPropertyIncome": 123.12
                |}
                |""".stripMargin)
      }
    }

    "given an empty FHLIncome" should {
      "return an empty JSON object" in {
        Json.toJson(emptyIncome) shouldBe JsObject.empty
      }
    }
  }

}
