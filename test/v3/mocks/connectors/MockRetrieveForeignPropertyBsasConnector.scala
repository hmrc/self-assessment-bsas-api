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

package v3.mocks.connectors

import api.connectors.DownstreamOutcome
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import v3.connectors.RetrieveForeignPropertyBsasConnector
import v3.models.request.retrieveBsas.RetrieveForeignPropertyBsasRequestData
import v3.models.response.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasResponse

import scala.concurrent.{ExecutionContext, Future}

trait MockRetrieveForeignPropertyBsasConnector extends MockFactory {

  val mockConnector: RetrieveForeignPropertyBsasConnector = mock[RetrieveForeignPropertyBsasConnector]

  object MockRetrieveForeignPropertyBsasConnector {

    def retrieveForeignPropertyBsas(
        requestData: RetrieveForeignPropertyBsasRequestData): CallHandler[Future[DownstreamOutcome[RetrieveForeignPropertyBsasResponse]]] = {
      (mockConnector
        .retrieveForeignPropertyBsas(_: RetrieveForeignPropertyBsasRequestData)(_: HeaderCarrier, _: ExecutionContext, _: String))
        .expects(requestData, *, *, *)
    }
  }
}
