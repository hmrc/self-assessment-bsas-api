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

package v2.controllers.requestParsers

import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v2.fixtures.TriggerBsasRequestBodyFixtures._
import v2.mocks.validators.MockTriggerBSASValidator
import v2.models.errors._
import v2.models.request.triggerBsas.{TriggerBsasRawData, TriggerBsasRequest}

class TriggerBsasRequestParserSpec extends UnitSpec {

  val nino = "AA123456A"

  trait Test extends MockTriggerBSASValidator {
    lazy val parser = new TriggerBsasRequestParser(mockValidator)
  }

  "parser" should {
    "parse request" when {
      s"passed valid input" in new Test {
        val inputData = TriggerBsasRawData(nino, triggerBsasRawDataBody())


        MockValidator
          .validate(inputData)
          .returns(Nil)

        private val result = parser.parseRequest(inputData)
        result shouldBe Right(TriggerBsasRequest(Nino(nino), triggerBsasRequestDataBody()))
      }
    }

    "reject invalid input" when {
      "a single error is present" in new Test {
        val inputData = TriggerBsasRawData(nino, AnyContentAsJson(Json.obj()))


        MockValidator
          .validate(inputData)
          .returns(List(RuleIncorrectOrEmptyBodyError))

        private val result = parser.parseRequest(inputData)
        result shouldBe Left(ErrorWrapper(None, RuleIncorrectOrEmptyBodyError, None))
      }

      "multiple errors are present" in new Test {
        val inputData = TriggerBsasRawData(nino, triggerBsasRawDataBody())


        MockValidator
          .validate(inputData)
          .returns(List(BusinessIdFormatError, RuleEndBeforeStartDateError))

        private val result = parser.parseRequest(inputData)
        result shouldBe Left(ErrorWrapper(None, BadRequestError, Some(Seq(BusinessIdFormatError, RuleEndBeforeStartDateError))))
      }
    }
  }
}



