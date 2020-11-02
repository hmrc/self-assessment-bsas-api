/*
 * Copyright 2020 HM Revenue & Customs
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
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import v2.models.request.triggerBsas.TriggerBsasRequest
import v2.models.response.TriggerBsasResponse

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerBsasConnector @Inject()(val http: HttpClient,
                                     val appConfig: AppConfig) extends BaseDesConnector {

  def triggerBsas(request: TriggerBsasRequest)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    correlationId: String): Future[DesOutcome[TriggerBsasResponse]] = {

    import v2.connectors.httpparsers.StandardDesHttpParser._

    val nino = request.nino.nino

    post(
      body = request.body,
      DesUri[TriggerBsasResponse](s"income-tax/adjustable-summary-calculation/$nino")
    )
  }
}
