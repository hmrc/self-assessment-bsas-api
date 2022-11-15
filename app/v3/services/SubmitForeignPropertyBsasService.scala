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

package v3.services

import cats.data.EitherT
import cats.implicits._
import javax.inject.{ Inject, Singleton }
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v3.connectors.SubmitForeignPropertyBsasConnector
import v3.controllers.EndpointLogContext
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.submitBsas.foreignProperty.SubmitForeignPropertyBsasRequestData
import v3.support.DesResponseMappingSupport

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class SubmitForeignPropertyBsasService @Inject()(connector: SubmitForeignPropertyBsasConnector) extends DesResponseMappingSupport with Logging {

  def submitForeignPropertyBsas(request: SubmitForeignPropertyBsasRequestData)(
      implicit hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[Unit]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.submitForeignPropertyBsas(request)).leftMap(mapDesErrors(errorMap))
    } yield desResponseWrapper

    result.value
  }

  private val errorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_TAXABLE_ENTITY_ID"     -> NinoFormatError,
      "INVALID_CALCULATION_ID"        -> CalculationIdFormatError,
      "INVALID_CORRELATIONID"         -> DownstreamError,
      "INVALID_PAYLOAD"               -> DownstreamError,
      "BVR_FAILURE_C15320"            -> DownstreamError,
      "BVR_FAILURE_C55508"            -> DownstreamError,
      "BVR_FAILURE_C55509"            -> DownstreamError,
      "BVR_FAILURE_C559107"           -> RulePropertyIncomeAllowanceClaimed,
      "BVR_FAILURE_C559103"           -> RulePropertyIncomeAllowanceClaimed,
      "BVR_FAILURE_C559099"           -> RuleOverConsolidatedExpensesThreshold,
      "BVR_FAILURE_C55503"            -> DownstreamError,
      "BVR_FAILURE_C55316"            -> DownstreamError,
      "NO_DATA_FOUND"                 -> NotFoundError,
      "ASC_ALREADY_SUPERSEDED"        -> RuleSummaryStatusSuperseded,
      "ASC_ALREADY_ADJUSTED"          -> RuleAlreadyAdjusted,
      "UNALLOWABLE_VALUE"             -> RuleResultingValueNotPermitted,
      "ASC_ID_INVALID"                -> RuleSummaryStatusInvalid,
      "INCOMESOURCE_TYPE_NOT_MATCHED" -> RuleTypeOfBusinessIncorrectError,
      "SERVER_ERROR"                  -> DownstreamError,
      "SERVICE_UNAVAILABLE"           -> DownstreamError,
    )

    val extraTysErrors =
      Map(
        "INVALID_TAX_YEAR"            -> TaxYearFormatError,
        "NOT_FOUND"                   -> NotFoundError,
        "RULE_TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
      )

    errors ++ extraTysErrors
  }
}
