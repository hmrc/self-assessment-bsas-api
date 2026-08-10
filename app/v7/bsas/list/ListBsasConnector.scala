/*
 * Copyright 2026 HM Revenue & Customs
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

package v7.bsas.list

import api.config.AppConfig
import api.config.ConfigFeatureSwitches
import api.connectors.DownstreamUri.{HipUri, IfsUri}
import api.connectors.httpparsers.StandardDownstreamHttpParser.*
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v7.bsas.list.model.request.ListBsasRequestData
import v7.bsas.list.model.response.ListBsasResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListBsasConnector @Inject() (val http: HttpClientV2, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def listBsas(request: ListBsasRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[ListBsasResponse]] = {

    import request.*
    import schema.*

    val queryParams = Seq(
      "incomeSourceId"   -> incomeSourceId.map(_.businessId),
      "incomeSourceType" -> incomeSourceType
    ).collect { case (k, Some(v)) => (k, v) }

    val preTysQueryParams = queryParams :+ ("taxYear" -> taxYear.asDownstream)

    lazy val downstreamUri1517 = IfsUri[DownstreamResp](s"income-tax/adjustable-summary-calculation/$nino")
    lazy val downstreamUri1898 =
      if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1898")) {
        HipUri[DownstreamResp](s"itsa/income-tax/v1/${taxYear.asTysDownstream}/adjustable-summary-calculation/$nino")
      } else {
        IfsUri[DownstreamResp](s"income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino")
      }

    if (taxYear.useTaxYearSpecificApi) get(downstreamUri1898, queryParams) else get(downstreamUri1517, preTysQueryParams)
  }

}
