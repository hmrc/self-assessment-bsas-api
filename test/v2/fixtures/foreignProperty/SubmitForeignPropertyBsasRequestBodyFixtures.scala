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

package v2.fixtures.foreignProperty

import v2.models.errors.MtdError

object SubmitForeignPropertyBsasRequestBodyFixtures {

  def rangeError(fieldName: String): MtdError =
    MtdError("RULE_RANGE_INVALID", s"Adjustment value for '$fieldName' falls outside the accepted range")

  def formatError(fieldName: String): MtdError =
    MtdError("FORMAT_ADJUSTMENT_VALUE", s"The format of the '$fieldName' value is invalid")
}
