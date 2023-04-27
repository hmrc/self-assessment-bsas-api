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

package v2.services

import api.controllers.RequestContext
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.BaseService
import cats.implicits._
import v2.connectors.ListBsasConnector
import v2.models.request.ListBsasRequest
import v2.models.response.listBsas.{ BsasEntries, ListBsasResponse }

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class ListBsasService @Inject()(connector: ListBsasConnector) extends BaseService {

  def listBsas(request: ListBsasRequest)(implicit ctx: RequestContext,
                                         ec: ExecutionContext): Future[Either[ErrorWrapper, ResponseWrapper[ListBsasResponse[BsasEntries]]]] =
    connector
      .listBsas(request)
      .map(_.leftMap(mapDownstreamErrors(errorMap)))

  private val errorMap: Map[String, MtdError] =
    Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "NO_DATA_FOUND"             -> NotFoundError,
      "INVALID_TAXYEAR"           -> InternalError,
      "INVALID_INCOMESOURCEID"    -> InternalError,
      "INVALID_INCOMESOURCE_TYPE" -> InternalError,
      "SERVER_ERROR"              -> InternalError,
      "SERVICE_UNAVAILABLE"       -> InternalError
    )
}
