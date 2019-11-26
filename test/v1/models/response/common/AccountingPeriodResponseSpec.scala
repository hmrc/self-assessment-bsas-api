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

import play.api.libs.json.{JsError, JsSuccess, Json}
import support.UnitSpec
import v1.fixtures.ListBsasFixtures._

class AccountingPeriodResponseSpec extends UnitSpec {

  val model =
    AccountingPeriodResponse(
      startDate = "2018-10-11",
      endDate = "2019-10-10"
    )

  "AccountingPeriodResponse" should {

    "write correctly to JSON" in {
      Json.toJson(model) shouldBe accountingJSON
    }

    "read from valid JSON" in {
      accountingFromDesJSON.validate[AccountingPeriodResponse] shouldBe JsSuccess(model)
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        invalidAccountingJson.validate[AccountingPeriodResponse] shouldBe a[JsError]
      }
    }
  }
}
