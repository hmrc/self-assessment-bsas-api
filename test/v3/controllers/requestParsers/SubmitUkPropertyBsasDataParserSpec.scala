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
import v2.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._
import v2.mocks.validators.MockSubmitUkPropertyBsasValidator
import v2.models.errors._
import v2.models.request.submitBsas.ukProperty.{SubmitUkPropertyBsasRawData, SubmitUkPropertyBsasRequestData}

class SubmitUkPropertyBsasDataParserSpec extends UnitSpec {

  val bsasId = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  val nino = "AA123456A"
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  trait Test extends MockSubmitUkPropertyBsasValidator {
    lazy val parser = new SubmitUkPropertyBsasDataParser(mockValidator)
  }

  "parser" should {
    "accept valid input" when {
      "a non-fhl-property with expenses with full adjustments is passed" in new Test {

        val inputData = SubmitUkPropertyBsasRawData(nino, bsasId, AnyContentAsJson(validNonFHLInputJson))

        MockValidator
          .validate(inputData)
          .returns(Nil)

        private val result = parser.parseRequest(inputData)
        result shouldBe Right(SubmitUkPropertyBsasRequestData(Nino(nino), bsasId, validNonFHLBody))
      }

      "a fhl-property with consolidated expenses has full adjustments is passed" in new Test {

        val inputData = SubmitUkPropertyBsasRawData(nino, bsasId, AnyContentAsJson(validfhlInputJson))

        MockValidator
          .validate(inputData)
          .returns(Nil)

        private val result = parser.parseRequest(inputData)
        result shouldBe Right(SubmitUkPropertyBsasRequestData(Nino(nino), bsasId, validfhlBody))
      }

      "minimal adjustment is passed" in new Test {

        val inputData = SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyNonFHL())

        MockValidator
          .validate(inputData)
          .returns(Nil)

        private val result = parser.parseRequest(inputData)
        result shouldBe Right(SubmitUkPropertyBsasRequestData(Nino(nino), bsasId, validMinimalBody))
      }
    }

    "reject invalid input" when {
      "a adjustment has zero value" in new Test {

        val inputData = SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyNonFHL(nonFHLIncomeZeroValue, nonFHLExpensesAllFields))


        MockValidator
          .validate(inputData)
          .returns(List(FormatAdjustmentValueError.copy(paths = Some(Seq("otherPropertyIncome")))))

        private val result = parser.parseRequest(inputData)
        result shouldBe Left(ErrorWrapper(correlationId, FormatAdjustmentValueError.copy(paths = Some(Seq("otherPropertyIncome"))), None))
      }

      "a adjustment has a value over the range" in new Test {

        val inputData = SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyNonFHL(nonFHLIncomeExceedRangeValue, nonFHLExpensesAllFields))


        MockValidator
          .validate(inputData)
          .returns(List(RuleAdjustmentRangeInvalid.copy(paths = Some(Seq("rentIncome")))))

        private val result = parser.parseRequest(inputData)
        result shouldBe Left(ErrorWrapper(correlationId, RuleAdjustmentRangeInvalid.copy(paths = Some(Seq("rentIncome"))), None))
      }

      "the bsas id format is incorrect" in new Test {

        val inputData = SubmitUkPropertyBsasRawData(nino, invalidBsasId, submitBsasRawDataBodyNonFHL(nonFHLIncomeExceedRangeValue, nonFHLExpensesAllFields))


        MockValidator
          .validate(inputData)
          .returns(List(BsasIdFormatError))

        private val result = parser.parseRequest(inputData)
        result shouldBe Left(ErrorWrapper(correlationId, BsasIdFormatError))
      }

      "the input contains consolidated expenses along with other expenses" in new Test {

        val inputData = SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyFHL(fhlIncomeAllFields, fhlInvalidConsolidatedExpenses))


          MockValidator
          .validate(inputData)
          .returns(List(RuleBothExpensesError))

        private val result = parser.parseRequest(inputData)
        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesError, None))
      }

      "the input has multiple invalid feels" in new Test {

        val inputData = SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyFHL(fhlIncomeAllFields, fhlMultipleInvalidExpenses))

        MockValidator
          .validate(inputData)
          .returns(List(RuleAdjustmentRangeInvalid.copy(paths = Some(Seq("premisesRunningCosts", "repairsAndMaintenance", "financialCosts", "professionalFees")))))

        private val result = parser.parseRequest(inputData)
        result shouldBe
          Left(ErrorWrapper(correlationId, RuleAdjustmentRangeInvalid.copy(paths = Some(Seq("premisesRunningCosts", "repairsAndMaintenance", "financialCosts", "professionalFees")))))
      }
    }
  }
}
