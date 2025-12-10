/*
 * Copyright 2025 HM Revenue & Customs
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

package v7.foreignPropertyBsas.submit.def4

import common.errors.*
import play.api.libs.json.*
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors.*
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v7.foreignPropertyBsas.submit.def4.model.request.*
import v7.foreignPropertyBsas.submit.def4.model.request.SubmitForeignPropertyBsasFixtures.*

class Def4_SubmitForeignPropertyBsasValidatorSpec extends UnitSpec with JsonErrorValidators {

  private implicit val correlationId: String = "1234"

  private val validNino          = "AA123456A"
  private val validCalculationId = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  private val validTaxYear       = "2026-27"

  private val parsedNino          = Nino(validNino)
  private val parsedCalculationId = CalculationId(validCalculationId)
  private val parsedTaxYear       = TaxYear.fromMtd(validTaxYear)

  private def entry =
    Json.parse(
      s"""
        |{
        |    "propertyId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
        |    "income": {
        |        "totalRentsReceived": 1000.45,
        |        "premiumsOfLeaseGrant": -99.99,
        |        "otherPropertyIncome": 1000.00
        |    },
        |    "expenses": {
        |        "premisesRunningCosts": 1000.45,
        |        "repairsAndMaintenance": -99999.99,
        |        "financialCosts": 5000.45,
        |        "professionalFees": 300.99,
        |        "costOfServices": 500.00,
        |        "residentialFinancialCost": 9000.00,
        |        "other": 1000.00,
        |        "travelCosts": 99.99
        |    }
        |}
      """.stripMargin
    )

  private val entryConsolidated =
    Json.parse(
      """
        |{
        |    "propertyId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
        |    "income": {
        |        "totalRentsReceived": 1000.45,
        |        "premiumsOfLeaseGrant": -99.99,
        |        "otherPropertyIncome": 1000.00
        |    },
        |    "expenses": {
        |        "consolidatedExpenses": 332.78
        |    }
        |}
      """.stripMargin
    )

  private def foreignPropertyBodyWith(entries: JsValue*): JsObject =
    Json
      .parse(
        s"""
        |{
        |    "foreignProperty": {
        |        "propertyLevelDetail": ${JsArray(entries)}
        |    }
        |}
      """.stripMargin
      )
      .as[JsObject]

  private val foreignPropertyBody: JsValue = foreignPropertyBodyWith(entry)
  private val parsedForeignPropertyBody    = foreignPropertyBody.as[Def4_SubmitForeignPropertyBsasRequestBody]

  private val parsedWithOnlyZeroAdjustmentsBody: Def4_SubmitForeignPropertyBsasRequestBody =
    mtdRequestWithOnlyZeroAdjustments(true).as[Def4_SubmitForeignPropertyBsasRequestBody]

  private def validator(nino: String, calculationId: String, taxYear: String, body: JsValue) =
    new Def4_SubmitForeignPropertyBsasValidator(nino, calculationId, taxYear, body)

  "running a validation" should {
    "return the parsed domain object" when {

      "passed a valid foreign property request" in {
        val result = validator(validNino, validCalculationId, validTaxYear, foreignPropertyBody).validateAndWrapResult()
        result shouldBe Right(
          Def4_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedForeignPropertyBody)
        )
      }

      "passed a valid non-fhl consolidated expenses request" in {
        val foreignPropertyBodyConsolidated       = foreignPropertyBodyWith(entryConsolidated)
        val parsedForeignPropertyBodyConsolidated = foreignPropertyBodyConsolidated.as[Def4_SubmitForeignPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, validTaxYear, foreignPropertyBodyConsolidated).validateAndWrapResult()

        result shouldBe Right(
          Def4_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedForeignPropertyBodyConsolidated)
        )
      }

      "a minimal foreign property request is supplied" in {
        val minimalForeignPropertyBody = Json.parse(
          """
            |{
            |    "foreignProperty": {
            |        "propertyLevelDetail": [
            |            {
            |                "propertyId": "717f3a7a-db8e-11e9-8a34-2a2ae2dbcce4",
            |                "income": {
            |                    "totalRentsReceived": 1000.45
            |                }
            |            }
            |        ]
            |    }
            |}
          """.stripMargin
        )
        val parsedMinimalForeignPropertyBody = minimalForeignPropertyBody.as[Def4_SubmitForeignPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, validTaxYear, minimalForeignPropertyBody).validateAndWrapResult()

        result shouldBe Right(
          Def4_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedMinimalForeignPropertyBody)
        )
      }

      "a valid request with a taxYear is supplied" in {
        val foreignPropertyBodyWithConsolidatedEntry       = foreignPropertyBodyWith(entryConsolidated)
        val parsedForeignPropertyBodyWithConsolidatedEntry = foreignPropertyBodyWithConsolidatedEntry.as[Def4_SubmitForeignPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, validTaxYear, foreignPropertyBodyWithConsolidatedEntry).validateAndWrapResult()

        result shouldBe Right(
          Def4_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedForeignPropertyBodyWithConsolidatedEntry)
        )
      }

      "a valid request with only zero adjustments set to true is supplied" in {
        val result =
          validator(validNino, validCalculationId, validTaxYear, mtdRequestWithOnlyZeroAdjustments(true)).validateAndWrapResult()

        result shouldBe Right(
          Def4_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedWithOnlyZeroAdjustmentsBody)
        )
      }
    }

    "return NinoFormatError" when {
      "passed an invalid nino" in {
        val result = validator("A12344A", validCalculationId, validTaxYear, foreignPropertyBody).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return CalculationIdFormatError" when {
      "passed an invalid calculationId" in {
        val result = validator(validNino, "12345", validTaxYear, foreignPropertyBody).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, CalculationIdFormatError)
        )
      }
    }

    "return TaxYearFormatError" when {
      "passed a badly formatted tax year" in {
        val result = validator(validNino, validCalculationId, "not-a-tax-year", foreignPropertyBody).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError" when {
      "passed a tax year range of more than one year" in {
        val result = validator(validNino, validCalculationId, "2022-24", foreignPropertyBody).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return RuleIncorrectOrEmptyBodyError" when {
      "passed an empty body" in {
        val body   = Json.parse("{}")
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }
    }

    "return ValueFormatError" when {
      "income or (non-consolidated) expenses is invalid" when {

        List(
          (
            (v: JsNumber) => foreignPropertyBodyWith(entry.update("/income/totalRentsReceived", v)),
            "/foreignProperty/propertyLevelDetail/0/income/totalRentsReceived"),
          (
            (v: JsNumber) => foreignPropertyBodyWith(entry.update("/income/premiumsOfLeaseGrant", v)),
            "/foreignProperty/propertyLevelDetail/0/income/premiumsOfLeaseGrant"),
          (
            (v: JsNumber) => foreignPropertyBodyWith(entry.update("/income/otherPropertyIncome", v)),
            "/foreignProperty/propertyLevelDetail/0/income/otherPropertyIncome"),
          (
            (v: JsNumber) => foreignPropertyBodyWith(entry.update("/expenses/premisesRunningCosts", v)),
            "/foreignProperty/propertyLevelDetail/0/expenses/premisesRunningCosts"),
          (
            (v: JsNumber) => foreignPropertyBodyWith(entry.update("/expenses/repairsAndMaintenance", v)),
            "/foreignProperty/propertyLevelDetail/0/expenses/repairsAndMaintenance"),
          (
            (v: JsNumber) => foreignPropertyBodyWith(entry.update("/expenses/financialCosts", v)),
            "/foreignProperty/propertyLevelDetail/0/expenses/financialCosts"),
          (
            (v: JsNumber) => foreignPropertyBodyWith(entry.update("/expenses/professionalFees", v)),
            "/foreignProperty/propertyLevelDetail/0/expenses/professionalFees"),
          (
            (v: JsNumber) => foreignPropertyBodyWith(entry.update("/expenses/costOfServices", v)),
            "/foreignProperty/propertyLevelDetail/0/expenses/costOfServices"),
          ((v: JsNumber) => foreignPropertyBodyWith(entry.update("/expenses/other", v)), "/foreignProperty/propertyLevelDetail/0/expenses/other"),
          (
            (v: JsNumber) => foreignPropertyBodyWith(entry.update("/expenses/travelCosts", v)),
            "/foreignProperty/propertyLevelDetail/0/expenses/travelCosts"),
          (
            (v: JsNumber) => foreignPropertyBodyWith(entry.update("/expenses/residentialFinancialCost", v)),
            "/foreignProperty/propertyLevelDetail/0/expenses/residentialFinancialCost")
        ).foreach { case (body, path) => testWith(body, path) }

      }

      "consolidated expenses is invalid" when {
        List(
          (
            (v: JsNumber) => foreignPropertyBodyWith(entryConsolidated.update("/expenses/consolidatedExpenses", v)),
            "/foreignProperty/propertyLevelDetail/0/expenses/consolidatedExpenses")
        ).foreach { case (body, path) => testWith(body, path) }
      }

      "multiple fields are invalid" in {
        val path1 = "/foreignProperty/propertyLevelDetail/0/expenses/travelCosts"
        val path2 = "/foreignProperty/propertyLevelDetail/1/income/totalRentsReceived"

        val json =
          foreignPropertyBodyWith(
            entry.update("/expenses/travelCosts", JsNumber(0)),
            entry.update("/income/totalRentsReceived", JsNumber(123.123))
          )

        val result = validator(validNino, validCalculationId, validTaxYear, json).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            ValueFormatError.copy(
              paths = Some(List(path1, path2)),
              message = "The value must be between -99999999999.99 and 99999999999.99 (but cannot be 0 or 0.00)")
          )
        )
      }

      def testWith(body: JsNumber => JsValue, expectedPath: String): Unit =
        s"for $expectedPath" when {
          def doTest(value: JsNumber) = {
            val result = validator(validNino, validCalculationId, validTaxYear, body(value)).validateAndWrapResult()

            result shouldBe Left(
              ErrorWrapper(
                correlationId,
                ValueFormatError.forPathAndRangeExcludeZero(expectedPath, "-99999999999.99", "99999999999.99")
              )
            )
          }

          "value is out of range" in doTest(JsNumber(99999999999.99 + 0.01))

          "value is zero" in doTest(JsNumber(0))
        }
    }

    "return RuleBothExpensesSuppliedError" when {
      "passed consolidated and separate non-fhl expenses" in {
        val body = foreignPropertyBodyWith(
          entry.update("expenses/consolidatedExpenses", JsNumber(123.45)),
          entry.update("expenses/consolidatedExpenses", JsNumber(123.45))
        )
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleBothExpensesError.withPaths(
              List("/foreignProperty/propertyLevelDetail/0/expenses", "/foreignProperty/propertyLevelDetail/1/expenses")
            )
          )
        )
      }
    }

    "return RuleZeroAdjustmentsInvalidError" when {
      "passed only zero adjustments as false" in {
        val result = validator(
          validNino,
          validCalculationId,
          validTaxYear,
          mtdRequestWithOnlyZeroAdjustments(false)
        ).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleZeroAdjustmentsInvalidError.withPath("/foreignProperty/zeroAdjustments")
          )
        )
      }
    }

    "return RuleBothAdjustmentsSuppliedError" when {
      "passed zero adjustments as true and other adjustments" in {
        val result = validator(
          validNino,
          validCalculationId,
          validTaxYear,
          mtdRequestWithZeroAndOtherAdjustments(true)
        ).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleBothAdjustmentsSuppliedError.withPath("/foreignProperty")
          )
        )
      }
    }

    "return multiple errors" when {
      "passed a request containing multiple errors" in {
        val result = validator("A12344A", "not-a-calculation-id", validTaxYear, foreignPropertyBody).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(CalculationIdFormatError, NinoFormatError))
          )
        )
      }

      "passed zero adjustments as false and other adjustments" in {
        val result = validator(
          validNino,
          validCalculationId,
          validTaxYear,
          mtdRequestWithZeroAndOtherAdjustments(false)
        ).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(
              List(
                RuleBothAdjustmentsSuppliedError.withPath("/foreignProperty"),
                RuleZeroAdjustmentsInvalidError.withPath("/foreignProperty/zeroAdjustments")
              )
            )
          )
        )
      }
    }
  }

}
