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

package v4.mocks.services

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import shared.controllers.RequestContext
import shared.services.ServiceOutcome
import v4.models.request.submitBsas.foreignProperty.SubmitForeignPropertyBsasRequestData
import v4.services.SubmitForeignPropertyBsasService

import scala.concurrent.{ExecutionContext, Future}

trait MockSubmitForeignPropertyBsasService extends MockFactory {

  val mockService: SubmitForeignPropertyBsasService = mock[SubmitForeignPropertyBsasService]

  object MockSubmitForeignPropertyBsasService {

    def submitForeignPropertyBsas(requestData: SubmitForeignPropertyBsasRequestData): CallHandler[Future[ServiceOutcome[Unit]]] = {
      (mockService
        .submitForeignPropertyBsas(_: SubmitForeignPropertyBsasRequestData)(_: RequestContext, _: ExecutionContext))
        .expects(requestData, *, *)
    }

  }

}