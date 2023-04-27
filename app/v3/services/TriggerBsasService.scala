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

package v3.services

import api.controllers.RequestContext
import api.models
import api.models.errors._
import api.services.{ BaseService, ServiceOutcome }
import cats.implicits._
import v3.connectors.TriggerBsasConnector
import v3.models.errors._
import v3.models.request.triggerBsas.TriggerBsasRequest
import v3.models.response.TriggerBsasResponse

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class TriggerBsasService @Inject()(connector: TriggerBsasConnector) extends BaseService {

  def triggerBsas(request: TriggerBsasRequest)(implicit ctx: RequestContext, ec: ExecutionContext): Future[ServiceOutcome[TriggerBsasResponse]] =
    connector
      .triggerBsas(request)
      .map(_.leftMap(mapDownstreamErrors(errorMap)))

  private val errorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_TAXABLE_ENTITY_ID"   -> NinoFormatError,
      "INVALID_CORRELATIONID"       -> models.errors.InternalError,
      "INVALID_PAYLOAD"             -> models.errors.InternalError,
      "NO_DATA_FOUND"               -> NotFoundError,
      "ACCOUNTING_PERIOD_NOT_ENDED" -> RuleAccountingPeriodNotEndedError,
      "OBLIGATIONS_NOT_MET"         -> RulePeriodicDataIncompleteError,
      "NO_ACCOUNTING_PERIOD"        -> RuleNoAccountingPeriodError,
      "SERVER_ERROR"                -> models.errors.InternalError,
      "SERVICE_UNAVAILABLE"         -> models.errors.InternalError
    )
    val extraTysErrors =
      Map(
        "INVALID_TAX_YEAR"       -> models.errors.InternalError,
        "INVALID_CORRELATION_ID" -> models.errors.InternalError,
        "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
      )

    errors ++ extraTysErrors
  }
}
