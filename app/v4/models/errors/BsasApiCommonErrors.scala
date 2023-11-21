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

package v4.models.errors

import play.api.http.Status.{BAD_REQUEST, NOT_FOUND}
import shared.models.errors.MtdError

// MtdError types that are common to SA BSAS API.

object TypeOfBusinessFormatError extends MtdError("FORMAT_TYPE_OF_BUSINESS", "The provided type of business is invalid", BAD_REQUEST)

object AdjustedStatusFormatError extends MtdError("FORMAT_ADJUSTED_STATUS", "The supplied adjusted status format is invalid", BAD_REQUEST)

object FormatAdjustmentValueError extends MtdError("FORMAT_ADJUSTMENT_VALUE", "The format of the adjustment value is invalid", BAD_REQUEST)

object BsasIdFormatError extends MtdError("FORMAT_BSAS_ID", "The format of the BSAS ID is invalid", BAD_REQUEST)

object TriggerNotFoundError
    extends MtdError(
      "MATCHING_RESOURCE_NOT_FOUND",
      "A matching incomeSourceId record was not found, or the incomeSourceType provided does not relate to the incomeSourceId",
      NOT_FOUND)
