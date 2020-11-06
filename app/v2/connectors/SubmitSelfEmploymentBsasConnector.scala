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

import javax.inject.{Inject, Singleton}

import config.AppConfig
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import v2.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestData
import v2.models.response.SubmitSelfEmploymentBsasResponse

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitSelfEmploymentBsasConnector @Inject()(val http: HttpClient,
                                                  val appConfig: AppConfig) extends BaseDesConnector {

  def submitSelfEmploymentBsas(request: SubmitSelfEmploymentBsasRequestData)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    correlationId: String): Future[DesOutcome[SubmitSelfEmploymentBsasResponse]] = {

    import v2.connectors.httpparsers.StandardDesHttpParser._

    put(
      body = request.body,
      DesUri[SubmitSelfEmploymentBsasResponse](s"income-tax/adjustable-summary-calculation/${request.nino.nino}/${request.bsasId}")
    )
  }
}
