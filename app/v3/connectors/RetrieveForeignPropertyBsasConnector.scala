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

import javax.inject.{ Inject, Singleton }
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import v3.connectors.DownstreamUri.{ IfsUri, TaxYearSpecificIfsUri }
import v3.models.request.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasRequestData
import v3.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasResponse

import scala.concurrent.{ ExecutionContext, Future }
import v3.connectors.httpparsers.StandardDownstreamHttpParser._


@Singleton
class RetrieveForeignPropertyBsasConnector @Inject()(val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def retrieveForeignPropertyBsas(request: RetrieveForeignPropertyBsasRequestData)(
      implicit hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrieveForeignPropertyBsasResponse]] = {

    import request._

    val url = taxYear match {
      case Some(ty) if ty.useTaxYearSpecificApi =>
        TaxYearSpecificIfsUri[RetrieveForeignPropertyBsasResponse](
          s"income-tax/adjustable-summary-calculation/${ty.asTysDownstream}/${nino.nino}/${request.calculationId}")
      case _ => IfsUri[RetrieveForeignPropertyBsasResponse](s"income-tax/adjustable-summary-calculation/${nino.nino}/${request.calculationId}")
    }

    get(url)

  }
}
