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

import api.models.domain.{ BusinessId, Nino, TaxYear }
import api.models.errors._
import support.UnitSpec
import v3.models.request.ListBsasRequestData

class ListBsasValidatorFactorySpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino                = "AA123456B"
  private val validTaxYear             = "2019-20"
  private val validTypeOfBusinessSE    = "self-employment"
  private val validTypeOfBusinessUkFhl = "uk-property-fhl"
  private val validBusinessId          = "XAIS12345678901"

  private val parsedNino                = Nino(validNino)
  private val parsedTaxYear             = TaxYear.fromMtd(validTaxYear)
  private val parsedTypeOfBusinessSE    = "01"
  private val parsedTypeOfBusinessUkFhl = "04"
  private val parsedBusinessId          = BusinessId(validBusinessId)

  val validatorFactory = new ListBsasValidatorFactory

  private def validator(nino: String, taxYear: Option[String], typeOfBusiness: Option[String], businessId: Option[String]) =
    validatorFactory.validator(nino, taxYear, typeOfBusiness, businessId)

  "validator" should {
    "return the parsed domain object" when {
      "passed a valid request with a Tax Year, 'self-employment' Type of Business and Business ID" in {
        val result = validator(validNino, Some(validTaxYear), Some(validTypeOfBusinessSE), Some(validBusinessId)).validateAndWrapResult()
        result shouldBe Right(ListBsasRequestData(parsedNino, parsedTaxYear, Some(parsedBusinessId), Some(parsedTypeOfBusinessSE)))
      }

      "passed a valid request with a Tax Year, 'uk property fhl' Type of Business and Business ID" in {
        val result = validator(validNino, Some(validTaxYear), Some(validTypeOfBusinessUkFhl), Some(validBusinessId)).validateAndWrapResult()
        result shouldBe Right(ListBsasRequestData(parsedNino, parsedTaxYear, Some(parsedBusinessId), Some(parsedTypeOfBusinessUkFhl)))
      }

      "passed a valid request with a Tax Year but no Type of Business or Business ID" in {
        val result = validator(validNino, Some(validTaxYear), None, None).validateAndWrapResult()
        result shouldBe Right(ListBsasRequestData(parsedNino, parsedTaxYear, None, None))
      }

      "passed a valid request with no query parameters" in {
        withClue("If no taxYear is sent, the default is the current tax year for 'today'") {
          val result = validator(validNino, None, None, None).validateAndWrapResult()
          result shouldBe Right(ListBsasRequestData(parsedNino, TaxYear.currentTaxYear(), None, None))
        }
      }
    }

    "return a single error" when {
      "passed an invalid nino" in {
        val result = validator("A12344A", Some(validTaxYear), None, None).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }

      "passed a tax year range of more than one year" in {
        val result = validator(validNino, Some("2018-20"), None, None).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }

      "passed an invalid taxYear (i.e. earlier than 2023-24)" in {
        val result = validator(validNino, Some("2018-19"), None, None).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearNotSupportedError)
        )
      }

      "an invalid type of business is provided" in {
        val result = validator(validNino, Some(validTaxYear), Some("not-a-type-of-business"), None).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TypeOfBusinessFormatError)
        )
      }

      "an invalid business id is provided" in {
        val result = validator(validNino, Some(validTaxYear), None, Some("not-a-business-id")).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, BusinessIdFormatError)
        )
      }
    }

    "return multiple errors" when {
      "multiple invalid parameters are provided" in {
        val result = validator("not-a-nino", None, Some("not-a-type-of-business"), None).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(NinoFormatError, TypeOfBusinessFormatError))
          )
        )
      }
    }
  }
}
