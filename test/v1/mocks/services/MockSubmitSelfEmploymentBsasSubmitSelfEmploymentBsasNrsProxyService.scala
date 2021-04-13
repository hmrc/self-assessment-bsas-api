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
import v1.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestBody
import v1.services.SubmitSelfEmploymentBsasNrsProxyService

import scala.concurrent.{ExecutionContext, Future}

trait MockSubmitSelfEmploymentBsasSubmitSelfEmploymentBsasNrsProxyService extends MockFactory {

  val mockSubmitSelfEmploymentBsasNrsProxyService: SubmitSelfEmploymentBsasNrsProxyService = mock[SubmitSelfEmploymentBsasNrsProxyService]

  object MockSubmitSelfEmploymentBsasNrsProxyService {
    def submit(nino: String): CallHandler[Future[Unit]] = {
      (mockSubmitSelfEmploymentBsasNrsProxyService
        .submit(_: String, _: SubmitSelfEmploymentBsasRequestBody)(_: HeaderCarrier, _: ExecutionContext))
        .expects(nino, *, *, *)
    }
  }

}
