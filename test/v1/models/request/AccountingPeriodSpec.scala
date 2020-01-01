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

package v1.models.request

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import support.UnitSpec

class AccountingPeriodSpec extends UnitSpec {

  val requestJson: JsValue = Json.parse("""
      |{
      |  "startDate" : "2018-11-25",
      |  "endDate" : "2018-11-26"
      |}
  """.stripMargin)

  val invalidJson: JsValue = Json.parse("""
      |{
      |  "startDate" : 4,
      |  "endDate" : true
      |}
  """.stripMargin)

  val model: AccountingPeriod = AccountingPeriod("2018-11-25","2018-11-26")

  "AccountingPeriod" when {
    "read from valid JSON" should {
      "return the expected AccountingPeriod object" in {
        requestJson.validate[AccountingPeriod] shouldBe JsSuccess(model)
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidJson.validate[AccountingPeriod] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JsValue" in {
        Json.toJson(model) shouldBe requestJson
      }
    }
  }
}
