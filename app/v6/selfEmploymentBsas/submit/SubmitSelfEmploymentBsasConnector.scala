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

package v6.selfEmploymentBsas.submit

import play.api.http.Status
import shared.config.{ConfigFeatureSwitches, SharedAppConfig}
import shared.connectors.DownstreamUri.{HipUri, IfsUri}
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v6.selfEmploymentBsas.submit.model.request.SubmitSelfEmploymentBsasRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitSelfEmploymentBsasConnector @Inject()(val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def submitSelfEmploymentBsas(request: SubmitSelfEmploymentBsasRequestData)(implicit
                                                                             hc: HeaderCarrier,
                                                                             ec: ExecutionContext,
                                                                             correlationId: String): Future[DownstreamOutcome[Unit]] = {

    import shared.connectors.httpparsers.StandardDownstreamHttpParser.*

    implicit val successCode: SuccessCode = SuccessCode(Status.OK)

    import request.*

    val downstreamUri =
      if (taxYear.useTaxYearSpecificApi) {
        if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1874")) {
          HipUri[Unit](s"itsa/income-tax/v1/${taxYear.asTysDownstream}/adjustable-summary-calculation/$nino/$calculationId")
        } else {
          IfsUri[Unit](s"income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino/$calculationId")
        }
      } else {
        IfsUri[Unit](s"income-tax/adjustable-summary-calculation/$nino/$calculationId")
      }
    put(body, downstreamUri)
  }

}
