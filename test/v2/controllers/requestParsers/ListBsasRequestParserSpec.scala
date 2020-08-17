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

package v2.controllers.requestParsers

import java.time.LocalDate

import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import utils.DesTaxYear
import v2.mocks.MockCurrentDateProvider
import v2.mocks.validators.MockListBsasValidator
import v2.models.domain.TypeOfBusiness
import v2.models.errors.{BadRequestError, ErrorWrapper, NinoFormatError, TaxYearFormatError}
import v2.models.request.{ListBsasRawData, ListBsasRequest}

class ListBsasRequestParserSpec extends UnitSpec{


  private val nino = "AA123456B"
  private val taxYear = "2019-20"
  private val typeOfBusiness = "self-employment"
  private val typeOfBusinessTwo = "uk-property-non-fhl"
  private val typeOfBusinessThree = "uk-property-fhl"
  private val incomeSourceId ="incomeSourceId"
  private val incomeSourceType ="incomeSourceType"
  private val businessId = "XAIS12345678901"


  private val inputWithSelfEmploymentIdAndTypeOfBusiness = ListBsasRawData(nino, Some(taxYear), Some(typeOfBusiness), Some(businessId))
  private val inputWithSelfEmploymentId = ListBsasRawData(nino, Some(taxYear), None, Some(businessId))
  private val inputDataTwo = ListBsasRawData(nino, Some(taxYear), Some(typeOfBusiness), None)
  private val inputDataThree = ListBsasRawData(nino, Some(taxYear), Some(typeOfBusinessTwo), None)
  private val inputDataFour = ListBsasRawData(nino, Some(taxYear), Some(typeOfBusinessThree), None)
  private val inputDataFive = ListBsasRawData(nino, Some(taxYear), None, None)

  class Test(date: LocalDate = LocalDate.of(2019, 6, 18)) extends MockListBsasValidator with MockCurrentDateProvider {
    lazy val parser = new ListBsasRequestParser(mockValidator, mockCurrentDateProvider)

    MockCurrentDateProvider.getCurrentDate().returns(date)
  }


  "parse" when {
    "a valid businessId is provided" should {
      "return a valid object if no typeOfBusiness is provided" in new Test {
        MockValidator.validate(inputWithSelfEmploymentIdAndTypeOfBusiness).returns(Nil)

        parser.parseRequest(inputWithSelfEmploymentIdAndTypeOfBusiness) shouldBe
          Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), Some(incomeSourceId), Some(businessId)))
      }
      "return a valid object if a typeOfBusiness is provided" in new Test {
        MockValidator.validate(inputWithSelfEmploymentId).returns(Nil)

        parser.parseRequest(inputWithSelfEmploymentId) shouldBe
          Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), Some(incomeSourceId), Some(businessId)))
      }
    }

    "no businessId is provided" should {
      "return a valid object with self-employment" in new Test {

        MockValidator.validate(inputDataTwo).returns(Nil)

        parser.parseRequest(inputDataTwo) shouldBe
          Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), Some(incomeSourceType), Some(TypeOfBusiness.`self-employment`.toIdentifierValue)))
      }

      "return a valid object with uk-property-non-fhl" in new Test {

        MockValidator.validate(inputDataThree).returns(Nil)

        parser.parseRequest(inputDataThree) shouldBe
          Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), Some(incomeSourceType), Some(TypeOfBusiness.`uk-property-non-fhl`.toIdentifierValue)))
      }

      "return a valid object with uk-property-fhl" in new Test {

        MockValidator.validate(inputDataFour).returns(Nil)

        parser.parseRequest(inputDataFour) shouldBe
          Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), Some(incomeSourceType), Some(TypeOfBusiness.`uk-property-fhl`.toIdentifierValue)))
      }
    }

    "no query parameters are sent" should {
      "return a valid object with no incomeSourceType or identifierValue" in new Test {

        MockValidator.validate(inputDataFive).returns(Nil)

        parser.parseRequest(inputDataFive) shouldBe
          Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), None, None))
      }

      "valid data is provided without tax year" in new Test {
        MockValidator.validate(inputDataFive.copy(taxYear = None)).returns(Nil)

        parser.parseRequest(inputDataFive.copy(taxYear = None)) shouldBe Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), None, None))
      }
    }

    "return an ErrorWrapper" when {
      "a single error is found" in new Test {
        MockValidator.validate(inputWithSelfEmploymentIdAndTypeOfBusiness).returns(List(NinoFormatError))

        parser.parseRequest(inputWithSelfEmploymentIdAndTypeOfBusiness) shouldBe Left(ErrorWrapper(None, NinoFormatError))
      }

      "a multiple errors are found" in new Test {
        MockValidator.validate(inputWithSelfEmploymentIdAndTypeOfBusiness).returns(List(NinoFormatError, TaxYearFormatError))

        parser.parseRequest(inputWithSelfEmploymentIdAndTypeOfBusiness) shouldBe Left(ErrorWrapper(None, BadRequestError, Some(List(NinoFormatError, TaxYearFormatError))))
      }
    }
  }
}
