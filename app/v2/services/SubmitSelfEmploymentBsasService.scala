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

package v2.services

import api.controllers.RequestContext
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.BaseService
import cats.implicits._
import v2.connectors.SubmitSelfEmploymentBsasConnector
import v2.models.errors._
import v2.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestData
import v2.models.response.SubmitSelfEmploymentBsasResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitSelfEmploymentBsasService @Inject()(connector: SubmitSelfEmploymentBsasConnector) extends BaseService {

  private val errorMap: Map[String, MtdError] =
    Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_CALCULATION_ID" -> BsasIdFormatError,
      "INVALID_PAYLOAD" -> InternalError,
      "ASC_ID_INVALID" -> RuleSummaryStatusInvalid,
      "ASC_ALREADY_SUPERSEDED" -> RuleSummaryStatusSuperseded,
      "ASC_ALREADY_ADJUSTED" -> RuleBsasAlreadyAdjusted,
      "UNALLOWABLE_VALUE" -> RuleResultingValueNotPermitted,
      "INCOMESOURCE_TYPE_NOT_MATCHED" -> RuleNotSelfEmployment,
      "BVR_FAILURE_C55316" -> RuleOverConsolidatedExpensesThreshold,
      "BVR_FAILURE_C15320" -> RuleTradingIncomeAllowanceClaimed,
      "BVR_FAILURE_C55503" -> InternalError,
      "BVR_FAILURE_C55508" -> InternalError,
      "BVR_FAILURE_C55509" -> InternalError,
      "NO_DATA_FOUND" -> NotFoundError,
      "INVALID_CORRELATIONID" -> InternalError,
      "SERVER_ERROR" -> InternalError,
      "SERVICE_UNAVAILABLE" -> InternalError
    )

  def submitSelfEmploymentBsas(request: SubmitSelfEmploymentBsasRequestData)(
    implicit ctx: RequestContext,
    ec: ExecutionContext): Future[Either[ErrorWrapper, ResponseWrapper[SubmitSelfEmploymentBsasResponse]]] = {

    connector
      .submitSelfEmploymentBsas(request)
      .map(_.leftMap(mapDownstreamErrors(errorMap)))
  }
}
