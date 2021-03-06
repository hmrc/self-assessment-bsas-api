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

package v1.mocks.services

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import v1.models.request.submitBsas.ukProperty.SubmitUKPropertyBsasRequestBody
import v1.services.SubmitUKPropertyBsasNrsProxyService

import scala.concurrent.{ExecutionContext, Future}

trait MockSubmitUKPropertyBsasNrsProxyService extends MockFactory {

  val mockSubmitUKPropertyBsasNrsProxyService: SubmitUKPropertyBsasNrsProxyService = mock[SubmitUKPropertyBsasNrsProxyService]

  object MockSubmitUKPropertyBsasNrsProxyService {
    def submit(nino: String): CallHandler[Future[Unit]] = {
      (mockSubmitUKPropertyBsasNrsProxyService
        .submit(_: String, _: SubmitUKPropertyBsasRequestBody)(_: HeaderCarrier, _: ExecutionContext))
        .expects(nino, *, *, *)
    }
  }

}
