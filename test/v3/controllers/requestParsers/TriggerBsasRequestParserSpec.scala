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

package v3.controllers.requestParsers

import api.models.domain.Nino
import api.models.errors._
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import v3.fixtures.TriggerBsasRequestBodyFixtures._
import v3.mocks.validators.MockTriggerBsasValidator
import v3.models.request.triggerBsas.{TriggerBsasRawData, TriggerBsasRequest}

class TriggerBsasRequestParserSpec extends UnitSpec {

  val nino = "AA123456A"
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  trait Test extends MockTriggerBsasValidator {
    lazy val parser = new TriggerBsasRequestParser(mockValidator)
  }

  "parser" should {
    "parse request" when {
      s"passed valid input" in new Test {
        val inputData: TriggerBsasRawData = TriggerBsasRawData(nino, triggerBsasRawDataBody())

        MockValidator
          .validate(inputData)
          .returns(Nil)

        private val result = parser.parseRequest(inputData)
        result shouldBe Right(TriggerBsasRequest(Nino(nino), triggerBsasRequestDataBody()))
      }
    }

    "reject invalid input" when {
      "a single error is present" in new Test {
        val inputData: TriggerBsasRawData = TriggerBsasRawData(nino, AnyContentAsJson(Json.obj()))

        MockValidator
          .validate(inputData)
          .returns(List(RuleIncorrectOrEmptyBodyError))

        private val result = parser.parseRequest(inputData)
        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError, None))
      }

      "multiple errors are present" in new Test {
        val inputData: TriggerBsasRawData = TriggerBsasRawData(nino, triggerBsasRawDataBody())

        MockValidator
          .validate(inputData)
          .returns(List(BusinessIdFormatError, RuleEndBeforeStartDateError))

        private val result = parser.parseRequest(inputData)
        result shouldBe Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(BusinessIdFormatError, RuleEndBeforeStartDateError))))
      }
    }
  }
}
