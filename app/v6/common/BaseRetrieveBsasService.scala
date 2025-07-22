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

package v6.common

import common.errors.*
import shared.models.domain.TaxYear
import shared.models.errors.{ErrorWrapper, NotFoundError}
import shared.models.outcomes.ResponseWrapper
import shared.services.{BaseService, ServiceOutcome}
import v6.common.model.{HasIncomeSourceType, HasTaxYear}

trait BaseRetrieveBsasService extends BaseService {

  protected val supportedIncomeSourceType: Set[String]

  final protected def validateTypeOfBusiness[T <: HasIncomeSourceType](responseWrapper: ResponseWrapper[T]): ServiceOutcome[T] =
    if (supportedIncomeSourceType.contains(responseWrapper.responseData.incomeSourceType)) {
      Right(responseWrapper)
    } else {
      Left(ErrorWrapper(responseWrapper.correlationId, RuleTypeOfBusinessIncorrectError, None))
    }

  final protected def checkTaxYear[T <: HasTaxYear](taxYear: TaxYear, responseWrapper: ResponseWrapper[T]): ServiceOutcome[T] = {
    if (taxYear.useTaxYearSpecificApi || taxYear == responseWrapper.responseData.taxYear) {
      Right(responseWrapper)
    } else {
      Left(ErrorWrapper(responseWrapper.correlationId, NotFoundError, None))
    }
  }

}
