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

package v6.ukPropertyBsas.submit

import cats.implicits.*
import common.errors.*
import shared.controllers.RequestContext
import shared.models.errors.*
import shared.services.{BaseService, ServiceOutcome}
import v6.ukPropertyBsas.submit.model.request.SubmitUkPropertyBsasRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitUkPropertyBsasService @Inject() (connector: SubmitUkPropertyBsasConnector) extends BaseService {

  def submitPropertyBsas(
      request: SubmitUkPropertyBsasRequestData
  )(implicit
      ctx: RequestContext,
      ec: ExecutionContext
  ): Future[ServiceOutcome[Unit]] = {

    connector
      .submitPropertyBsas(request)
      .map(_.leftMap(mapDownstreamErrors(errorMap)))

  }

  private val errorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_TAXABLE_ENTITY_ID"     -> NinoFormatError,
      "INVALID_CALCULATION_ID"        -> CalculationIdFormatError,
      "INVALID_CORRELATIONID"         -> InternalError,
      "INVALID_PAYLOAD"               -> InternalError,
      "BVR_FAILURE_C55316"            -> InternalError,
      "BVR_FAILURE_C15320"            -> InternalError,
      "BVR_FAILURE_C55508"            -> RulePropertyIncomeAllowanceClaimed,
      "BVR_FAILURE_C55509"            -> RulePropertyIncomeAllowanceClaimed,
      "BVR_FAILURE_C55503"            -> RuleOverConsolidatedExpensesThreshold,
      "BVR_FAILURE_C559107"           -> InternalError,
      "BVR_FAILURE_C559103"           -> InternalError,
      "BVR_FAILURE_C559099"           -> InternalError,
      "NO_DATA_FOUND"                 -> NotFoundError,
      "ASC_ALREADY_SUPERSEDED"        -> RuleSummaryStatusSuperseded,
      "ASC_ALREADY_ADJUSTED"          -> RuleAlreadyAdjusted,
      "UNALLOWABLE_VALUE"             -> RuleResultingValueNotPermitted,
      "ASC_ID_INVALID"                -> RuleSummaryStatusInvalid,
      "INCOMESOURCE_TYPE_NOT_MATCHED" -> RuleTypeOfBusinessIncorrectError,
      "SERVER_ERROR"                  -> InternalError,
      "SERVICE_UNAVAILABLE"           -> InternalError
    )
    val extraTysErrors = Map(
      "INVALID_TAX_YEAR"               -> TaxYearFormatError,
      "NOT_FOUND"                      -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED"         -> RuleTaxYearNotSupportedError,
      "INCOME_SOURCE_TYPE_NOT_MATCHED" -> RuleTypeOfBusinessIncorrectError
    )

    errors ++ extraTysErrors
  }

}
