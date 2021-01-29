/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.libs.json.Json
import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v2.mocks.validators.MockSubmitForeignPropertyBsasValidator
import v2.models.errors.{BadRequestError, BsasIdFormatError, ErrorWrapper, NinoFormatError}
import v2.models.request.submitBsas.foreignProperty._

class SubmitForeignPropertyBsasRequestParserSpec extends UnitSpec {
  val nino = "AA123456B"
  val bsasId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val requestBodyJson = Json.parse(
    """
      |{
      |  "foreignProperty": [
      |    {
      |      "countryCode": "FRA",
      |      "income": {
      |        "rentIncome": 123.12,
      |        "premiumsOfLeaseGrant": 123.12,
      |        "otherPropertyIncome": 123.12
      |      },
      |      "expenses": {
      |        "premisesRunningCosts": 123.12,
      |        "repairsAndMaintenance": 123.12,
      |        "financialCosts": 123.12,
      |        "professionalFees": 123.12,
      |        "travelCosts": 123.12,
      |        "costOfServices": 123.12,
      |        "residentialFinancialCost": 123.12,
      |        "other": 123.12
      |      }
      |    }
      |  ]
      |}
      |""".stripMargin
  )

  val inputData =
    SubmitForeignPropertyRawData(nino, bsasId, requestBodyJson)

  trait Test extends MockSubmitForeignPropertyBsasValidator {
    lazy val parser = new SubmitForeignPropertyBsasRequestParser(mockValidator)
  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockValidator.validate(inputData).returns(Nil)

        val submitForeignPropertyBsasRequestBody =
          SubmitForeignPropertyBsasRequestBody(
            Some(Seq(ForeignProperty(
              "FRA",
              Some(ForeignPropertyIncome(
                Some(123.12),
                Some(123.12),
                Some(123.12)
              )),
              Some(ForeignPropertyExpenses(
                Some(123.12),
                Some(123.12),
                Some(123.12),
                Some(123.12),
                Some(123.12),
                Some(123.12),
                Some(123.12),
                Some(123.12),
                None
              ))))),
            None
            )

        parser.parseRequest(inputData) shouldBe
          Right(SubmitForeignPropertyBsasRequestData(Nino(nino), bsasId, submitForeignPropertyBsasRequestBody))
      }
    }
    "return an ErrroWrapper" when {
      "a single validation error occurs" in new Test {
        MockValidator.validate(inputData)
          .returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockValidator.validate(inputData)
          .returns(List(NinoFormatError, BsasIdFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BsasIdFormatError))))
      }
    }
  }
}
