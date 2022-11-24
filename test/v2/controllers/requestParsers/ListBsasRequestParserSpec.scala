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

package v2.controllers.requestParsers

import java.time.LocalDate

import support.UnitSpec
import domain.Nino
import v2.mocks.MockCurrentDateProvider
import v2.mocks.validators.MockListBsasValidator
import v2.models.domain.TypeOfBusiness
import v2.models.errors.{BadRequestError, ErrorWrapper, NinoFormatError, TaxYearFormatError}
import v2.models.request.{ListBsasRawData, ListBsasRequest}
import v3.models.domain.DownstreamTaxYear

class ListBsasRequestParserSpec extends UnitSpec{


  private val nino = "AA123456B"
  private val taxYear = "2019-20"
  private val typeOfBusinessSE = "self-employment"
  private val typeOfBusinessNonFhl = "uk-property-non-fhl"
  private val typeOfBusinessFhl = "uk-property-fhl"
  private val typeOfBusinessFhlEea = "foreign-property-fhl-eea"
  private val typeOfBusinessForeign = "foreign-property"
  private val businessId = "XAIS12345678901"

  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val inputWithBusinessIdAndTypeOfBusiness = ListBsasRawData(nino, Some(taxYear), Some(typeOfBusinessSE), Some(businessId))
  private val inputWithBusinessId = ListBsasRawData(nino, Some(taxYear), None, Some(businessId))
  private val inputDataTwo = ListBsasRawData(nino, Some(taxYear), Some(typeOfBusinessSE), None)
  private val inputDataThree = ListBsasRawData(nino, Some(taxYear), Some(typeOfBusinessNonFhl), None)
  private val inputDataFour = ListBsasRawData(nino, Some(taxYear), Some(typeOfBusinessFhl), None)
  private val inputDataFive = ListBsasRawData(nino, Some(taxYear), None, None)
  private val inputDataSix = ListBsasRawData(nino, Some(taxYear), Some(typeOfBusinessFhlEea), None)
  private val inputDataSeven = ListBsasRawData(nino, Some(taxYear), Some(typeOfBusinessForeign), None)

  class Test(date: LocalDate = LocalDate.of(2019, 6, 18)) extends MockListBsasValidator with MockCurrentDateProvider {
    lazy val parser = new ListBsasRequestParser(mockValidator, mockCurrentDateProvider)

    MockCurrentDateProvider.getCurrentDate().returns(date)
  }


  "parse" when {
    "a valid businessId is provided" should {
      "return a valid object if a typeOfBusiness is provided" in new Test {
        MockValidator.validate(inputWithBusinessIdAndTypeOfBusiness).returns(Nil)

        parser.parseRequest(inputWithBusinessIdAndTypeOfBusiness) shouldBe
          Right(ListBsasRequest(Nino(nino), DownstreamTaxYear.fromMtd(taxYear), Some(businessId), Some("01")))
      }
      "return a valid object if no typeOfBusiness is provided" in new Test {
        MockValidator.validate(inputWithBusinessId).returns(Nil)

        parser.parseRequest(inputWithBusinessId) shouldBe
          Right(ListBsasRequest(Nino(nino), DownstreamTaxYear.fromMtd(taxYear), Some(businessId), None))
      }
    }

    "no businessId is provided" should {
      "return a valid object with self-employment" in new Test {

        MockValidator.validate(inputDataTwo).returns(Nil)

        parser.parseRequest(inputDataTwo) shouldBe
          Right(ListBsasRequest(Nino(nino), DownstreamTaxYear.fromMtd(taxYear), None, Some(TypeOfBusiness.`self-employment`.toIdentifierValue)))
      }

      "return a valid object with uk-property-non-fhl" in new Test {

        MockValidator.validate(inputDataThree).returns(Nil)

        parser.parseRequest(inputDataThree) shouldBe
          Right(ListBsasRequest(Nino(nino), DownstreamTaxYear.fromMtd(taxYear), None, Some(TypeOfBusiness.`uk-property-non-fhl`.toIdentifierValue)))
      }

      "return a valid object with uk-property-fhl" in new Test {

        MockValidator.validate(inputDataFour).returns(Nil)

        parser.parseRequest(inputDataFour) shouldBe
          Right(ListBsasRequest(Nino(nino), DownstreamTaxYear.fromMtd(taxYear), None, Some(TypeOfBusiness.`uk-property-fhl`.toIdentifierValue)))
      }

      "return a valid object with foreign-property-fhl-eea" in new Test {

        MockValidator.validate(inputDataSix).returns(Nil)

        parser.parseRequest(inputDataSix) shouldBe
          Right(ListBsasRequest(Nino(nino), DownstreamTaxYear.fromMtd(taxYear),
            None, Some(TypeOfBusiness.`foreign-property-fhl-eea`.toIdentifierValue)))
      }

      "return a valid object with foreign-property" in new Test {

        MockValidator.validate(inputDataSeven).returns(Nil)

        parser.parseRequest(inputDataSeven) shouldBe
          Right(ListBsasRequest(Nino(nino), DownstreamTaxYear.fromMtd(taxYear),
            None, Some(TypeOfBusiness.`foreign-property`.toIdentifierValue)))
      }
    }

    "no query parameters are sent" should {
      "return a valid object with no incomeSourceType or identifierValue" in new Test {

        MockValidator.validate(inputDataFive).returns(Nil)

        parser.parseRequest(inputDataFive) shouldBe
          Right(ListBsasRequest(Nino(nino), DownstreamTaxYear.fromMtd(taxYear), None, None))
      }

      "valid data is provided without tax year" in new Test {
        MockValidator.validate(inputDataFive.copy(taxYear = None)).returns(Nil)

        parser.parseRequest(inputDataFive.copy(taxYear = None)) shouldBe Right(ListBsasRequest(Nino(nino), DownstreamTaxYear.fromMtd(taxYear), None, None))
      }
    }

    "return an ErrorWrapper" when {
      "a single error is found" in new Test {
        MockValidator.validate(inputWithBusinessIdAndTypeOfBusiness).returns(List(NinoFormatError))

        parser.parseRequest(inputWithBusinessIdAndTypeOfBusiness) shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "a multiple errors are found" in new Test {
        MockValidator.validate(inputWithBusinessIdAndTypeOfBusiness).returns(List(NinoFormatError, TaxYearFormatError))

        parser.parseRequest(inputWithBusinessIdAndTypeOfBusiness) shouldBe Left(ErrorWrapper(correlationId, BadRequestError, Some(List(NinoFormatError, TaxYearFormatError))))
      }
    }
  }
}
