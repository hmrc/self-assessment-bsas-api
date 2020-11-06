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

package v2.mocks.connectors

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import v2.connectors.{DesOutcome, TriggerBsasConnector}
import v2.models.request.triggerBsas.TriggerBsasRequest
import v2.models.response.TriggerBsasResponse

import scala.concurrent.{ExecutionContext, Future}

trait MockTriggerBsasConnector extends MockFactory {

  val mockConnector: TriggerBsasConnector = mock[TriggerBsasConnector]

  object MockTriggerBsasConnector {

    def triggerBsas(requestData: TriggerBsasRequest): CallHandler[Future[DesOutcome[TriggerBsasResponse]]] = {
      (mockConnector
        .triggerBsas(_: TriggerBsasRequest)(_: HeaderCarrier, _: ExecutionContext, _: String))
        .expects(requestData, *, *, *)
    }
  }
}
