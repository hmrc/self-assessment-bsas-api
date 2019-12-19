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

package v1.models.request.submitBsas.selfEmployment

import play.api.libs.json.Json
import support.UnitSpec
import v1.fixtures.request.submitBsas.selfEmployment.SeIncomeFixture._
import v1.models.domain.EmptyJsonBody

class SeIncomeSpec extends UnitSpec {

  val seIncomeModelWithoutOther: SeIncome =
    SeIncome(
      turnover = Some(1000.75),
      other = None
    )

  val seIncomeModelEmpty: SeIncome =
    SeIncome(
      turnover = None,
      other = None
    )

  "SeIncome" when {
    "read from valid JSON" should {
      "produce the expected SeIncome object" in {
        seIncomeJson(seIncomeModel).as[SeIncome] shouldBe seIncomeModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(seIncomeModel) shouldBe seIncomeJson(seIncomeModel)
      }
    }

    "some optional fields as not supplied" should {
      "read those fields as 'None'" in {
        seIncomeJson(seIncomeModelWithoutOther).as[SeIncome] shouldBe seIncomeModelWithoutOther
      }

      "not write those fields to JSON" in {
        Json.toJson(seIncomeModelWithoutOther) shouldBe seIncomeJson(seIncomeModelWithoutOther)
      }
    }


    "no fields as supplied" should {
      "read to an empty SeIncome object" in {
        seIncomeJson(seIncomeModelEmpty).as[SeIncome] shouldBe seIncomeModelEmpty
      }

      "write to empty JSON" in {
        Json.toJson(seIncomeModelEmpty) shouldBe Json.toJson(EmptyJsonBody)
      }
    }
  }
}
