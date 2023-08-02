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

package utils

import support.UnitSpec
import v3.models.domain.DownstreamTaxYear

import java.time.LocalDate

class DateUtilsSpec extends UnitSpec {

  "getDownstreamTaxYear" should {
    "return a valid DownstreamTaxYear" when {
      "a LocalDate is supplied" in {
        DateUtils.getDownstreamTaxYear(LocalDate.parse(s"2019-04-05")) shouldBe DownstreamTaxYear("2019")
      }
    }
  }

  "getTaxYear" should {
    "return a valid TaxYear" when {
      "no tax year is supplied when the date is 5th April of the current year" in {
        DateUtils.getDownstreamTaxYear(LocalDate.parse(s"2019-04-05")) shouldBe DownstreamTaxYear("2019")
      }

      "no tax year is supplied when the date is 6th April of the current year" in {
        DateUtils.getDownstreamTaxYear(LocalDate.parse("2019-04-06")) shouldBe DownstreamTaxYear("2020")
      }
    }
  }
}
