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

package v5.ukPropertyBsas.retrieve

import shared.config.{ConfigFeatureSwitches, SharedAppConfig}
import shared.connectors.DownstreamUri.{HipUri, IfsUri, TaxYearSpecificIfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import shared.models.domain.TaxYear
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v5.ukPropertyBsas.retrieve.model.request.RetrieveUkPropertyBsasRequestData
import v5.ukPropertyBsas.retrieve.model.response.RetrieveUkPropertyBsasResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveUkPropertyBsasConnector @Inject() (val http: HttpClient, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def retrieve(request: RetrieveUkPropertyBsasRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrieveUkPropertyBsasResponse]] = {

    import request._
    import schema._

    def downstreamUri1876(taxYear: TaxYear): DownstreamUri[DownstreamResp] = if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1876")) {
      HipUri(s"itsa/income-tax/v1/${taxYear.asTysDownstream}/adjustable-summary-calculation/$nino/$calculationId")
    } else {
      TaxYearSpecificIfsUri(s"income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino/$calculationId")
    }

    lazy val downstreamUri1516: DownstreamUri[DownstreamResp] = IfsUri(s"income-tax/adjustable-summary-calculation/$nino/$calculationId")

    val downstreamUri: DownstreamUri[DownstreamResp] = taxYear match {
      case Some(ty) if ty.useTaxYearSpecificApi => downstreamUri1876(ty)
      case _                                    => downstreamUri1516
    }

    get(downstreamUri)

  }

}
