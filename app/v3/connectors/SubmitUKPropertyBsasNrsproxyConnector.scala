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
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import v3.models.request.submitBsas.ukProperty.SubmitUKPropertyBsasRequestBody

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitUKPropertyBsasNrsproxyConnector @Inject()(http: HttpClient,
                                                      appConfig: AppConfig) {

  def submit[T](nino: String, requestBody: SubmitUKPropertyBsasRequestBody)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = {
    implicit val readsEmpty: HttpReads[Unit] = (_: String, _: String, _: HttpResponse) => ()

    http.POST[SubmitUKPropertyBsasRequestBody, Unit](s"${appConfig.mtdNrsProxyBaseUrl}/mtd-api-nrs-proxy/$nino/itsa-annual-adjustment", requestBody)
  }
}