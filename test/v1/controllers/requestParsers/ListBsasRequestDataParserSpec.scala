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
import v1.models.errors.{BadRequestError, ErrorWrapper, NinoFormatError, TaxYearFormatError}
import v1.models.request.{DesTaxYear, ListBsasRawData, ListBsasRequest}

class ListBsasRequestDataParserSpec extends UnitSpec{


  private val nino = "AA123456B"
  private val taxYear = "2018-19"
  private val typeOfBusiness ="self-employment"
  private val selfEmploymentId = "XAIS12345678901"


  private val inputData = ListBsasRawData(nino, taxYear, Some(typeOfBusiness), Some(selfEmploymentId))

  trait Test extends MockListBsasValidator {
    lazy val parser = new ListBsasRequestDataParser(mockValidator)
  }


  "parse" should {
    "return a request object" when {
      "valid data is provided" in new Test {
        MockValidator.validate(inputData).returns(Nil)

        parser.parseRequest(inputData) shouldBe
          Right(ListBsasRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), Some(typeOfBusiness), Some(selfEmploymentId)))
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
