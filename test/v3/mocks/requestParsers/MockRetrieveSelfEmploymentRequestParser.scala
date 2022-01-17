/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.mocks.requestParsers

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import v2.controllers.requestParsers.RetrieveSelfEmploymentRequestParser
import v2.models.errors.ErrorWrapper
import v2.models.request.{RetrieveSelfEmploymentBsasRawData, RetrieveSelfEmploymentBsasRequestData}

trait MockRetrieveSelfEmploymentRequestParser extends MockFactory {

  val mockRequestParser: RetrieveSelfEmploymentRequestParser = mock[RetrieveSelfEmploymentRequestParser]

  object MockRetrieveSelfEmploymentRequestParser {

    def parse(data: RetrieveSelfEmploymentBsasRawData): CallHandler[Either[ErrorWrapper, RetrieveSelfEmploymentBsasRequestData]] = {
      (mockRequestParser.parseRequest(_: RetrieveSelfEmploymentBsasRawData)(_: String)).expects(data, *)
    }
  }
}
