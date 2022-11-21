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

import javax.inject.{ Inject, Singleton }
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import v3.connectors.DownstreamUri.{ DesUri, TaxYearSpecificIfsUri }
import v3.models.request.ListBsasRequest
import v3.models.response.listBsas.{ BsasSummary, ListBsasResponse }

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class ListBsasConnector @Inject()(val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def listBsas(request: ListBsasRequest)(implicit hc: HeaderCarrier,
                                         ec: ExecutionContext,
                                         correlationId: String): Future[DownstreamOutcome[ListBsasResponse[BsasSummary]]] = {

    import v3.connectors.httpparsers.StandardDownstreamHttpParser._
    import request._

    val commonQueryParams = Map(
      "incomeSourceId"   -> incomeSourceId,
      "incomeSourceType" -> incomeSourceType
    )

    def queryMap[A](as: Map[String, A]): Map[String, String] = as.collect {
      case (k: String, Some(v: String)) => (k, v)
    }

    if (taxYear.useTaxYearSpecificApi) {
      val mappedQueryParams: Map[String, String] = queryMap(commonQueryParams)
      get(
        uri =
          TaxYearSpecificIfsUri[ListBsasResponse[BsasSummary]](s"income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/${nino.nino}"),
        queryParams = mappedQueryParams.toSeq
      )
    } else {
      val additionalQueryParams = Map(
        "taxYear" -> Some(taxYear.asDownstream)
      )
      val mappedQueryParams: Map[String, String] = queryMap(commonQueryParams ++ additionalQueryParams)
      get(uri = DesUri[ListBsasResponse[BsasSummary]](s"income-tax/adjustable-summary-calculation/${nino.nino}"),
          queryParams = mappedQueryParams.toSeq)
    }

  }
}
