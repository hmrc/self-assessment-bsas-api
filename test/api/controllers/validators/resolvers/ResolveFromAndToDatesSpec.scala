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

package api.controllers.validators.resolvers

import api.models.errors.{EndDateFormatError, StartDateFormatError}
import cats.data.Validated.{Invalid, Valid}
import support.UnitSpec

import java.time.LocalDate

class ResolveFromAndToDatesSpec extends UnitSpec {

  private val minTaxYear = 2017
  private val maxTaxYear = 2021

  val resolveFromAndToDates = new ResolveFromAndToDates(minTaxYear, maxTaxYear)

  "ResolvePeriodIdSpec" should {
    "return no errors" when {
      "passed valid from and to dates" in {
        val fromDate = "2019-04-06"
        val toDate   = "2019-08-06"

        val dateRange = DateRange(LocalDate.parse(fromDate), LocalDate.parse(toDate))
        val result = resolveFromAndToDates(dateRange)

        result shouldBe Valid(dateRange)
      }

      "passed valid from and to dates equal to the minimum and maximum" in {
        val fromDate = "2018-04-06"
        val toDate   = "2020-04-06"
        val dateRange = DateRange(LocalDate.parse(fromDate), LocalDate.parse(toDate))
        val result = resolveFromAndToDates(dateRange)

        result shouldBe Valid(dateRange)
      }
    }

    "return an error" when {
      "passed a fromYear less than or equal to minimumTaxYear" in {
        val dateRange = DateRange(LocalDate.parse("2016-04-06"), LocalDate.parse("2019-04-05"))
        val result = resolveFromAndToDates(dateRange)
        result shouldBe Invalid(List(StartDateFormatError))
      }

      "passed a toYear greater than or equal to maximumTaxYear" in {
        val dateRange = DateRange(LocalDate.parse("2020-04-06"), LocalDate.parse("2021-04-05"))
        val result = resolveFromAndToDates(dateRange)
        result shouldBe Invalid(List(EndDateFormatError))
      }

      "passed both dates that are out of range" in {
        val dateRange = DateRange(LocalDate.parse("2015-04-06"), LocalDate.parse("2021-04-05"))
        val result = resolveFromAndToDates(dateRange)
        result shouldBe Invalid(List(StartDateFormatError, EndDateFormatError))
      }

    }
  }

}
