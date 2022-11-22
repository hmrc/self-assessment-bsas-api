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

import domain.Nino
import support.UnitSpec
import v3.mocks.validators.MockRetrieveForeignPropertyValidator
import v3.models.domain.TaxYear
import v3.models.errors.{BadRequestError, CalculationIdFormatError, ErrorWrapper, InvalidTaxYearParameterError, NinoFormatError, RuleTaxYearRangeInvalidError, TaxYearFormatError}
import v3.models.request.retrieveBsas.foreignProperty.{RetrieveForeignPropertyBsasRawData, RetrieveForeignPropertyBsasRequestData}

class RetrieveForeignPropertyRequestDataParserSpec extends UnitSpec {

  trait Test extends MockRetrieveForeignPropertyValidator {
    lazy val parser = new RetrieveForeignPropertyRequestParser(mockValidator)
  }

  val nino = "AA123456A"
  val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  val inputRawData = RetrieveForeignPropertyBsasRawData(nino, calculationId, taxYear=None)
  val outputRequestData = RetrieveForeignPropertyBsasRequestData(Nino(nino), calculationId, taxYear=None)

  val TysInputRawData = RetrieveForeignPropertyBsasRawData(nino, calculationId, Some("2023-24"))
  val TysOutputRequestData = RetrieveForeignPropertyBsasRequestData(Nino(nino), calculationId, Some(TaxYear.fromMtd("2023-24")))

  "parser" should {
    "return a valid request object" when {
      "passed a valid raw data object" in new Test {
        MockValidator.validate(inputRawData).returns(List())
        parser.parseRequest(inputRawData) shouldBe Right(outputRequestData)
      }
      "passed a valid TYS raw data object" in new Test {
        MockValidator.validate(TysInputRawData).returns(List())
        parser.parseRequest(TysInputRawData) shouldBe Right(TysOutputRequestData)
      }
    }
    "return a single error" when {
      "a single error is thrown in the validation" in new Test {
        MockValidator.validate(inputRawData).returns(List(NinoFormatError))
        parser.parseRequest(inputRawData) shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }
      "a single error is thrown in the validation for a TYS request" in new Test {
        MockValidator.validate(TysInputRawData).returns(List(NinoFormatError))
        parser.parseRequest(TysInputRawData) shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }
    }
    "return multiple errors" when {
      "multiple errors are thrown in the validation" in new Test {
        MockValidator.validate(inputRawData).returns(List(NinoFormatError, CalculationIdFormatError))
        parser.parseRequest(inputRawData) shouldBe Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, CalculationIdFormatError))))
      }
      "multiple errors are thrown in the validation for a TYS request" in new Test {
        MockValidator.validate(TysInputRawData).returns(List(NinoFormatError,CalculationIdFormatError))
        parser.parseRequest(TysInputRawData) shouldBe Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, CalculationIdFormatError))))
      }
    }
    "return the relevant taxYear error" when {
      "passed an invalid tax year" in new Test {
        val invalidTaxYear = RetrieveForeignPropertyBsasRawData(nino, calculationId, Some("2022-23"))
        MockValidator.validate(invalidTaxYear).returns(List(InvalidTaxYearParameterError))
        parser.parseRequest(invalidTaxYear) shouldBe Left(ErrorWrapper(correlationId, InvalidTaxYearParameterError))
      }
      "passed an incorrectly formatted tax year" in new Test {
        val invalidFormatTaxYear = RetrieveForeignPropertyBsasRawData(nino, calculationId, Some("2022"))
        MockValidator.validate(invalidFormatTaxYear).returns(List(TaxYearFormatError))
        parser.parseRequest(invalidFormatTaxYear) shouldBe Left(ErrorWrapper(correlationId, TaxYearFormatError))
      }
      "passed an invalid range of tax years" in new Test {
        val invalidRangeTaxYear = RetrieveForeignPropertyBsasRawData(nino, calculationId, Some("2023-26"))
        MockValidator.validate(invalidRangeTaxYear).returns(List(RuleTaxYearRangeInvalidError))
        parser.parseRequest(invalidRangeTaxYear) shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError))
      }
    }
  }

}
