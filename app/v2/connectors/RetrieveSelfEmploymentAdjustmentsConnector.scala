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
import v2.models.request.RetrieveAdjustmentsRequestData
import v2.models.response.retrieveBsasAdjustments.selfEmployment.RetrieveSelfEmploymentAdjustmentsResponse

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveSelfEmploymentAdjustmentsConnector @Inject()(val http: HttpClient,
                                                          val appConfig: AppConfig) extends BaseDesConnector {

  def retrieveSelfEmploymentAdjustments(request: RetrieveAdjustmentsRequestData)(
                                       implicit hc: HeaderCarrier,
                                       ec: ExecutionContext): Future[DesOutcome[RetrieveSelfEmploymentAdjustmentsResponse]] = {

    import v2.connectors.httpparsers.StandardDesHttpParser._

    val nino = request.nino.nino
    val bsasId = request.bsasId

    get(
      DesUri[RetrieveSelfEmploymentAdjustmentsResponse](s"income-tax/adjustable-summary-calculation/$nino/$bsasId"), queryParams = Seq("return" -> "2")
    )
  }
}
