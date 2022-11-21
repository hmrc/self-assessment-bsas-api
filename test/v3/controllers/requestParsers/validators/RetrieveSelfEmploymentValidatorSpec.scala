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
import v3.models.errors.{CalculationIdFormatError, InvalidTaxYearParameterError, NinoFormatError, RuleTaxYearRangeInvalidError, TaxYearFormatError}
import v3.models.request.retrieveBsas.selfEmployment.RetrieveSelfEmploymentBsasRawData

class RetrieveSelfEmploymentValidatorSpec extends UnitSpec {

  val validNino = "AA123456A"
  val validCalculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val invalidNino = "BEANS"
  val invalidCalculationId = "f2fb30e5-4ab6-4a29-b3c1-beans"

  val validator = new RetrieveSelfEmploymentValidator()

  "validator" should {
    "return no errors" when {
      "passed valid raw data" in {
        val input = RetrieveSelfEmploymentBsasRawData(validNino, validCalculationId, None)
        validator.validate(input) shouldBe List()
      }
      "a valid request with a taxYear is supplied" in {
        val input = RetrieveSelfEmploymentBsasRawData(validNino, validCalculationId, Some("2023-24"))
        validator.validate(input) shouldBe List()
      }
    }
    "return a single error" when {
      "passed raw data with an invalid nino" in {
        val input = RetrieveSelfEmploymentBsasRawData(invalidNino, validCalculationId, None)
        validator.validate(input) shouldBe List(NinoFormatError)
      }
      "passed raw data with an invalid calculation id" in {
        val input = RetrieveSelfEmploymentBsasRawData(validNino, invalidCalculationId, None)
        validator.validate(input) shouldBe List(CalculationIdFormatError)
      }
    }
    "return multiple errors" when {
      "passed raw data with multiple invalid fields" in {
        val input = RetrieveSelfEmploymentBsasRawData(invalidNino, invalidCalculationId, None)
        validator.validate(input) shouldBe List(NinoFormatError, CalculationIdFormatError)
      }
    }
    "return InvalidTaxYearParameterError error" when {
      "a tax year before TYS is suppled" in {
        val input = RetrieveSelfEmploymentBsasRawData(validNino, validCalculationId, Some("2022-23"))

        validator.validate(input) shouldBe
          List(InvalidTaxYearParameterError)
      }
    }

    "return TaxYearFormatError error" when {
      "a badly formatted tax year is suppled" in {
        val input = RetrieveSelfEmploymentBsasRawData(validNino, validCalculationId, Some("BAD_TAX_YEAR"))

        validator.validate(input) shouldBe
          List(TaxYearFormatError)
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "a tax year range of more than one year is supplied" in {
        val input = RetrieveSelfEmploymentBsasRawData(validNino, validCalculationId, Some("2022-24"))

        validator.validate(input) shouldBe
          List(RuleTaxYearRangeInvalidError)
      }
    }

  }

}
