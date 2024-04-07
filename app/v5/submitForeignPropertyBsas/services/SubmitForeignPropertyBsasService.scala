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

package v5.submitForeignPropertyBsas.services

import cats.implicits._
import shared.controllers.RequestContext
import shared.models
import shared.models.errors._
import shared.services.{BaseService, ServiceOutcome}
import v5.models.errors._
import v5.submitForeignPropertyBsas.connectors.SubmitForeignPropertyBsasConnector
import v5.submitForeignPropertyBsas.models.SubmitForeignPropertyBsasRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitForeignPropertyBsasService @Inject()(connector: SubmitForeignPropertyBsasConnector) extends BaseService {

  private val errorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_CALCULATION_ID" -> CalculationIdFormatError,
      "INVALID_CORRELATIONID" -> models.errors.InternalError,
      "INVALID_PAYLOAD" -> models.errors.InternalError,
      "BVR_FAILURE_C15320" -> models.errors.InternalError,
      "BVR_FAILURE_C55508" -> models.errors.InternalError,
      "BVR_FAILURE_C55509" -> models.errors.InternalError,
      "BVR_FAILURE_C559107" -> RulePropertyIncomeAllowanceClaimed,
      "BVR_FAILURE_C559103" -> RulePropertyIncomeAllowanceClaimed,
      "BVR_FAILURE_C559099" -> RuleOverConsolidatedExpensesThreshold,
      "BVR_FAILURE_C55503" -> models.errors.InternalError,
      "BVR_FAILURE_C55316" -> models.errors.InternalError,
      "NO_DATA_FOUND" -> NotFoundError,
      "ASC_ALREADY_SUPERSEDED" -> RuleSummaryStatusSuperseded,
      "ASC_ALREADY_ADJUSTED" -> RuleAlreadyAdjusted,
      "UNALLOWABLE_VALUE" -> RuleResultingValueNotPermitted,
      "ASC_ID_INVALID" -> RuleSummaryStatusInvalid,
      "INCOMESOURCE_TYPE_NOT_MATCHED" -> RuleTypeOfBusinessIncorrectError,
      "SERVER_ERROR" -> models.errors.InternalError,
      "SERVICE_UNAVAILABLE" -> models.errors.InternalError,
    )

    val extraTysErrors =
      Map(
        "INVALID_TAX_YEAR" -> TaxYearFormatError,
        "NOT_FOUND" -> NotFoundError,
        "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError,
        "INCOME_SOURCE_TYPE_NOT_MATCHED" -> RuleTypeOfBusinessIncorrectError
      )

    errors ++ extraTysErrors
  }

  def submitForeignPropertyBsas(request: SubmitForeignPropertyBsasRequestData)(implicit ctx: RequestContext,
                                                                               ec: ExecutionContext): Future[ServiceOutcome[Unit]] = {

    connector
      .submitForeignPropertyBsas(request)
      .map(_.leftMap(mapDownstreamErrors(errorMap)))
  }
}
