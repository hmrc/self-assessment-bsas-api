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

package v3.connectors

import config.AppConfig
import uk.gov.hmrc.http.{ HeaderCarrier, HttpClient }
import v3.connectors.DownstreamUri.{ DesUri, TaxYearSpecificIfsUri }
import v3.connectors.httpparsers.StandardDownstreamHttpParser._
import v3.models.request.triggerBsas.TriggerBsasRequest
import v3.models.response.TriggerBsasResponse

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class TriggerBsasConnector @Inject()(val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def triggerBsas(request: TriggerBsasRequest)(implicit hc: HeaderCarrier,
                                               ec: ExecutionContext,
                                               correlationId: String): Future[DownstreamOutcome[TriggerBsasResponse]] = {

    import request._

    val downstreamUri =
      if (taxYear.useTaxYearSpecificApi) {
        TaxYearSpecificIfsUri[TriggerBsasResponse](s"income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino")
      } else {
        DesUri[TriggerBsasResponse](s"income-tax/adjustable-summary-calculation/$nino")
      }

    post(
      body = request.body,
      uri = downstreamUri
    )
  }
}
