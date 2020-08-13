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

package v2.mocks.services

import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import v2.controllers.EndpointLogContext
import v2.models.errors.ErrorWrapper
import v2.models.outcomes.ResponseWrapper
import v2.models.request.ListBsasRequest
import v2.models.response.listBsas.{BsasEntries, ListBsasResponse}
import v2.services.ListBsasService

import scala.concurrent.{ExecutionContext, Future}

trait MockListBsasService extends MockFactory{

  val mockService: ListBsasService = mock[ListBsasService]

  object MockListBsasService{

    def listBsas(requestData: ListBsasRequest): CallHandler4[ListBsasRequest, HeaderCarrier, ExecutionContext, EndpointLogContext, Future[Either[ErrorWrapper, ResponseWrapper[ListBsasResponse[BsasEntries]]]]] = {
      (mockService
        .listBsas(_: ListBsasRequest)(_: HeaderCarrier, _: ExecutionContext, _: EndpointLogContext))
        .expects(requestData, *, *, *)
    }
  }
}