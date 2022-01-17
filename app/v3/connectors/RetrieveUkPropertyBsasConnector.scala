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
import v2.models.request.RetrieveUkPropertyBsasRequestData
import v2.models.response.retrieveBsas.ukProperty.RetrieveUkPropertyBsasResponse
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveUkPropertyBsasConnector @Inject()(val http: HttpClient,
                                                val appConfig: AppConfig) extends BaseDownstreamConnector {

  def retrieve(request: RetrieveUkPropertyBsasRequestData)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    correlationId: String): Future[DownstreamOutcome[RetrieveUkPropertyBsasResponse]] = {

    import v2.connectors.httpparsers.StandardDesHttpParser._

    val nino = request.nino.nino
    val bsasId = request.bsasId

    val queryParams = Map(
      "return" -> request.adjustedStatus
    )

    def queryMap[A](as: Map[String, A]): Map[String, String] = as.collect {
      case (k: String, Some(v: String)) => (k, v)
    }

    val mappedQueryParams: Map[String, String] = queryMap(queryParams)

    get(
      DownstreamUri[RetrieveUkPropertyBsasResponse](s"income-tax/adjustable-summary-calculation/$nino/$bsasId"), mappedQueryParams.toSeq
    )
  }
}
