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

package v5.foreignPropertyBsas.submit.def2

import common.errors.{RuleBothExpensesError, RuleBothPropertiesSuppliedError, RuleDuplicateCountryCodeError}
import play.api.libs.json.*
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors.*
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v5.foreignPropertyBsas.submit.def2.model.request.{Def2_SubmitForeignPropertyBsasRequestBody, Def2_SubmitForeignPropertyBsasRequestData}

class Def2_SubmitForeignPropertyBsasValidatorSpec extends UnitSpec with JsonErrorValidators {

  private implicit val correlationId: String = "1234"

  private val validNino          = "AA123456A"
  private val validCalculationId = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  private val validTaxYear       = "2023-24"

  private val parsedNino          = Nino(validNino)
  private val parsedCalculationId = CalculationId(validCalculationId)
  private val parsedTaxYear       = TaxYear.fromMtd(validTaxYear)

  private def entryWith(countryCode: String) =
    Json.parse(s"""
      |{
      |  "countryCode": "$countryCode",
      |  "income": {
      |    "totalRentsReceived": 1000.45,
      |    "premiumsOfLeaseGrant": -99.99,
      |    "otherPropertyIncome": 1000.00
      |  },
      |  "expenses": {
      |    "premisesRunningCosts": 1000.45,
      |    "repairsAndMaintenance": -99999.99,
      |    "financialCosts": 5000.45,
      |    "professionalFees": 300.99,
      |    "costOfServices": 500.00,
      |    "residentialFinancialCost": 9000.00,
      |    "other": 1000.00,
      |    "travelCosts": 99.99
      |  }
      |}""".stripMargin)

  private val entry = entryWith(countryCode = "AFG")

  private val entryConsolidated =
    Json.parse("""
      |{
      |  "countryCode": "AFG",
      |  "income": {
      |    "totalRentsReceived": 1000.45,
      |    "premiumsOfLeaseGrant": -99.99,
      |    "otherPropertyIncome": 1000.00
      |  },
      |  "expenses": {
      |    "consolidatedExpenses": 332.78
      |  }
      |}""".stripMargin)

  private def nonFhlBodyWith(nonFhlEntries: JsValue*): JsObject =
    Json
      .parse(
        s"""{
         |  "nonFurnishedHolidayLet": ${JsArray(nonFhlEntries)}
         |}
         |""".stripMargin
      )
      .as[JsObject]

  private val nonFhlBody: JsValue = nonFhlBodyWith(entry)
  private val parsedNonFhlBody    = nonFhlBody.as[Def2_SubmitForeignPropertyBsasRequestBody]

  private val fhlBodyJson =
    Json
      .parse(
        s"""
         |{
         |  "foreignFhlEea": {
         |    "income": {
         |      "totalRentsReceived": 1000.45
         |    },
         |    "expenses": {
         |      "premisesRunningCosts": 1001.00,
         |      "repairsAndMaintenance": -99999.99,
         |      "financialCosts": 200.50,
         |      "professionalFees": -99999.99,
         |      "costOfServices": -1000.45,
         |      "other": 500.00,
         |      "travelCosts": 100.00
         |    }
         |  }
         |}
         |""".stripMargin
      )
      .as[JsObject]

  private val parsedFhlBody = fhlBodyJson.as[Def2_SubmitForeignPropertyBsasRequestBody]

  private val fhlBodyConsolidated = Json.parse(
    s"""{
       |  "foreignFhlEea": {
       |    "income": {
       |      "totalRentsReceived": 1000.45
       |    },
       |    "expenses": {
       |      "consolidatedExpenses": 1000.45
       |    }
       |  }
       |}
       |""".stripMargin
  )

  private val parsedFhlBodyConsolidated = fhlBodyConsolidated.as[Def2_SubmitForeignPropertyBsasRequestBody]

  private def validator(nino: String, calculationId: String, taxYear: Option[String], body: JsValue) =
    new Def2_SubmitForeignPropertyBsasValidator(nino, calculationId, taxYear, body)

  "running a validation" should {
    "return the parsed domain object" when {

      "passed a valid fhl request" in {
        val result = validator(validNino, validCalculationId, None, fhlBodyJson).validateAndWrapResult()

        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, None, parsedFhlBody)
        )
      }

      "passed a valid non-fhl request" in {
        val result = validator(validNino, validCalculationId, None, nonFhlBody).validateAndWrapResult()
        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, None, parsedNonFhlBody)
        )
      }

      "passed a valid fhl consolidated expenses request" in {
        val result = validator(validNino, validCalculationId, None, fhlBodyConsolidated).validateAndWrapResult()
        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, None, parsedFhlBodyConsolidated)
        )
      }

      "passed a valid non-fhl consolidated expenses request" in {
        val nonFhlBodyConsolidated       = nonFhlBodyWith(entryConsolidated)
        val parsedNonFhlBodyConsolidated = nonFhlBodyConsolidated.as[Def2_SubmitForeignPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, None, nonFhlBodyConsolidated).validateAndWrapResult()

        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, None, parsedNonFhlBodyConsolidated)
        )
      }

      "a minimal fhl request is supplied" in {
        val minimalFhlBody =
          Json.parse(
            """
            |{
            |  "foreignFhlEea": {
            |    "income": {
            |      "totalRentsReceived": 1000.45
            |    }
            |  }
            |}
            |""".stripMargin
          )
        val parsedMinimalFhlBody = minimalFhlBody.as[Def2_SubmitForeignPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, None, minimalFhlBody).validateAndWrapResult()

        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, None, parsedMinimalFhlBody)
        )
      }

      "a minimal non-fhl request is supplied" in {
        val minimalNonFhlBody = Json.parse("""
          |{
          |   "nonFurnishedHolidayLet":  [
          |       {
          |          "countryCode": "FRA",
          |          "income": {
          |              "totalRentsReceived": 1000.45
          |          }
          |       }
          |    ]
          |}
          |""".stripMargin)
        val parsedMinimalNonFhlBody = minimalNonFhlBody.as[Def2_SubmitForeignPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, None, minimalNonFhlBody).validateAndWrapResult()

        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, None, parsedMinimalNonFhlBody)
        )
      }

      "a valid request with a taxYear is supplied" in {
        val nonFhlBodyWithConsolidatedEntry       = nonFhlBodyWith(entryConsolidated)
        val parsedNonFhlBodyWithConsolidatedEntry = nonFhlBodyWithConsolidatedEntry.as[Def2_SubmitForeignPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, Some(validTaxYear), nonFhlBodyWithConsolidatedEntry).validateAndWrapResult()

        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, Some(parsedTaxYear), parsedNonFhlBodyWithConsolidatedEntry)
        )
      }
    }

    "return NinoFormatError" when {
      "passed an invalid nino" in {
        val result = validator("A12344A", validCalculationId, None, fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return CalculationIdFormatError" when {
      "passed an invalid calculationId" in {
        val result = validator(validNino, "12345", None, fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, CalculationIdFormatError)
        )
      }
    }

    "return InvalidTaxYearParameterError" when {
      "passed a tax year before TYS" in {
        val result = validator(validNino, validCalculationId, Some("2022-23"), fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, InvalidTaxYearParameterError)
        )
      }
    }

    "return RuleTaxYearNotSupportedError" when {
      "given a tax year after 2024-25" in {
        val result = validator(validNino, validCalculationId, Some("2025-26"), fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearNotSupportedError)
        )
      }
    }

    "return TaxYearFormatError" when {
      "passed a badly formatted tax year" in {
        val result = validator(validNino, validCalculationId, Some("not-a-tax-year"), fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError" when {
      "passed a tax year range of more than one year" in {
        val result = validator(validNino, validCalculationId, Some("2022-24"), fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return RuleBothPropertiesSuppliedError" when {
      "passed both fhl and non-fhl" in {
        val body   = fhlBodyJson ++ nonFhlBodyWith(entry)
        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleBothPropertiesSuppliedError)
        )
      }

      "passed both fhl and non-fhl even if they are empty" in {
        // Note: no other errors should be returned
        val body = Json.parse(
          s"""
               |{
               |  "foreignFhlEea": {},
               |  "nonFurnishedHolidayLet": []
               |}
               |""".stripMargin
        )
        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleBothPropertiesSuppliedError)
        )
      }
    }

    "return RuleIncorrectOrEmptyBodyError" when {
      "passed an empty body" in {
        val body   = Json.parse("{}")
        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }

      "an object/array is empty or a mandatory field is missing" when {
        List(
          "/foreignFhlEea",
          "/foreignFhlEea/income",
          "/foreignFhlEea/expenses"
        ).foreach(path => testWith(fhlBodyJson.replaceWithEmptyObject(path), path))

        List(
          (nonFhlBodyWith(), "/nonFurnishedHolidayLet"),
          (nonFhlBodyWith(entry.replaceWithEmptyObject("/income")), "/nonFurnishedHolidayLet/0/income"),
          (nonFhlBodyWith(entry.replaceWithEmptyObject("/expenses")), "/nonFurnishedHolidayLet/0/expenses"),
          (nonFhlBodyWith(entry.removeProperty("/countryCode")), "/nonFurnishedHolidayLet/0/countryCode"),
          (nonFhlBodyWith(entry.removeProperty("/income").removeProperty("/expenses")), "/nonFurnishedHolidayLet/0")
        ).foreach(testWith.tupled)

        def testWith(body: JsValue, expectedPath: String): Unit =
          s"for $expectedPath" in {
            val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()
            result shouldBe Left(
              ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath(expectedPath))
            )
          }
      }

      "an object is empty except for an additional (non-schema) property" in {
        val body = Json.parse("""
          |{
          |    "foreignFhlEea":{
          |       "unknownField": 999.99
          |    }
          |}""".stripMargin)

        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/foreignFhlEea"))
        )
      }

      "return ValueFormatError" when {
        "income or (non-consolidated) expenses is invalid" when {
          List(
            "/foreignFhlEea/income/totalRentsReceived",
            "/foreignFhlEea/expenses/premisesRunningCosts",
            "/foreignFhlEea/expenses/repairsAndMaintenance",
            "/foreignFhlEea/expenses/financialCosts",
            "/foreignFhlEea/expenses/professionalFees",
            "/foreignFhlEea/expenses/costOfServices",
            "/foreignFhlEea/expenses/other",
            "/foreignFhlEea/expenses/travelCosts"
          ).foreach(path => testWith(fhlBodyJson.update(path, _), path))

          List(
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/income/totalRentsReceived", v)), "/nonFurnishedHolidayLet/0/income/totalRentsReceived"),
            (
              (v: JsNumber) => nonFhlBodyWith(entry.update("/income/premiumsOfLeaseGrant", v)),
              "/nonFurnishedHolidayLet/0/income/premiumsOfLeaseGrant"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/income/otherPropertyIncome", v)), "/nonFurnishedHolidayLet/0/income/otherPropertyIncome"),
            (
              (v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/premisesRunningCosts", v)),
              "/nonFurnishedHolidayLet/0/expenses/premisesRunningCosts"),
            (
              (v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/repairsAndMaintenance", v)),
              "/nonFurnishedHolidayLet/0/expenses/repairsAndMaintenance"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/financialCosts", v)), "/nonFurnishedHolidayLet/0/expenses/financialCosts"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/professionalFees", v)), "/nonFurnishedHolidayLet/0/expenses/professionalFees"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/costOfServices", v)), "/nonFurnishedHolidayLet/0/expenses/costOfServices"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/other", v)), "/nonFurnishedHolidayLet/0/expenses/other"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/travelCosts", v)), "/nonFurnishedHolidayLet/0/expenses/travelCosts")
          ).foreach { case (body, path) => testWith(body, path) }

          List(
            (
              (v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/residentialFinancialCost", v)),
              "/nonFurnishedHolidayLet/0/expenses/residentialFinancialCost")).foreach { case (body, path) => testWith(body, path, min = "0") }
        }

        "consolidated expenses is invalid" when {
          List(
            "/foreignFhlEea/expenses/consolidatedExpenses"
          ).foreach(path => testWith(fhlBodyConsolidated.update(path, _), path))

          List(
            (
              (v: JsNumber) => nonFhlBodyWith(entryConsolidated.update("/expenses/consolidatedExpenses", v)),
              "/nonFurnishedHolidayLet/0/expenses/consolidatedExpenses")
          ).foreach { case (body, path) => testWith(body, path) }
        }

        "multiple fields are invalid" in {
          val path1 = "/nonFurnishedHolidayLet/0/expenses/travelCosts"
          val path2 = "/nonFurnishedHolidayLet/1/income/totalRentsReceived"

          val json =
            nonFhlBodyWith(
              entryWith(countryCode = "ZWE").update("/expenses/travelCosts", JsNumber(0)),
              entryWith(countryCode = "AFG").update("/income/totalRentsReceived", JsNumber(123.123))
            )

          val result = validator(validNino, validCalculationId, None, json).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(
              correlationId,
              ValueFormatError.copy(
                paths = Some(List(path1, path2)),
                message = "The value must be between -99999999999.99 and 99999999999.99 (but cannot be 0 or 0.00)")
            )
          )
        }

        def testWith(body: JsNumber => JsValue, expectedPath: String, min: String = "-99999999999.99", max: String = "99999999999.99"): Unit =
          s"for $expectedPath" when {
            def doTest(value: JsNumber) = {
              val result = validator(validNino, validCalculationId, None, body(value)).validateAndWrapResult()

              result shouldBe Left(
                ErrorWrapper(
                  correlationId,
                  ValueFormatError.forPathAndRangeExcludeZero(expectedPath, min, max)
                )
              )
            }

            "value is out of range" in doTest(JsNumber(99999999999.99 + 0.01))

            "value is zero" in doTest(JsNumber(0))
          }
      }

      "return RuleCountryCodeError" when {
        "passed an invalid country code" in {
          val body   = nonFhlBodyWith(entryWith(countryCode = "QQQ"))
          val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(correlationId, RuleCountryCodeError.withPath("/nonFurnishedHolidayLet/0/countryCode"))
          )
        }

        "passed multiple invalid country codes" in {
          val body   = nonFhlBodyWith(entryWith(countryCode = "QQQ"), entryWith(countryCode = "AAA"))
          val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(
              correlationId,
              RuleCountryCodeError.withPaths(List("/nonFurnishedHolidayLet/0/countryCode", "/nonFurnishedHolidayLet/1/countryCode")))
          )
        }
      }

      "return RuleDuplicateCountryCodeError" when {
        "a country code is duplicated" in {
          val code   = "ZWE"
          val body   = nonFhlBodyWith(entryWith(code), entryWith(code))
          val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(
              correlationId,
              RuleDuplicateCountryCodeError.forDuplicatedCodesAndPaths(
                code = code,
                paths = List("/nonFurnishedHolidayLet/0/countryCode", "/nonFurnishedHolidayLet/1/countryCode"))
            )
          )
        }

        "multiple country codes are duplicated" in {
          val code1  = "AFG"
          val code2  = "ZWE"
          val body   = nonFhlBodyWith(entryWith(code1), entryWith(code2), entryWith(code1), entryWith(code2))
          val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(
              correlationId,
              BadRequestError,
              Some(List(
                RuleDuplicateCountryCodeError
                  .forDuplicatedCodesAndPaths(
                    code = code1,
                    paths = List("/nonFurnishedHolidayLet/0/countryCode", "/nonFurnishedHolidayLet/2/countryCode")),
                RuleDuplicateCountryCodeError
                  .forDuplicatedCodesAndPaths(
                    code = code2,
                    paths = List("/nonFurnishedHolidayLet/1/countryCode", "/nonFurnishedHolidayLet/3/countryCode"))
              ))
            )
          )
        }
      }

      "return RuleBothExpensesSuppliedError" when {
        "passed consolidated and separate fhl expenses" in {
          val body   = fhlBodyJson.update("foreignFhlEea/expenses/consolidatedExpenses", JsNumber(123.45))
          val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(correlationId, RuleBothExpensesError.withPath("/foreignFhlEea/expenses"))
          )
        }

        "passed consolidated and separate non-fhl expenses" in {
          val body = nonFhlBodyWith(
            entryWith(countryCode = "ZWE").update("expenses/consolidatedExpenses", JsNumber(123.45)),
            entryWith(countryCode = "AFG").update("expenses/consolidatedExpenses", JsNumber(123.45))
          )
          val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(
              correlationId,
              RuleBothExpensesError.withPaths(List("/nonFurnishedHolidayLet/0/expenses", "/nonFurnishedHolidayLet/1/expenses"))
            )
          )
        }
      }

      "return multiple errors" when {
        "passed a request containing multiple errors" in {
          val result = validator("A12344A", "not-a-calculation-id", None, fhlBodyJson).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(
              correlationId,
              BadRequestError,
              Some(List(CalculationIdFormatError, NinoFormatError))
            )
          )
        }
      }
    }
  }

}
