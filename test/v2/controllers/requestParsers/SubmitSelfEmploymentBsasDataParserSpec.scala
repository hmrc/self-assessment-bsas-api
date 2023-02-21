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

package v2.controllers.requestParsers

import api.models.domain.Nino
import api.models.errors._
import play.api.http.Status.BAD_REQUEST
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import v2.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._
import v2.mocks.validators.MockSubmitSelfEmploymentBsasValidator
import v2.models.request.submitBsas.selfEmployment.{Income, SubmitSelfEmploymentBsasRawData, SubmitSelfEmploymentBsasRequestData}

class SubmitSelfEmploymentBsasDataParserSpec extends UnitSpec {

  val bsasId                         = "a54ba782-5ef4-47f4-ab72-495406665ca9"
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

  val invalidBsasId = "a54ba782-5ef4-/(0)*f4-ab72-4954066%%%%%%%%%%"

  trait Test extends MockSubmitSelfEmploymentBsasValidator {
    lazy val parser = new SubmitSelfEmploymentBsasDataParser(mockValidator)
  }

  "parser" should {

    val rangeError: MtdError  = MtdError("RULE_RANGE_INVALID", s"Adjustment value for 'other' falls outside the accepted range", BAD_REQUEST)
    val formatError: MtdError = MtdError("FORMAT_ADJUSTMENT_VALUE", s"The format of the 'turnover' value is invalid", BAD_REQUEST)

    "accept valid input" when {
      "full adjustments is passed" in new Test {

        val inputData = SubmitSelfEmploymentBsasRawData(nino, bsasId, AnyContentAsJson(mtdRequest))

        MockValidator
          .validate(inputData)
          .returns(Nil)

        private val result = parser.parseRequest(inputData)
        result shouldBe Right(SubmitSelfEmploymentBsasRequestData(Nino(nino), bsasId, submitSelfEmploymentBsasRequestBodyModel))
      }
    }

    "reject invalid input" when {
      "a adjustment has zero value" in new Test {

        val inputData = SubmitSelfEmploymentBsasRawData(
          nino,
          bsasId,
          AnyContentAsJson(
            submitSelfEmploymentBsasRequestBodyMtdJson(submitSelfEmploymentBsasRequestBodyModel.copy(income = Some(invalidIncomeWithZeroValue))))
        )

        MockValidator
          .validate(inputData)
          .returns(List(formatError))

        private val result = parser.parseRequest(inputData)
        result shouldBe Left(ErrorWrapper(correlationId, formatError, None))
      }

      "a adjustment has a value over the range" in new Test {

        val inputData = SubmitSelfEmploymentBsasRawData(
          nino,
          bsasId,
          AnyContentAsJson(
            submitSelfEmploymentBsasRequestBodyMtdJson(submitSelfEmploymentBsasRequestBodyModel.copy(income = Some(invalidIncomeWithZeroValue))))
        )

        MockValidator
          .validate(inputData)
          .returns(List(rangeError))

        private val result = parser.parseRequest(inputData)
        result shouldBe Left(ErrorWrapper(correlationId, rangeError, None))
      }

      "the bsas id format is incorrect" in new Test {

        val inputData =
          SubmitSelfEmploymentBsasRawData(nino,
                                          invalidBsasId,
                                          AnyContentAsJson(submitSelfEmploymentBsasRequestBodyMtdJson(submitSelfEmploymentBsasRequestBodyModel)))

        MockValidator
          .validate(inputData)
          .returns(List(BsasIdFormatError))

        private val result = parser.parseRequest(inputData)
        result shouldBe Left(ErrorWrapper(correlationId, BsasIdFormatError))
      }

      "the input has multiple invalid feels" in new Test {

        val inputData = SubmitSelfEmploymentBsasRawData(
          nino,
          bsasId,
          AnyContentAsJson(
            submitSelfEmploymentBsasRequestBodyMtdJson(submitSelfEmploymentBsasRequestBodyModel.copy(income = Some(invalidIncomeWithMultipleErrors))))
        )

        MockValidator
          .validate(inputData)
          .returns(List(formatError, rangeError))

        private val result = parser.parseRequest(inputData)
        result shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(formatError, rangeError))))
      }
    }
  }
}
