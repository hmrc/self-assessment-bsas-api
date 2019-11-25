/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.response.common

import play.api.libs.json.{JsSuccess, JsValue, Json}
import support.UnitSpec

class AccountingPeriodSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      | "startDate": "2018-10-11",
      | "endDate": "2019-10-10"
      | }
      |""".stripMargin
  )

  val desJson: JsValue = Json.parse (
    """
      |{
      | "accountingStartDate": "2018-10-11",
      | "accountingEndDate": "2019-10-10"
      | }
      |""".stripMargin)

  val model =
    AccountingPeriod(
      startDate = "2018-10-11",
      endDate = "2019-10-10"
    )

  "Accounting Period" should {

    "write correctly to json" in {
      Json.toJson(model) shouldBe json
    }

    "read correctly to json" in {
      desJson.validate[AccountingPeriod] shouldBe JsSuccess(model)
    }
  }
}
