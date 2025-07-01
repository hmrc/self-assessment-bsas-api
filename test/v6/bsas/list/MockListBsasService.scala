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

package v6.bsas.list

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import shared.controllers.RequestContext
import shared.services.ServiceOutcome
import v6.bsas.list.model.request.ListBsasRequestData
import v6.bsas.list.model.response.ListBsasResponse
import org.scalatest.TestSuite
import scala.concurrent.{ExecutionContext, Future}

trait MockListBsasService extends TestSuite with MockFactory {

  val mockListBsasService: ListBsasService = mock[ListBsasService]

  object MockListBsasService {

    def listBsas(requestData: ListBsasRequestData): CallHandler[Future[ServiceOutcome[ListBsasResponse]]] = {
      (mockListBsasService
        .listBsas(_: ListBsasRequestData)(_: RequestContext, _: ExecutionContext))
        .expects(requestData, *, *)
    }

  }

}
