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

package v3.controllers.requestParsers

import api.models.domain.{Nino, TaxYear}
import api.models.errors.{BadRequestError, BsasIdFormatError, ErrorWrapper, NinoFormatError}
import play.api.libs.json.Json
import support.UnitSpec
import v3.mocks.validators.MockSubmitUkPropertyBsasValidator
import v3.models.request.submitBsas.ukProperty._

class SubmitUkPropertyBsasRequestParserSpec extends UnitSpec {

  val calculationId                  = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  val nino                           = "AA123456A"
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val requestBodyJson = Json.parse(
    """
      |{
      |  "nonFurnishedHolidayLet": {    
      |    "income": {
      |      "totalRentsReceived": 123.12
      |    }
      |  }
      |}
      |""".stripMargin
  )

  val requestBody: SubmitUKPropertyBsasRequestBody =
    SubmitUKPropertyBsasRequestBody(
      nonFurnishedHolidayLet = Some(NonFurnishedHolidayLet(Some(NonFHLIncome(Some(123.12), None, None, None)), None)),
      furnishedHolidayLet = None
    )

  def inputDataWith(taxYear: Option[String]): SubmitUkPropertyBsasRawData = SubmitUkPropertyBsasRawData(
    nino,
    calculationId,
    requestBodyJson,
    taxYear
  )

  trait Test extends MockSubmitUkPropertyBsasValidator {
    lazy val parser = new SubmitUkPropertyBsasRequestParser(mockValidator)
  }

  "parser" should {
    "return a request object" when {
      "valid request data is supplied without a tax year" in new Test {

        val inputData: SubmitUkPropertyBsasRawData = inputDataWith(None)
        MockValidator.validate(inputData).returns(Nil)

        parser.parseRequest(inputData) shouldBe
          Right(SubmitUkPropertyBsasRequestData(Nino(nino), calculationId, requestBody, None))
      }

      "valid request data is supplied with a tax year" in new Test {

        val inputData: SubmitUkPropertyBsasRawData = inputDataWith(Some("2023-24"))
        MockValidator.validate(inputData).returns(Nil)

        parser.parseRequest(inputData) shouldBe
          Right(SubmitUkPropertyBsasRequestData(Nino(nino), calculationId, requestBody, Some(TaxYear.fromMtd("2023-24"))))

      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {

        val inputData: SubmitUkPropertyBsasRawData = inputDataWith(None)
        MockValidator.validate(inputData).returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {

        val inputData: SubmitUkPropertyBsasRawData = inputDataWith(None)
        MockValidator.validate(inputData).returns(List(NinoFormatError, BsasIdFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BsasIdFormatError))))
      }
    }
  }
}
