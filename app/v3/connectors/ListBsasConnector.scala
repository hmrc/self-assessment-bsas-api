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
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import v3.models.request.ListBsasRequest
import v3.models.response.listBsas.{BsasEntries, ListBsasResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListBsasConnector @Inject()(val http: HttpClient,
                                  val appConfig: AppConfig) extends BaseDownstreamConnector {

  def listBsas(request: ListBsasRequest)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    correlationId: String): Future[DownstreamOutcome[ListBsasResponse[BsasEntries]]] = {

    import v3.connectors.httpparsers.StandardDesHttpParser._

    val nino = request.nino.nino

    val queryParams = Map(
      "taxYear" -> Some(request.taxYear.toString),
      "incomeSourceId" -> request.incomeSourceId,
      "incomeSourceType" -> request.incomeSourceType
    )

    def queryMap[A](as: Map[String, A]): Map[String, String] = as.collect {
      case (k: String, Some(v: String)) => (k, v)
    }

    val mappedQueryParams: Map[String, String] = queryMap(queryParams)

    get(
      DownstreamUri[ListBsasResponse[BsasEntries]](s"income-tax/adjustable-summary-calculation/$nino"), mappedQueryParams.toSeq
    )
  }
}