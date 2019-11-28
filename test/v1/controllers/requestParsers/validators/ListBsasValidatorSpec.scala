/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators

import support.UnitSpec
import v1.models.errors.{NinoFormatError, RuleTaxYearNotSupportedError, RuleTaxYearRangeExceededError, SelfEmploymentIdFormatError, TypeOfBusinessFormatError}
import v1.models.request.ListBsasRawData

class ListBsasValidatorSpec extends UnitSpec{

  private val nino = "AA123456B"
  private val taxYear = "2018-19"
  private val typeOfBusiness = "uk-property-fhl"
  private val selfEmploymentId = "XAIS12345678901"
  private val invalidNino = "not a nino"
  private val invalidTaxYear = "2018-20"
  private val tooEarlyTaxYear = "2016-17"
  private val invalidTypeOfBusiness = "toothpicks-for-hamsters"
  private val invalidSelfEmploymentId = "Not a SelfEmploymentId"

  val validator = new ListBsasValidator()


  "running the validator" should {
    "return no errors" when {
      "valid parameters are provided with a tax year" in {
        validator.validate(ListBsasRawData(nino, taxYear, Some(typeOfBusiness), Some(selfEmploymentId))) shouldBe Nil
      }
    }

    "return one error" when {
      "an invalid nino is provided" in {
        validator.validate(ListBsasRawData(invalidNino, taxYear, Some(typeOfBusiness), Some(selfEmploymentId))) shouldBe List(NinoFormatError)
      }
      "an invalid tax year is provided" in {
        validator.validate(ListBsasRawData(nino, invalidTaxYear, Some(typeOfBusiness), Some(selfEmploymentId))) shouldBe List(RuleTaxYearRangeExceededError)
      }
      "a too early tax year is provided" in {
        validator.validate(ListBsasRawData(nino, tooEarlyTaxYear, Some(typeOfBusiness), Some(selfEmploymentId))) shouldBe List(RuleTaxYearNotSupportedError)
      }
      "an invalid type of business is provided" in {
        validator.validate(ListBsasRawData(nino, taxYear, Some(invalidTypeOfBusiness), Some(selfEmploymentId))) shouldBe List(TypeOfBusinessFormatError)
      }
      "an invalid self employment id is provided" in {
        validator.validate(ListBsasRawData(nino, taxYear, Some(typeOfBusiness), Some(invalidSelfEmploymentId))) shouldBe List(SelfEmploymentIdFormatError)
      }
    }

    "return multiple errors" when {
      "multiple invalid parameters are provided" in {
        val expectedResult = List(NinoFormatError, TypeOfBusinessFormatError)
        validator.validate(ListBsasRawData(invalidNino, taxYear, Some(invalidTypeOfBusiness), Some(selfEmploymentId))) shouldBe expectedResult
      }
    }
  }
}
