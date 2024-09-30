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

package v5.bsas.list

import shared.config.SharedAppConfig
import shared.connectors.DownstreamUri.{IfsUri, TaxYearSpecificIfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v5.bsas.list.model.request.ListBsasRequestData
import v5.bsas.list.model.response.{BsasSummary, ListBsasResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListBsasConnector @Inject() (val http: HttpClient, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def listBsas(request: ListBsasRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[ListBsasResponse[BsasSummary]]] = {

    import request._
    import schema._

    val queryParams = Map(
      "incomeSourceId"   -> incomeSourceId.map(_.businessId),
      "incomeSourceType" -> incomeSourceType
    )

    val mappedQueryParams: Map[String, String] = queryParams.collect { case (k: String, Some(v: String)) => (k, v) }

    if (taxYear.useTaxYearSpecificApi) {
      get(
        TaxYearSpecificIfsUri[DownstreamResp](s"income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino"),
        mappedQueryParams.toList
      )
    } else {
      val mappedQueryParamsWithTaxYear: Map[String, String] = mappedQueryParams ++ Map("taxYear" -> taxYear.asDownstream)
      get(IfsUri[DownstreamResp](s"income-tax/adjustable-summary-calculation/$nino"), mappedQueryParamsWithTaxYear.toSeq)
    }

  }

}
