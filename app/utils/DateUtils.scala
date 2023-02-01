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

import v2.models.domain.DownstreamTaxYear
import v3.models.domain.TaxYear
import java.time.LocalDate

object DateUtils {

  def getDownstreamTaxYear(dateProvided: Any): DownstreamTaxYear = dateProvided match {
    case taxYear: String => DownstreamTaxYear.fromMtd(taxYear)
    case current: LocalDate =>
      val fiscalYearStartDate = LocalDate.parse(s"${current.getYear.toString}-04-05")

      if(current.isAfter(fiscalYearStartDate)) DownstreamTaxYear((current.getYear + 1).toString)
      else DownstreamTaxYear(current.getYear.toString)
  }

  def getTaxYear(dateProvided: Any): TaxYear = dateProvided match {
    case taxYear: String => TaxYear.fromMtd(taxYear)
    case current: LocalDate =>
      val fiscalYearStartDate = LocalDate.parse(s"${current.getYear.toString}-04-05")

      if(current.isAfter(fiscalYearStartDate)) TaxYear((current.getYear + 1).toString)
      else TaxYear(current.getYear.toString)
  }
}
