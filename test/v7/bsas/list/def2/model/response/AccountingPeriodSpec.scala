/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.bsas.list.def2.model.response

import play.api.libs.json.{JsError, JsObject, Json}
import shared.utils.UnitSpec
import v7.bsas.list.def2.model.Def2_ListBsasFixtures

class AccountingPeriodSpec extends UnitSpec with Def2_ListBsasFixtures {

  "AccountingPeriod" when {
    "read from valid JSON" should {
      "return the expected object" in {
        val result = accountingPeriodDownstreamJson.as[AccountingPeriod]
        result shouldBe accountingPeriod
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        val result = JsObject.empty.validate[AccountingPeriod]
        result shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JSON" in {
        val result = Json.toJson(accountingPeriod)
        result shouldBe accountingPeriodJson
      }
    }
  }

}
