/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.controllers.requestParsers.validators.validations

import v3.models.errors.{MtdError, RuleAdjustmentRangeInvalid}

object AdjustmentRangeValidation {

  private val minValue = BigDecimal(-99999999999.99)
  private val maxValue = BigDecimal(99999999999.99)

  def validate(field: Option[BigDecimal], fieldName: String): List[MtdError] = {

    val error = RuleAdjustmentRangeInvalid.copy(paths = Some(Seq(fieldName)))

    field match {
      case Some(value) if value > maxValue || value < minValue  =>
        List(error)
      case _ => List()
    }
  }
}
