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

import api.models.errors.{ EndDateFormatError, MtdError, RuleEndBeforeStartDateError, StartDateFormatError }

import java.time.LocalDate

case class DateRange(startDate: LocalDate, endDate: LocalDate)

object ResolveDateRange extends Resolver[(String, String), DateRange] {

  def apply(value: (String, String), notUsedError: Option[MtdError], path: Option[String]): Either[Seq[MtdError], DateRange] = {
    val (startDate, endDate) = value

    ResolverHelpers.flatten[DateRange](for {
      parsedStartDate <- ResolveIsoDate(startDate, StartDateFormatError)
      parsedEndDate   <- ResolveIsoDate(endDate, EndDateFormatError)
    } yield {
      val startDateEpochTime = parsedStartDate.toEpochDay
      val endDateEpochTime   = parsedEndDate.toEpochDay

      if ((endDateEpochTime - startDateEpochTime) <= 0) {
        Left(List(RuleEndBeforeStartDateError))
      } else {
        Right(DateRange(parsedStartDate, parsedEndDate))
      }
    })
  }

}
