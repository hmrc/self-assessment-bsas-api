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

package v1.controllers.requestParsers

import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v1.mocks.validators.MockListBsasValidator
import v1.models.domain.TypeOfBusiness
import v1.models.errors.{BadRequestError, ErrorWrapper, NinoFormatError, TaxYearFormatError}
import v1.models.request.{DesTaxYear, ListBsasRawData, ListBsasRequest}

class ListBsasRequestDataParserSpec extends UnitSpec{


  private val nino = "AA123456B"
  private val taxYear = "2018-19"
  private val typeOfBusiness = "self-employment"
  private val typeOfBusinessTwo = "uk-property-non-fhl"
  private val typeOfBusinessThree = "uk-property-fhl"
  private val incomeSourceId ="incomeSourceId"
  private val incomeSourceType ="incomeSourceType"
  private val selfEmploymentId = "XAIS12345678901"


  private val inputData = ListBsasRawData(nino, taxYear, Some(typeOfBusiness), Some(selfEmploymentId))
  private val inputDataTwo = ListBsasRawData(nino, taxYear, Some(typeOfBusiness), None)
  private val inputDataThree = ListBsasRawData(nino, taxYear, Some(typeOfBusinessTwo), None)
  private val inputDataFour = ListBsasRawData(nino, taxYear, Some(typeOfBusinessThree), None)
  private val inputDataFive = ListBsasRawData(nino, taxYear, None, None)

  trait Test extends MockListBsasValidator {
    lazy val parser = new ListBsasRequestDataParser(mockValidator)
  }


  "parse" should {
    "return a request object" when {
      "valid selfEmploymentId is provided" in new Test {
        MockValidator.validate(inputData).returns(Nil)

        parser.parseRequest(inputData) shouldBe
          Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), Some(incomeSourceId), Some(selfEmploymentId)))
      }

      "valid self-employment is provided" in new Test {

        MockValidator.validate(inputDataTwo).returns(Nil)

        parser.parseRequest(inputDataTwo) shouldBe
          Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), Some(incomeSourceType), Some(TypeOfBusiness.`self-employment`.toIdentifierValue)))
      }

      "valid uk property non fhl is provided" in new Test {

        MockValidator.validate(inputDataThree).returns(Nil)

        parser.parseRequest(inputDataThree) shouldBe
          Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), Some(incomeSourceType), Some(TypeOfBusiness.`uk-property-non-fhl`.toIdentifierValue)))
      }

      "valid uk property fhl is provided" in new Test {

        MockValidator.validate(inputDataFour).returns(Nil)

        parser.parseRequest(inputDataFour) shouldBe
          Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), Some(incomeSourceType), Some(TypeOfBusiness.`uk-property-fhl`.toIdentifierValue)))
      }

      "valid data with no optionals is provided" in new Test {

        MockValidator.validate(inputDataFive).returns(Nil)

        parser.parseRequest(inputDataFive) shouldBe
          Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), None, None))
      }
    }

    "return an ErrorWrapper" when {
      "a single error is found" in new Test {
        MockValidator.validate(inputData).returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe Left(ErrorWrapper(None, NinoFormatError))
      }

      "a multiple errors are found" in new Test {
        MockValidator.validate(inputData).returns(List(NinoFormatError, TaxYearFormatError))

        parser.parseRequest(inputData) shouldBe Left(ErrorWrapper(None, BadRequestError, Some(List(NinoFormatError, TaxYearFormatError))))
      }
    }
  }
}
