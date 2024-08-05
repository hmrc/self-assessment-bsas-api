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

package v6.foreignPropertyBsas.submit.def3.model.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec

class ForeignPropertyIncomeSpec extends UnitSpec {

  val model: ForeignPropertyIncome =
    ForeignPropertyIncome(totalRentsReceived = Some(1.12), premiumsOfLeaseGrant = Some(2.12), otherPropertyIncome = Some(3.12))

  val emptyModel: ForeignPropertyIncome = ForeignPropertyIncome(None, None, None)

  "reads" when {
    "passed mtd json" should {
      "return the corresponding model" in {
        Json
          .parse("""
              |{
              |   "totalRentsReceived": 1.12,
              |   "premiumsOfLeaseGrant": 2.12,
              |   "otherPropertyIncome": 3.12
              |}
              |""".stripMargin)
          .as[ForeignPropertyIncome] shouldBe model
      }
    }

    "passed an empty JSON" should {
      "return an empty model" in {
        JsObject.empty.as[ForeignPropertyIncome] shouldBe emptyModel
      }
    }
  }

  "writes" when {
    "passed a model" should {
      "return the downstream JSON" in {
        Json.toJson(model) shouldBe
          Json.parse("""
              |{
              |   "rent": 1.12,
              |   "premiumsOfLeaseGrant": 2.12,
              |   "otherPropertyIncome": 3.12
              |}
              |""".stripMargin)
      }
    }

    "passed an empty model" should {
      "return an empty JSON" in {
        Json.toJson(emptyModel) shouldBe JsObject.empty
      }
    }
  }

}
