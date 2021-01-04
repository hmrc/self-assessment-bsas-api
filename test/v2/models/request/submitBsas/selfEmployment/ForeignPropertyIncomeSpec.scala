/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.models.request.submitBsas.selfEmployment

import play.api.libs.json.Json
import support.UnitSpec
import v2.fixtures.selfEmployment.IncomeFixture._
import v2.models.domain.EmptyJsonBody

class ForeignPropertyIncomeSpec extends UnitSpec {

  val incomeModelWithoutOther: Income =
    Income(
      turnover = Some(1000.75),
      other = None
    )

  val incomeModelEmpty: Income =
    Income(
      turnover = None,
      other = None
    )

  "Income" when {
    "read from valid JSON" should {
      "produce the expected Income object" in {
        incomeJson(incomeModel).as[Income] shouldBe incomeModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(incomeModel) shouldBe incomeJson(incomeModel)
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        incomeJson(incomeModelWithoutOther).as[Income] shouldBe incomeModelWithoutOther
      }

      "not write those fields to JSON" in {
        Json.toJson(incomeModelWithoutOther) shouldBe incomeJson(incomeModelWithoutOther)
      }
    }

    "no fields as supplied" should {
      "read to an empty Income object" in {
        incomeJson(incomeModelEmpty).as[Income] shouldBe incomeModelEmpty
      }

      "write to empty JSON" in {
        Json.toJson(incomeModelEmpty) shouldBe Json.toJson(EmptyJsonBody)
      }
    }

    "isEmpty is called" should {
      "return true when all empty fields are supplied" in {
        incomeJson(incomeModelEmpty).as[Income].isEmpty shouldBe true
      }

      "return false when non-empty fields is supplied" in {
        incomeJson(incomeModelEmpty.copy(Some(1000.49))).as[Income].isEmpty shouldBe false
      }
    }
  }
}
