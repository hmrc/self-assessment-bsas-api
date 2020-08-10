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

package v2.models.response.retrieveBsas.selfEmployment

import support.UnitSpec
import v2.fixtures.selfEmployment.RetrieveSelfEmploymentBsasFixtures.{desIncomeBreakdownJson, incomeBreakdownModel, mtdIncomeBreakdownJson}
import v2.models.utils.JsonErrorValidators

class IncomeBreakdownSpec extends UnitSpec with JsonErrorValidators {

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        desIncomeBreakdownJson.as[IncomeBreakdown] shouldBe incomeBreakdownModel
      }
    }
    testPropertyType[IncomeBreakdown](desIncomeBreakdownJson)(
      path = "/turnover",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
    testPropertyType[IncomeBreakdown](desIncomeBreakdownJson)(
      path = "/other",
      replacement = "test".toJson,
      expectedError = JsonError.NUMBER_FORMAT_EXCEPTION
    )
  }

  "writes" when {
    "passed a valid model" should {
      "return valid JSON" in {
        incomeBreakdownModel.toJson shouldBe mtdIncomeBreakdownJson
      }
    }
  }

}
