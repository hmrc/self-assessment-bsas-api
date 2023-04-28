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
import v2.connectors.TriggerBsasConnector
import v2.models.errors.{ RuleAccountingPeriodNotEndedError, RuleNoAccountingPeriodError, RulePeriodicDataIncompleteError }
import v2.models.request.triggerBsas.TriggerBsasRequest
import v2.models.response.TriggerBsasResponse

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class TriggerBsasService @Inject()(connector: TriggerBsasConnector) extends BaseService {

  def triggerBsas(request: TriggerBsasRequest)(implicit ctx: RequestContext,
                                               ec: ExecutionContext): Future[Either[ErrorWrapper, ResponseWrapper[TriggerBsasResponse]]] = {

    connector
      .triggerBsas(request)
      .map(_.leftMap(mapDownstreamErrors(errorMap)))
  }

  private val errorMap: Map[String, MtdError] =
    Map(
      "INVALID_TAXABLE_ENTITY_ID"    -> NinoFormatError,
      "ACCOUNTING_PERIOD_NOT_ENDED"  -> RuleAccountingPeriodNotEndedError,
      "OBLIGATIONS_NOT_MET"          -> RulePeriodicDataIncompleteError,
      "NO_ACCOUNTING_PERIOD"         -> RuleNoAccountingPeriodError,
      "NO_DATA_FOUND"                -> NotFoundError,
      "INVALID_PAYLOAD"              -> InternalError,
      "SERVER_ERROR"                 -> InternalError,
      "SERVICE_UNAVAILABLE"          -> InternalError,
      "INCOME_SOURCEID_NOT_PROVIDED" -> InternalError,
      "INVALID_CORRELATIONID"        -> InternalError
    )
}
