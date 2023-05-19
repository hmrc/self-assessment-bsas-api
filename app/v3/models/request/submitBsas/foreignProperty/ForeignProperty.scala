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

package v3.models.request.submitBsas.foreignProperty

import play.api.libs.json.{ Format, Json }
import shapeless.HNil
import utils.EmptinessChecker

import scala.annotation.nowarn

case class ForeignProperty(countryCode: String, income: Option[ForeignPropertyIncome], expenses: Option[ForeignPropertyExpenses])

object ForeignProperty {

  @nowarn("cat=lint-byname-implicit")
  implicit val emptinessChecker: EmptinessChecker[ForeignProperty] = EmptinessChecker.use { body =>
    "income"     -> body.income ::
      "expenses" -> body.expenses :: HNil
  }

  implicit val format: Format[ForeignProperty] = Json.format[ForeignProperty]
}
