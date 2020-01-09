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

package v1.controllers.requestParsers

import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v1.mocks.validators.MockRetrieveAdjustmentsValidator
import v1.models.errors.{BadRequestError, BsasIdFormatError, ErrorWrapper, NinoFormatError}
import v1.models.request.{RetrieveAdjustmentsRawData, RetrieveAdjustmentsRequestData}

class RetrieveAdjustmentsRequestParserSpec extends UnitSpec {

  trait Test extends MockRetrieveAdjustmentsValidator {
    lazy val parser = new RetrieveAdjustmentsRequestParser(mockValidator)
  }

  val nino = "AA123456A"
  val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val adjustedStatus = Some("true")

  val inputRawData = RetrieveAdjustmentsRawData(nino, bsasId)
  val outputRequestData = RetrieveAdjustmentsRequestData(Nino(nino), bsasId)

  "parser" should {
    "return a valid request object" when {
      "passed a valid raw data object" in new Test {
        val input: RetrieveAdjustmentsRawData = inputRawData
        MockValidator.validate(input).returns(List())
        parser.parseRequest(input) shouldBe Right(outputRequestData)
      }
    }
    "return a single error" when {
      "a single error is thrown in the validation" in new Test {
        MockValidator.validate(inputRawData).returns(List(NinoFormatError))
        parser.parseRequest(inputRawData) shouldBe Left(ErrorWrapper(None, NinoFormatError))
      }
    }
    "return multiple errors" when {
      "a multiple errors are thrown in the validation" in new Test {
        MockValidator.validate(inputRawData).returns(List(NinoFormatError, BsasIdFormatError))
        parser.parseRequest(inputRawData) shouldBe Left(ErrorWrapper(None, BadRequestError, Some(Seq(NinoFormatError, BsasIdFormatError))))
      }
    }
  }

}
