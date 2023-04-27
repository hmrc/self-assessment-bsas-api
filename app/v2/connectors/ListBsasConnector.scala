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

package v2.connectors

import api.connectors.DownstreamUri.DesUri
import api.connectors.{ BaseDownstreamConnector, DownstreamOutcome }
import api.connectors.httpparsers.StandardDownstreamHttpParser._
import config.AppConfig
import uk.gov.hmrc.http.{ HeaderCarrier, HttpClient }
import v2.models.request.ListBsasRequest
import v2.models.response.listBsas.{ BsasEntries, ListBsasResponse }

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class ListBsasConnector @Inject()(val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def listBsas(request: ListBsasRequest)(implicit
                                         hc: HeaderCarrier,
                                         ec: ExecutionContext,
                                         correlationId: String): Future[DownstreamOutcome[ListBsasResponse[BsasEntries]]] = {

    import request._

    val queryParams = Map(
      "taxYear"          -> Some(taxYear.toString),
      "incomeSourceId"   -> incomeSourceId,
      "incomeSourceType" -> incomeSourceType
    )

    def queryMap[A](as: Map[String, A]): Map[String, String] = as.collect {
      case (k: String, Some(v: String)) => (k, v)
    }

    val mappedQueryParams: Map[String, String] = queryMap(queryParams)

    get(
      DesUri[ListBsasResponse[BsasEntries]](s"income-tax/adjustable-summary-calculation/$nino"),
      mappedQueryParams.toSeq
    )
  }
}
