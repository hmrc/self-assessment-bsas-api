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

package v3.connectors

import api.connectors.DownstreamUri.{IfsUri, TaxYearSpecificIfsUri}
import api.connectors.httpparsers.StandardDownstreamHttpParser._
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v3.models.request.ListBsasRequest
import v3.models.response.listBsas.{BsasSummary, ListBsasResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListBsasConnector @Inject()(val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def listBsas(request: ListBsasRequest)(implicit hc: HeaderCarrier,
                                         ec: ExecutionContext,
                                         correlationId: String): Future[DownstreamOutcome[ListBsasResponse[BsasSummary]]] = {

    import request._

    val queryParams = Map(
      "incomeSourceId" -> incomeSourceId,
      "incomeSourceType" -> incomeSourceType
    )

    val mappedQueryParams: Map[String, String] = queryParams.collect {
      case (k: String, Some(v: String)) => (k, v)
    }

    if (taxYear.useTaxYearSpecificApi) {
      get(
        TaxYearSpecificIfsUri[ListBsasResponse[BsasSummary]](s"income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino"),
        mappedQueryParams.toSeq
      )
    } else {
      val mappedQueryParamsWithTaxYear: Map[String, String] = mappedQueryParams ++ Map("taxYear" -> taxYear.asDownstream)
      get(IfsUri[ListBsasResponse[BsasSummary]](s"income-tax/adjustable-summary-calculation/$nino"), mappedQueryParamsWithTaxYear.toSeq)
    }

  }
}
