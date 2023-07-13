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

package v2.controllers.requestParsers.validators

import api.models.errors._
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import v2.fixtures.ukProperty.SubmitUKPropertyBsasRequestBodyFixtures._
import v2.models.errors._
import v2.models.request.submitBsas.ukProperty._

class SubmitUkPropertyBsasValidatorSpec extends UnitSpec {

  val bsasId = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  val nino = "AA123456A"
  val invalidBody: AnyContentAsJson = AnyContentAsJson(Json.obj("aproperty" -> "25"))

  class SetUp {
    val validator = new SubmitUkPropertyBsasValidator
  }

  "running validation" should {
    "return no errors" when {
      "all the fields are submitted for adjustments non-furnished" in new SetUp {

        validator
          .validate(SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyNonFHL(nonFHLIncomeAllFields, nonFHLExpensesAllFields)))
          .isEmpty shouldBe true
      }

      "all the fields are submitted for adjustments furnished" in new SetUp {

        validator
          .validate(SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyFHL(fhlIncomeAllFields, fhlExpensesAllFields)))
          .isEmpty shouldBe true
      }
    }

    "return errors" when {
      "a single adjustment field is zero" in new SetUp {

        private val result = validator.validate(
          SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyNonFHL(nonFHLIncomeZeroValue, nonFHLExpensesAllFields))
        )

        result.length shouldBe 1
        result shouldBe List(FormatAdjustmentValueError.copy(paths = Some(Seq("/nonFurnishedHolidayLet/income/otherPropertyIncome"))))
      }

      "a single adjustment field is more then 99999999999.99 " in new SetUp {

        private val result = validator.validate(
          SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyNonFHL(nonFHLIncomeExceedRangeValue, nonFHLExpensesAllFields))
        )

        result.length shouldBe 1
        result shouldBe List(RuleAdjustmentRangeInvalid.copy(paths = Some(Seq("/nonFurnishedHolidayLet/income/rentIncome"))))
      }

      "the format of the bsasId is invalid" in new SetUp {

        private val result = validator.validate(
          SubmitUkPropertyBsasRawData(
            nino,
            invalidBsasId,
            submitBsasRawDataBodyNonFHL(nonFHLIncomeExceedRangeValue, nonFHLExpensesAllFields)
          )
        )

        result.length shouldBe 1
        result shouldBe List(BsasIdFormatError)
      }

      "the submission contains consolidated expenses along with other values" in new SetUp {

        private val result =
          validator.validate(
            SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyFHL(fhlIncomeAllFields, fhlInvalidConsolidatedExpenses))
          )

        result.length shouldBe 1
        result shouldBe List(RuleBothExpensesError)
      }

      "multiple adjustment fields are invalid" in new SetUp {

        private val result =
          validator.validate(
            SubmitUkPropertyBsasRawData(nino, bsasId, submitBsasRawDataBodyFHL(fhlIncomeAllFields, fhlMultipleInvalidExpenses))
          )

        result.length shouldBe 1
        result shouldBe List(
          RuleAdjustmentRangeInvalid.copy(paths = Some(Seq(
            "/furnishedHolidayLet/expenses/premisesRunningCosts",
            "/furnishedHolidayLet/expenses/repairsAndMaintenance",
            "/furnishedHolidayLet/expenses/financialCosts",
            "/furnishedHolidayLet/expenses/professionalFees"
          ))))
      }

      "invalid request is submitted" in new SetUp {

        private val result =
          validator.validate(
            SubmitUkPropertyBsasRawData(nino, bsasId, invalidBody)
          )

        result.length shouldBe 1
        result shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "an empty body is submitted" in new SetUp {

        private val result =
          validator.validate(
            SubmitUkPropertyBsasRawData(nino, bsasId, AnyContentAsJson(Json.obj()))
          )

        result.length shouldBe 1
        result shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "an empty FHL body is submitted" in new SetUp {

        val body: AnyContentAsJson = AnyContentAsJson(Json.obj("furnishedHolidayLet" -> Json.obj()))

        private val result =
          validator.validate(
            SubmitUkPropertyBsasRawData(nino, bsasId, body)
          )

        result.length shouldBe 1
        result shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "an FHL body is submitted with an empty income body" in new SetUp {

        val body: AnyContentAsJson = submitBsasRawDataBodyFHL(income = Some(Json.obj()), expenses = None)

        private val result =
          validator.validate(
            SubmitUkPropertyBsasRawData(nino, bsasId, body)
          )

        result.length shouldBe 1
        result shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "an FHL body is submitted with an empty expenses body" in new SetUp {

        val body: AnyContentAsJson = submitBsasRawDataBodyFHL(income = None, expenses = Some(Json.obj()))

        private val result =
          validator.validate(
            SubmitUkPropertyBsasRawData(nino, bsasId, body)
          )

        result.length shouldBe 1
        result shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "an empty non-FHL body is submitted" in new SetUp {

        val body: AnyContentAsJson = AnyContentAsJson(Json.obj("nonFurnishedHolidayLet" -> Json.obj()))

        private val result =
          validator.validate(
            SubmitUkPropertyBsasRawData(nino, bsasId, body)
          )

        result.length shouldBe 1
        result shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "a non-FHL body is submitted with an empty income body" in new SetUp {

        val body: AnyContentAsJson = submitBsasRawDataBodyNonFHL(income = Some(Json.obj()), expenses = None)

        private val result =
          validator.validate(
            SubmitUkPropertyBsasRawData(nino, bsasId, body)
          )

        result.length shouldBe 1
        result shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "a non-FHL body is submitted with an empty expenses body" in new SetUp {

        val body: AnyContentAsJson = submitBsasRawDataBodyNonFHL(income = None, expenses = Some(Json.obj()))

        private val result =
          validator.validate(
            SubmitUkPropertyBsasRawData(nino, bsasId, body)
          )

        result.length shouldBe 1
        result shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "both furnished and non furnished are supplied" in new SetUp {

        private val result =
          validator.validate(
            SubmitUkPropertyBsasRawData(
              nino,
              bsasId,
              AnyContentAsJson(
                Json.obj(
                  "nonFurnishedHolidayLet" ->
                    Json.obj("income" -> Json.obj("rentIncome" -> 1)),
                  "furnishedHolidayLet" ->
                    Json.obj("income" -> Json.obj("rentIncome" -> 1))
                )
              )
            )
          )

        result.length shouldBe 1
        result shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
    }
  }
}
