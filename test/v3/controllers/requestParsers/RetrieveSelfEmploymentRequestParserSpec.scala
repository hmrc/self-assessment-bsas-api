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

package v3.controllers.requestParsers

import support.UnitSpec
import domain.Nino
import v3.mocks.validators.MockRetrieveSelfEmploymentValidator
import v3.models.errors.{BadRequestError, BsasIdFormatError, ErrorWrapper, NinoFormatError}
import v3.models.request.retrieveBsas.selfEmployment.{RetrieveSelfEmploymentBsasRawData, RetrieveSelfEmploymentBsasRequestData}

class RetrieveSelfEmploymentRequestParserSpec extends UnitSpec {

  trait Test extends MockRetrieveSelfEmploymentValidator {
    lazy val parser = new RetrieveSelfEmploymentRequestParser(mockValidator)
  }

  val nino = "AA123456A"
  val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  val inputRawData = RetrieveSelfEmploymentBsasRawData(nino, calculationId)
  val outputRequestData = RetrieveSelfEmploymentBsasRequestData(Nino(nino), calculationId)

  "parser" should {
    "return a valid request object" when {
      "passed a valid raw data object" in new Test {
        MockValidator.validate(inputRawData).returns(List())
        parser.parseRequest(inputRawData) shouldBe Right(outputRequestData)
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
