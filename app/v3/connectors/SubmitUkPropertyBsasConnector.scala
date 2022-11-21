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
import play.api.http.Status.OK

import javax.inject.{ Inject, Singleton }
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import v3.connectors.DownstreamUri.{ DesUri, TaxYearSpecificIfsUri }
import v3.models.request.submitBsas.ukProperty.SubmitUkPropertyBsasRequestData
import v3.connectors.httpparsers.StandardDownstreamHttpParser._

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class SubmitUkPropertyBsasConnector @Inject()(val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def submitPropertyBsas(request: SubmitUkPropertyBsasRequestData)(implicit
                                                                   hc: HeaderCarrier,
                                                                   ec: ExecutionContext,
                                                                   correlationId: String): Future[DownstreamOutcome[Unit]] = {

    val nino          = request.nino.nino
    val calculationId = request.calculationId
    val taxYear       = request.taxYear

    implicit val successCode: SuccessCode = SuccessCode(OK)

    val downstreamUri =
      taxYear match {
        case Some(taxYear) if taxYear.useTaxYearSpecificApi =>
          TaxYearSpecificIfsUri[Unit](s"income-tax/adjustable-summary-calculation/${taxYear.asTysDownstream}/$nino/$calculationId")

        case _ =>
          DesUri[Unit](s"income-tax/adjustable-summary-calculation/$nino/$calculationId")
      }

    put(
      body = request.body,
      uri = downstreamUri
    )
  }
}
