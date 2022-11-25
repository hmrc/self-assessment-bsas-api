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

package v3.controllers.requestParsers.validators

import support.UnitSpec
import v3.models.errors.{ CalculationIdFormatError, InvalidTaxYearParameterError, NinoFormatError, RuleTaxYearRangeInvalidError, TaxYearFormatError }
import v3.models.request.retrieveBsas.foreignProperty.RetrieveForeignPropertyBsasRawData

class RetrieveForeignPropertyValidatorSpec extends UnitSpec {

  val validNino            = "AA123456A"
  val validCalculationId   = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val validTaxYear         = Some("2023-24")
  val invalidNino          = "WALRUS"
  val invalidCalculationId = "f2fb30e5-4ab6-4a29-b3c1-walrus"

  val validator = new RetrieveForeignPropertyValidator()

  "validator" should {
    "return no errors" when {
      "passed valid raw data for a non-tys request" in {
        val input = RetrieveForeignPropertyBsasRawData(validNino, validCalculationId, taxYear = None)
        validator.validate(input) shouldBe List()
      }
      "passed valid raw data for a tys request" in {
        val input = RetrieveForeignPropertyBsasRawData(validNino, validCalculationId, validTaxYear)
        validator.validate(input) shouldBe List()
      }
    }
    "return a single error" when {
      "passed raw data with an invalid nino" in {
        val input = RetrieveForeignPropertyBsasRawData(invalidNino, validCalculationId, taxYear = None)
        validator.validate(input) shouldBe List(NinoFormatError)
      }
      "passed raw data with an invalid calculation id" in {
        val input = RetrieveForeignPropertyBsasRawData(validNino, invalidCalculationId, taxYear = None)
        validator.validate(input) shouldBe List(CalculationIdFormatError)
      }
    }
    "return TYS errors for an invalid TYS request" when {
      "passed raw data with an invalid taxYear (i.e. earlier than 2023-24)" in {
        val input = RetrieveForeignPropertyBsasRawData(validNino, validCalculationId, Some("2022-23"))
        validator.validate(input) shouldBe List(InvalidTaxYearParameterError)
      }
      "passed raw data with a invalidly formatted taxYear" in {
        val input = RetrieveForeignPropertyBsasRawData(validNino, validCalculationId, Some("2023"))
        validator.validate(input) shouldBe List(TaxYearFormatError)
      }
      "passed raw data with a invalid range of taxYears" in {
        val input = RetrieveForeignPropertyBsasRawData(validNino, validCalculationId, Some("2023-27"))
        validator.validate(input) shouldBe List(RuleTaxYearRangeInvalidError)
      }
    }
    "return multiple errors" when {
      "passed raw data with multiple invalid fields" in {
        val input = RetrieveForeignPropertyBsasRawData(invalidNino, invalidCalculationId, None)
        validator.validate(input) shouldBe List(NinoFormatError, CalculationIdFormatError)
      }
    }
  }
}
