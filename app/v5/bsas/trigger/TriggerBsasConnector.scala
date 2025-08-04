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

package v5.bsas.trigger

import shared.config.{ConfigFeatureSwitches, SharedAppConfig}
import shared.connectors.DownstreamUri.{HipUri, IfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser.*
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v5.bsas.trigger.model.{TriggerBsasRequestData, TriggerBsasResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerBsasConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def triggerBsas(request: TriggerBsasRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[TriggerBsasResponse]] = {

    import request.*
    import schema.*

    lazy val downstreamUri1873: DownstreamUri[DownstreamResp] =
      if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1873")) {
        HipUri(s"itsa/income-tax/v1/${taxYear.asTysDownstream}/adjustable-summary-calculation/$nino")
      } else {
        IfsUri(s"income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino")
      }

    lazy val downstreamUri1515: DownstreamUri[DownstreamResp] =
      IfsUri(s"income-tax/adjustable-summary-calculation/$nino")

    val downstreamUri: DownstreamUri[DownstreamResp] =
      if (taxYear.useTaxYearSpecificApi) downstreamUri1873 else downstreamUri1515

    post(body, downstreamUri)

  }

}
