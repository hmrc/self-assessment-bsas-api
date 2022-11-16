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

package v3.controllers.requestParsers

import domain.Nino
import play.api.libs.json.Json
import support.UnitSpec
import v3.mocks.validators.MockSubmitUkPropertyBsasValidator
import v3.models.domain.TaxYear
import v3.models.errors._
import v3.models.request.submitBsas.ukProperty._

class SubmitUkPropertyBsasRequestParserSpec extends UnitSpec {

  val calculationId                  = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  val nino                           = "AA123456A"
  val tysTaxYear: String             = "2023-24"
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

  val nonTysInputData: SubmitUkPropertyBsasRawData =
    SubmitUkPropertyBsasRawData(
      nino = nino,
      calculationId = calculationId,
      body = requestBodyJson,
      taxYear = None
    )

  val tysInputData: SubmitUkPropertyBsasRawData =
    SubmitUkPropertyBsasRawData(
      nino = nino,
      calculationId = calculationId,
      body = requestBodyJson,
      taxYear = Some(tysTaxYear)
    )

  val requestBody: SubmitUKPropertyBsasRequestBody =
    SubmitUKPropertyBsasRequestBody(
      nonFurnishedHolidayLet = Some(NonFurnishedHolidayLet(Some(NonFHLIncome(Some(123.12), None, None, None)), None)),
      furnishedHolidayLet = None
    )

  trait Test extends MockSubmitUkPropertyBsasValidator {
    lazy val parser = new SubmitUkPropertyBsasRequestParser(mockValidator)
  }

  "parser" should {
    "return a request object" when {
      "valid non-TYS request data is supplied" in new Test {

        MockValidator.validate(nonTysInputData).returns(Nil)

        parser.parseRequest(nonTysInputData) shouldBe
          Right(SubmitUkPropertyBsasRequestData(Nino(nino), calculationId, requestBody, None))
      }

      "valid TYS request data is supplied" in new Test {

        MockValidator.validate(tysInputData).returns(Nil)

        parser.parseRequest(tysInputData) shouldBe
          Right(SubmitUkPropertyBsasRequestData(Nino(nino), calculationId, requestBody, Some(TaxYear.fromMtd(tysTaxYear))))

      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockValidator
          .validate(nonTysInputData)
          .returns(List(NinoFormatError))

        parser.parseRequest(nonTysInputData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockValidator
          .validate(nonTysInputData)
          .returns(List(NinoFormatError, BsasIdFormatError))

        parser.parseRequest(nonTysInputData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BsasIdFormatError))))
      }
    }
  }
}
