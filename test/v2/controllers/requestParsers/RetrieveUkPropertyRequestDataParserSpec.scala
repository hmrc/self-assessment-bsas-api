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

package v2.controllers.requestParsers

import support.UnitSpec
import domain.Nino
import v2.mocks.validators.MockRetrieveUkPropertyValidator
import v2.models.errors.{BadRequestError, BsasIdFormatError, ErrorWrapper, NinoFormatError}
import v2.models.request.{RetrieveUkPropertyBsasRawData, RetrieveUkPropertyBsasRequestData}

class RetrieveUkPropertyRequestDataParserSpec extends UnitSpec {

  trait Test extends MockRetrieveUkPropertyValidator {
    lazy val parser = new RetrieveUkPropertyRequestParser(mockValidator)
  }

  val nino = "AA123456A"
  val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val adjustedStatus = Some("true")
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  val inputRawData = RetrieveUkPropertyBsasRawData(nino, bsasId, adjustedStatus)
  val outputRequestData = RetrieveUkPropertyBsasRequestData(Nino(nino), bsasId, Some("3"))

  "parser" should {
    "return a valid request object" when {
      "passed a valid raw data object with an adjusted summary of 'true'" in new Test {
        MockValidator.validate(inputRawData).returns(List())
        parser.parseRequest(inputRawData) shouldBe Right(outputRequestData)
      }
      "passed a valid raw data object with an adjusted summary of 'false'" in new Test {
        val input: RetrieveUkPropertyBsasRawData = inputRawData.copy(adjustedStatus = Some("false"))
        MockValidator.validate(input).returns(List())
        parser.parseRequest(input) shouldBe Right(outputRequestData.copy(adjustedStatus = Some("1")))
      }
      "passed a valid raw data object without an adjusted summary" in new Test {
        val input: RetrieveUkPropertyBsasRawData = inputRawData.copy(adjustedStatus = None)
        MockValidator.validate(input).returns(List())
        parser.parseRequest(input) shouldBe Right(outputRequestData.copy(adjustedStatus = None))
      }
    }
    "return a single error" when {
      "a single error is thrown in the validation" in new Test {
        MockValidator.validate(inputRawData).returns(List(NinoFormatError))
        parser.parseRequest(inputRawData) shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }
    }
    "return multiple errors" when {
      "a multiple errors are thrown in the validation" in new Test {
        MockValidator.validate(inputRawData).returns(List(NinoFormatError, BsasIdFormatError))
        parser.parseRequest(inputRawData) shouldBe Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BsasIdFormatError))))
      }
    }
  }

}
