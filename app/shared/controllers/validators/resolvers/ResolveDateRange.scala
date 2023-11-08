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

package shared.controllers.validators.resolvers

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import shared.models.domain.DateRange
import shared.models.errors.{EndDateFormatError, MtdError, RuleEndBeforeStartDateError, StartDateFormatError}

import java.time.LocalDate
import scala.math.Ordering.Implicits.infixOrderingOps

case class ResolveDateRange(startDateFormatError: MtdError = StartDateFormatError,
                            endDateFormatError: MtdError = EndDateFormatError,
                            endBeforeStartDateError: MtdError = RuleEndBeforeStartDateError)
    extends ResolverSupport {

  private def resolveDateRange(parsedStartDate: LocalDate, parsedEndDate: LocalDate): Validated[Seq[MtdError], DateRange] =
    if (parsedEndDate <= parsedStartDate)
      Invalid(List(endBeforeStartDateError))
    else
      Valid(DateRange(parsedStartDate, parsedEndDate))

  val resolver: Resolver[(String, String), DateRange] = { case (startDate, endDate) =>
    (
      ResolveIsoDate(startDate, startDateFormatError),
      ResolveIsoDate(endDate, endDateFormatError)
    ).mapN(resolveDateRange).andThen(identity)
  }

  def apply(value: (String, String)): Validated[Seq[MtdError], DateRange] = resolver(value)

}
