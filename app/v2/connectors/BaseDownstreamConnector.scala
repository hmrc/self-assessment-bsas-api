/*
 * Copyright 2021 HM Revenue & Customs
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

import config.AppConfig
import play.api.Logger
import play.api.libs.json.Writes
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{ HeaderCarrier, HttpReads, HttpClient }

import scala.concurrent.{ ExecutionContext, Future }

trait BaseDownstreamConnector {
  val http: HttpClient
  val appConfig: AppConfig

  val logger = Logger(this.getClass)

  lazy val downstreamService: DownstreamService = if (appConfig.ifsEnabled) {
    DownstreamService(appConfig.ifsBaseUrl, appConfig.ifsEnv, appConfig.ifsToken)
  } else {
    DownstreamService(appConfig.desBaseUrl, appConfig.desEnv, appConfig.desToken)
  }

  private[connectors] def desHeaderCarrier(implicit hc: HeaderCarrier, correlationId: String): HeaderCarrier =
    hc.copy(authorization = Some(Authorization(s"Bearer ${downstreamService.token}")))
      .withExtraHeaders("Environment" -> downstreamService.environment, "CorrelationId" -> correlationId)

  def put[Body: Writes, Resp](body: Body, uri: DownstreamUri[Resp])(implicit ec: ExecutionContext,
                                                                    hc: HeaderCarrier,
                                                                    httpReads: HttpReads[DownstreamOutcome[Resp]],
                                                                    correlationId: String): Future[DownstreamOutcome[Resp]] = {
    def doPut(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
      http.PUT(s"${downstreamService.baseUrl}/${uri.value}", body)
    }

    doPut(desHeaderCarrier)
  }

  def post[Body: Writes, Resp](body: Body, uri: DownstreamUri[Resp])(implicit ec: ExecutionContext,
                                                                     hc: HeaderCarrier,
                                                                     httpReads: HttpReads[DownstreamOutcome[Resp]],
                                                                     correlationId: String): Future[DownstreamOutcome[Resp]] = {

    def doPost(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
      http.POST(s"${downstreamService.baseUrl}/${uri.value}", body)
    }

    doPost(desHeaderCarrier)
  }

  def get[Resp](uri: DownstreamUri[Resp])(implicit ec: ExecutionContext,
                                          hc: HeaderCarrier,
                                          httpReads: HttpReads[DownstreamOutcome[Resp]],
                                          correlationId: String): Future[DownstreamOutcome[Resp]] = {

    def doGet(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] =
      http.GET(s"${downstreamService.baseUrl}/${uri.value}")

    doGet(desHeaderCarrier)
  }

  def get[Resp](uri: DownstreamUri[Resp], queryParams: Seq[(String, String)])(implicit ec: ExecutionContext,
                                                                              hc: HeaderCarrier,
                                                                              httpReads: HttpReads[DownstreamOutcome[Resp]],
                                                                              correlationId: String): Future[DownstreamOutcome[Resp]] = {

    def doGet(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
      http.GET(s"${downstreamService.baseUrl}/${uri.value}", queryParams)
    }

    doGet(desHeaderCarrier)
  }
}
