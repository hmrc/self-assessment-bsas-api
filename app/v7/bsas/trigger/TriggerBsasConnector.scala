/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.bsas.trigger

import shared.config.SharedAppConfig
import shared.connectors.DownstreamUri.{IfsUri, TaxYearSpecificIfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v7.bsas.trigger.model.{TriggerBsasRequestData, TriggerBsasResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerBsasConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def triggerBsas(request: TriggerBsasRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[TriggerBsasResponse]] = {

    import request._
    import schema._

    val downstreamUri: DownstreamUri[DownstreamResp] =
      if (taxYear.useTaxYearSpecificApi) {
        TaxYearSpecificIfsUri(s"income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino")
      } else {
        IfsUri(s"income-tax/adjustable-summary-calculation/$nino")
      }

    post(body, downstreamUri)

  }

}
