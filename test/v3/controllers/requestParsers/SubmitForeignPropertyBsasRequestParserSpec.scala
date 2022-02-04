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
import v3.mocks.validators.MockSubmitForeignPropertyBsasValidator
import v3.models.errors.{ BadRequestError, BsasIdFormatError, ErrorWrapper, NinoFormatError }
import v3.models.request.submitBsas.foreignProperty._

class SubmitForeignPropertyBsasRequestParserSpec extends UnitSpec {
  val nino                           = "AA123456B"
  val calculationId                  = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val requestBodyJson = Json.parse(
    """
      |{
      |  "nonFurnishedHolidayLet": [
      |    {
      |      "countryCode": "FRA",
      |      "income": {
      |        "totalRentsReceived": 123.12
      |      }
      |    }
      |  ]
      |}
      |""".stripMargin
  )

  val inputData: SubmitForeignPropertyRawData =
    SubmitForeignPropertyRawData(nino, calculationId, requestBodyJson)

  trait Test extends MockSubmitForeignPropertyBsasValidator {
    lazy val parser = new SubmitForeignPropertyBsasRequestParser(mockValidator)
  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockValidator.validate(inputData).returns(Nil)

        val body: SubmitForeignPropertyBsasRequestBody =
          SubmitForeignPropertyBsasRequestBody(
            nonFurnishedHolidayLet = Some(Seq(ForeignProperty("FRA", Some(ForeignPropertyIncome(Some(123.12), None, None)), None))),
            foreignFhlEea = None
          )

        parser.parseRequest(inputData) shouldBe
          Right(SubmitForeignPropertyBsasRequestData(Nino(nino), calculationId, body))
      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockValidator
          .validate(inputData)
          .returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockValidator
          .validate(inputData)
          .returns(List(NinoFormatError, BsasIdFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BsasIdFormatError))))
      }
    }
  }
}
