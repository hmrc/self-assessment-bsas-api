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

package v6.foreignPropertyBsas.retrieve

import shared.config.AppConfig
import shared.connectors.DownstreamUri.{IfsUri, TaxYearSpecificIfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v6.foreignPropertyBsas.retrieve.model.request.RetrieveForeignPropertyBsasRequestData
import v6.foreignPropertyBsas.retrieve.model.response.RetrieveForeignPropertyBsasResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveForeignPropertyBsasConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def retrieveForeignPropertyBsas(request: RetrieveForeignPropertyBsasRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrieveForeignPropertyBsasResponse]] = {

    import request._
    import schema._

    val downstreamUri: DownstreamUri[DownstreamResp] = taxYear match {
      case Some(ty) if ty.useTaxYearSpecificApi =>
        TaxYearSpecificIfsUri(s"income-tax/adjustable-summary-calculation/${ty.asTysDownstream}/$nino/$calculationId")
      case _ =>
        IfsUri(s"income-tax/adjustable-summary-calculation/$nino/$calculationId")
    }

    get(downstreamUri)
  }

}
