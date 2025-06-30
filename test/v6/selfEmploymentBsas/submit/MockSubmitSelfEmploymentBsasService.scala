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

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import shared.controllers.RequestContext
import shared.services.ServiceOutcome
import v6.selfEmploymentBsas.submit.model.request.SubmitSelfEmploymentBsasRequestData
import org.scalatest.TestSuite
import scala.concurrent.{ExecutionContext, Future}

trait MockSubmitSelfEmploymentBsasService extends TestSuite with MockFactory {

  val mockService: SubmitSelfEmploymentBsasService = mock[SubmitSelfEmploymentBsasService]

  object MockSubmitSelfEmploymentBsasService {

    def submitSelfEmploymentBsas(requestData: SubmitSelfEmploymentBsasRequestData): CallHandler[Future[ServiceOutcome[Unit]]] = {
      (mockService
        .submitSelfEmploymentBsas(_: SubmitSelfEmploymentBsasRequestData)(_: RequestContext, _: ExecutionContext))
        .expects(requestData, *, *)
    }

  }

}
