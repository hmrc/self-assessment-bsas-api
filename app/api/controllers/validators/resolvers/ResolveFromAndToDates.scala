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

import api.models.errors.{EndDateFormatError, MtdError, StartDateFormatError}
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.toFoldableOps

class ResolveFromAndToDates(minYear: Int, maximumYear: Int) {

  def apply(value: DateRange): Validated[Seq[MtdError], DateRange] = {
    val validatedFromDate = if (value.startDate.getYear < minYear) Invalid(List(StartDateFormatError)) else Valid(())
    val validatedToDate   = if (value.endDate.getYear >= maximumYear) Invalid(List(EndDateFormatError)) else Valid(())

    List(
      validatedFromDate,
      validatedToDate
    ).traverse_(identity).map(_ => value)

  }

}
