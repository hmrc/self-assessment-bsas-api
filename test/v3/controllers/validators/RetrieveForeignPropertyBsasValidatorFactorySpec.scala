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

package v3.controllers.validators

import api.models.domain.{ CalculationId, Nino, TaxYear }
import api.models.errors._
import support.UnitSpec
import v3.models.request.retrieveBsas.RetrieveForeignPropertyBsasRequestData

class RetrieveForeignPropertyBsasValidatorFactorySpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  val validNino          = "AA123456A"
  val validCalculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val validTaxYear       = "2023-24"

  private val parsedNino          = Nino(validNino)
  private val parsedCalculationId = CalculationId(validCalculationId)
  private val parsedTaxYear       = TaxYear.fromMtd(validTaxYear)

  val validatorFactory = new RetrieveForeignPropertyBsasValidatorFactory

  private def validator(nino: String, calculationId: String, taxYear: Option[String]) =
    validatorFactory.validator(nino, calculationId, taxYear)

  "validator" should {
    "return the parsed domain object" when {
      "passed a valid request" in {
        val result = validator(validNino, validCalculationId, None).validateAndWrapResult()

        result shouldBe Right(
          RetrieveForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, None)
        )
      }

      "passed a valid TYS request" in {
        val result = validator(validNino, validCalculationId, Some(validTaxYear)).validateAndWrapResult()

        result shouldBe Right(
          RetrieveForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, Some(parsedTaxYear))
        )
      }
    }

    "return a single error" when {
      "passed an invalid nino" in {
        val result = validator("A12344A", validCalculationId, None).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }

      "passed an invalid calculation id" in {
        val result = validator(validNino, "not-a-calculation-id", None).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, CalculationIdFormatError)
        )
      }
    }

    "return TYS errors for an invalid TYS request" when {
      "passed an invalid taxYear (i.e. earlier than 2023-24)" in {
        val result = validator(validNino, validCalculationId, Some("2022-23")).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, InvalidTaxYearParameterError)
        )
      }

      "passed an incorrectly formatted taxYear" in {
        val result = validator(validNino, validCalculationId, Some("202324")).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }

      "passed a tax year range of more than one year" in {
        val result = validator(validNino, validCalculationId, Some("2022-24")).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return multiple errors" when {
      "passed multiple invalid fields" in {
        val result = validator("not-a-nino", "not-a-calculation-id", None).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(NinoFormatError, CalculationIdFormatError))
          )
        )
      }
    }
  }
}