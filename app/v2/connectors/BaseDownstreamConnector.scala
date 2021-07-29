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
import uk.gov.hmrc.http.{ HeaderCarrier, HttpReads, HttpClient }

import scala.concurrent.{ ExecutionContext, Future }

trait BaseDownstreamConnector {
  val http: HttpClient
  val appConfig: AppConfig

  val logger: Logger = Logger(this.getClass)

  lazy val downstreamService: DownstreamService = if (appConfig.ifsEnabled) {
    DownstreamService(appConfig.ifsBaseUrl, appConfig.ifsEnv, appConfig.ifsToken, appConfig.ifsEnvironmentHeaders)
  } else {
    DownstreamService(appConfig.desBaseUrl, appConfig.desEnv, appConfig.desToken, appConfig.desEnvironmentHeaders)
  }

  private[connectors] def downstreamHeaderCarrier(additionalHeaders: Seq[String] = Seq.empty)
                                                 (implicit hc: HeaderCarrier, correlationId: String): HeaderCarrier =
    HeaderCarrier(
      extraHeaders = hc.extraHeaders ++
        // Contract headers
        Seq(
          "Authorization" -> s"Bearer ${downstreamService.token}",
          "Environment" -> downstreamService.environment,
          "CorrelationId" -> correlationId
        ) ++
        // Other headers (i.e Gov-Test-Scenario, Content-Type)
        hc.headers(additionalHeaders ++ downstreamService.environmentHeaders.getOrElse(Seq.empty))
    )

  def put[Body: Writes, Resp](body: Body, uri: DownstreamUri[Resp])(implicit ec: ExecutionContext,
                                                                    hc: HeaderCarrier,
                                                                    httpReads: HttpReads[DownstreamOutcome[Resp]],
                                                                    correlationId: String): Future[DownstreamOutcome[Resp]] = {
    def doPut(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
      http.PUT(s"${downstreamService.baseUrl}/${uri.value}", body)
    }

    doPut(downstreamHeaderCarrier(Seq("Content-Type")))
  }

  def post[Body: Writes, Resp](body: Body, uri: DownstreamUri[Resp])(implicit ec: ExecutionContext,
                                                                     hc: HeaderCarrier,
                                                                     httpReads: HttpReads[DownstreamOutcome[Resp]],
                                                                     correlationId: String): Future[DownstreamOutcome[Resp]] = {

    def doPost(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
      http.POST(s"${downstreamService.baseUrl}/${uri.value}", body)
    }

    doPost(downstreamHeaderCarrier(Seq("Content-Type")))
  }

  def get[Resp](uri: DownstreamUri[Resp])(implicit ec: ExecutionContext,
                                          hc: HeaderCarrier,
                                          httpReads: HttpReads[DownstreamOutcome[Resp]],
                                          correlationId: String): Future[DownstreamOutcome[Resp]] = {

    def doGet(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] =
      http.GET(s"${downstreamService.baseUrl}/${uri.value}")

    doGet(downstreamHeaderCarrier())
  }

  def get[Resp](uri: DownstreamUri[Resp], queryParams: Seq[(String, String)])(implicit ec: ExecutionContext,
                                                                              hc: HeaderCarrier,
                                                                              httpReads: HttpReads[DownstreamOutcome[Resp]],
                                                                              correlationId: String): Future[DownstreamOutcome[Resp]] = {

    def doGet(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
      http.GET(s"${downstreamService.baseUrl}/${uri.value}", queryParams)
    }

    doGet(downstreamHeaderCarrier())
  }
}
