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

package v5.selfEmploymentBsas.submit.def1.model.request

import play.api.libs.json.Json
import shared.UnitSpec
import shared.models.domain.EmptyJsonBody
import v5.selfEmploymentBsas.submit.def1.model.request.fixtures.IncomeFixture._

class IncomeSpec extends UnitSpec {

  private val incomeWithoutOther: Income =
    Income(
      turnover = Some(1000.75),
      other = None
    )

  private val emptyIncome: Income =
    Income(
      turnover = None,
      other = None
    )

  "Income" when {
    "read from valid JSON" should {
      "produce the expected Income object" in {
        incomeJson(income).as[Income] shouldBe income
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(income) shouldBe incomeJson(income)
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        incomeJson(incomeWithoutOther).as[Income] shouldBe incomeWithoutOther
      }

      "not write those fields to JSON" in {
        Json.toJson(incomeWithoutOther) shouldBe incomeJson(incomeWithoutOther)
      }
    }

    "no fields as supplied" should {
      "read to an empty Income object" in {
        incomeJson(emptyIncome).as[Income] shouldBe emptyIncome
      }

      "write to empty JSON" in {
        Json.toJson(emptyIncome) shouldBe Json.toJson(EmptyJsonBody)
      }
    }
  }

}
