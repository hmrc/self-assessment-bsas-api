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

package v6.selfEmploymentBsas.retrieve.def1

import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors._
import shared.utils.UnitSpec
import v6.selfEmploymentBsas.retrieve.def1.model.request.Def1_RetrieveSelfEmploymentBsasRequestData

class Def1_RetrieveSelfEmploymentBsasValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino          = "AA123456A"
  private val validCalculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val validTaxYear       = "2023-24"

  private val parsedNino          = Nino(validNino)
  private val parsedCalculationId = CalculationId(validCalculationId)
  private val parsedTaxYear       = TaxYear.fromMtd(validTaxYear)

  private def validator(nino: String, calculationId: String, taxYear: String) =
    new Def1_RetrieveSelfEmploymentBsasValidator(nino, calculationId, taxYear)

  "validator" should {
    "return the parsed domain object" when {
      "passed a valid non-TYS request" in {
        val result = validator(validNino, validCalculationId, validTaxYear).validateAndWrapResult()

        result shouldBe Right(
          Def1_RetrieveSelfEmploymentBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear)
        )
      }

      "passed a valid TYS request" in {
        val result = validator(validNino, validCalculationId, validTaxYear).validateAndWrapResult()

        result shouldBe Right(
          Def1_RetrieveSelfEmploymentBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear)
        )
      }
    }

    "return a single error" when {
      "passed an invalid nino" in {
        val result = validator("A12344A", validCalculationId, validTaxYear).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }

      "passed an invalid calculation id" in {
        val result = validator(validNino, "12345", validTaxYear).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, CalculationIdFormatError)
        )
      }
    }

    "return TYS errors for an invalid TYS request" when {
      "passed an incorrectly formatted taxYear" in {
        val result = validator(validNino, validCalculationId, "202324").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }

      "passed a tax year range of more than one year" in {
        val result = validator(validNino, validCalculationId, "2022-24").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return multiple errors" when {
      "passed multiple invalid fields" in {
        val result = validator("not-a-nino", "not-a-calculation-id", validTaxYear).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(CalculationIdFormatError, NinoFormatError))
          )
        )
      }
    }
  }

}
