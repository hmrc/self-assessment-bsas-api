/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.selfEmploymentBsas.retrieve

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import shared.controllers.RequestContext
import shared.services.ServiceOutcome
import v7.selfEmploymentBsas.retrieve.model.request.RetrieveSelfEmploymentBsasRequestData
import v7.selfEmploymentBsas.retrieve.model.response.RetrieveSelfEmploymentBsasResponse

import scala.concurrent.{ExecutionContext, Future}

trait MockRetrieveSelfEmploymentBsasService extends TestSuite with MockFactory {

  val mockService: RetrieveSelfEmploymentBsasService = mock[RetrieveSelfEmploymentBsasService]

  object MockRetrieveSelfEmploymentBsasService {

    def retrieveBsas(requestData: RetrieveSelfEmploymentBsasRequestData): CallHandler[Future[ServiceOutcome[RetrieveSelfEmploymentBsasResponse]]] = {
      (mockService
        .retrieveSelfEmploymentBsas(_: RetrieveSelfEmploymentBsasRequestData)(_: RequestContext, _: ExecutionContext))
        .expects(requestData, *, *)
    }

  }

}
