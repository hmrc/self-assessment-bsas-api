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

package v1.mocks.connectors

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import v1.connectors.{DesOutcome, SubmitUkPropertyBsasConnector}
import v1.models.request.submitBsas.ukProperty.SubmitUkPropertyBsasRequestData
import v1.models.response.SubmitUkPropertyBsasResponse

import scala.concurrent.{ExecutionContext, Future}

trait MockSubmitUkPropertyBsasConnector extends MockFactory {

  val mockConnector: SubmitUkPropertyBsasConnector = mock[SubmitUkPropertyBsasConnector]

  object MockSubmitUKPropertyBsasConnector {

    def submitUKPropertyBsas(requestData: SubmitUkPropertyBsasRequestData): CallHandler[Future[DesOutcome[SubmitUkPropertyBsasResponse]]] = {
      (mockConnector
        .submitPropertyBsas(_: SubmitUkPropertyBsasRequestData)(_: HeaderCarrier, _: ExecutionContext, _: String))
        .expects(requestData, *, *, *)
    }
  }
}
