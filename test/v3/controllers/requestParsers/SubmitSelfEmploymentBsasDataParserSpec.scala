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

import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import domain.Nino
import v3.mocks.validators.MockSubmitSelfEmploymentBsasValidator
import v3.models.errors._
import v3.models.request.submitBsas.selfEmployment.{Income, SubmitSelfEmploymentBsasRawData, SubmitSelfEmploymentBsasRequestData}
import v3.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._

class SubmitSelfEmploymentBsasDataParserSpec extends UnitSpec {

  val calculationId                  = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  val nino                           = "AA123456A"
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  val invalidIncomeWithZeroValue: Income =
    Income(
      turnover = Some(0),
      other = Some(1000.50)
    )

  val invalidIncomeWithExceedRangeValue: Income =
    Income(
      turnover = Some(1000.50),
      other = Some(100000000000.00)
    )

  val invalidIncomeWithMultipleErrors: Income =
    Income(
      turnover = Some(0),
      other = Some(100000000000.00)
    )

  val invalidCalculationId = "a54ba782-5ef4-/(0)*f4-ab72-4954066%%%%%%%%%%"

  trait Test extends MockSubmitSelfEmploymentBsasValidator {
    lazy val parser = new SubmitSelfEmploymentBsasDataParser(mockValidator)
  }

  val inputData: SubmitSelfEmploymentBsasRawData = SubmitSelfEmploymentBsasRawData(nino, calculationId,
    AnyContentAsJson(submitSelfEmploymentBsasRequestBodyMtdJson(
      submitSelfEmploymentBsasRequestBodyModel.copy(income = Some(invalidIncomeWithZeroValue)))))


  "parser" should {

    "accept valid input" when {
      "full adjustments is passed" in new Test {

        val inputData: SubmitSelfEmploymentBsasRawData = SubmitSelfEmploymentBsasRawData(nino, calculationId,
          AnyContentAsJson(mtdRequest))

        MockValidator
          .validate(inputData)
          .returns(Nil)

        private val result = parser.parseRequest(inputData)
        result shouldBe Right(SubmitSelfEmploymentBsasRequestData(Nino(nino), calculationId, submitSelfEmploymentBsasRequestBodyModel))
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
          .returns(List(NinoFormatError, CalculationIdFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, CalculationIdFormatError))))
      }
    }
  }
}
