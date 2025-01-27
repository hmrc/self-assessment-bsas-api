/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.bsas.list.def1

import common.errors.TypeOfBusinessFormatError
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.utils.UnitSpec
import v7.bsas.list.def1.model.request.Def1_ListBsasRequestData

class Def1_ListBsasValidatorSpec extends UnitSpec {

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

  private def validator(nino: String, taxYear: String, typeOfBusiness: Option[String], businessId: Option[String]) =
    new Def1_ListBsasValidator(nino, taxYear, typeOfBusiness, businessId)

  "validator" should {
    "return the parsed domain object" when {
      "passed a valid request with a Tax Year, 'self-employment' Type of Business and Business ID" in {
        val result = validator(validNino, validTaxYear, Some(validTypeOfBusinessSE), Some(validBusinessId)).validateAndWrapResult()
        result shouldBe Right(Def1_ListBsasRequestData(parsedNino, parsedTaxYear, Some(parsedBusinessId), Some(parsedTypeOfBusinessSE)))
      }

      "passed a valid request with a Tax Year, 'uk property fhl' Type of Business and Business ID" in {
        val result = validator(validNino, validTaxYear, Some(validTypeOfBusinessUkFhl), Some(validBusinessId)).validateAndWrapResult()
        result shouldBe Right(Def1_ListBsasRequestData(parsedNino, parsedTaxYear, Some(parsedBusinessId), Some(parsedTypeOfBusinessUkFhl)))
      }

      "passed a valid request with a Tax Year but no Type of Business or Business ID" in {
        val result = validator(validNino, validTaxYear, None, None).validateAndWrapResult()
        result shouldBe Right(Def1_ListBsasRequestData(parsedNino, parsedTaxYear, None, None))
      }
    }

    "return a single error" when {
      "passed an invalid nino" in {
        val result = validator("A12344A", validTaxYear, None, None).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }

      "passed a tax year range of more than one year" in {
        val result = validator(validNino, "2018-20", None, None).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }

      "passed an invalid taxYear (i.e. earlier than 2019-20)" in {
        val result = validator(validNino, "2018-19", None, None).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearNotSupportedError)
        )
      }

      "an invalid type of business is provided" in {
        val result = validator(validNino, validTaxYear, Some("not-a-type-of-business"), None).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TypeOfBusinessFormatError)
        )
      }

      "an invalid business id is provided" in {
        val result = validator(validNino, validTaxYear, None, Some("not-a-business-id")).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, BusinessIdFormatError)
        )
      }
    }

    "return multiple errors" when {
      "multiple invalid parameters are provided" in {
        val result = validator("not-a-nino", "2019-20", Some("not-a-type-of-business"), None).validateAndWrapResult()

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
