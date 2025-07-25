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

package v6.bsas.trigger

import cats.implicits.*
import common.errors.*
import shared.controllers.RequestContext
import shared.models.errors.*
import shared.services.{BaseService, ServiceOutcome}
import v6.bsas.trigger.model.{TriggerBsasRequestData, TriggerBsasResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerBsasService @Inject() (connector: TriggerBsasConnector) extends BaseService {

  def triggerBsas(
      request: TriggerBsasRequestData
  )(implicit
      ctx: RequestContext,
      ec: ExecutionContext
  ): Future[ServiceOutcome[TriggerBsasResponse]] = {

    connector
      .triggerBsas(request)
      .map(_.leftMap(mapDownstreamErrors(errorMap)))
  }

  private val errorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_TAXABLE_ENTITY_ID"   -> NinoFormatError,
      "INVALID_CORRELATIONID"       -> InternalError,
      "INVALID_PAYLOAD"             -> InternalError,
      "NO_DATA_FOUND"               -> TriggerNotFoundError,
      "ACCOUNTING_PERIOD_NOT_ENDED" -> RuleAccountingPeriodNotEndedError,
      "OBLIGATIONS_NOT_MET"         -> RuleObligationsNotMet,
      "NO_ACCOUNTING_PERIOD"        -> RuleNoAccountingPeriodError,
      "SERVER_ERROR"                -> InternalError,
      "SERVICE_UNAVAILABLE"         -> InternalError
    )
    val extraTysErrors =
      Map(
        "INVALID_TAX_YEAR"       -> InternalError,
        "INVALID_CORRELATION_ID" -> InternalError,
        "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
      )

    errors ++ extraTysErrors
  }

}
