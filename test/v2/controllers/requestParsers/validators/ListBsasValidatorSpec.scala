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

package v2.controllers.requestParsers.validators

import support.UnitSpec
import v2.models.errors._
import v2.models.request.ListBsasRawData

class ListBsasValidatorSpec extends UnitSpec {

  val validator = new ListBsasValidator()
  private val nino = "AA123456B"
  private val taxYear = "2019-20"
  private val typeOfBusiness = "uk-property-fhl"
  private val businessId = "XAIS12345678901"
  private val invalidNino = "not a nino"
  private val invalidTaxYear = "2018-20"
  private val tooEarlyTaxYear = "2018-19"
  private val invalidTypeOfBusiness = "toothpicks-for-hamsters"
  private val invalidBusinessId = "Not a businessId"
  private val rawData: ListBsasRawData = ListBsasRawData(nino, Some(taxYear), Some(typeOfBusiness), Some(businessId))

  "running the validator" should {
    "return no errors" when {
      "valid parameters are provided with a tax year" in {
        validator.validate(rawData) shouldBe Nil
      }

      "a valid request is supplied without a typeOfBusiness or businessId" in {
        validator.validate(rawData.copy(typeOfBusiness = None, businessId = None)) shouldBe Nil
      }
    }

    "return one error" when {
      "an invalid nino is provided" in {
        validator.validate(rawData.copy(nino = invalidNino)) shouldBe List(NinoFormatError)
      }
      "an invalid tax year is provided" in {
        validator.validate(rawData.copy(taxYear = Some(invalidTaxYear))) shouldBe List(RuleTaxYearRangeInvalidError)
      }
      "a too early tax year is provided" in {
        validator.validate(rawData.copy(taxYear = Some(tooEarlyTaxYear))) shouldBe List(RuleTaxYearNotSupportedError)
      }
      "an invalid type of business is provided" in {
        validator.validate(rawData.copy(typeOfBusiness = Some(invalidTypeOfBusiness))) shouldBe List(TypeOfBusinessFormatError)
      }
      "an invalid business id is provided" in {
        validator.validate(rawData.copy(businessId = Some(invalidBusinessId))) shouldBe List(BusinessIdFormatError)
      }
    }

    "return multiple errors" when {
      "multiple invalid parameters are provided" in {
        val expectedResult = List(NinoFormatError, TypeOfBusinessFormatError)
        validator.validate(rawData.copy(nino = invalidNino, typeOfBusiness = Some(invalidTypeOfBusiness))) shouldBe expectedResult
      }
    }
  }
}
