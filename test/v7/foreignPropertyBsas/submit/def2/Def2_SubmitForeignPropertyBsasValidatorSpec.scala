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

package v7.foreignPropertyBsas.submit.def2

import common.errors._
import play.api.libs.json._
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors._
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v7.foreignPropertyBsas.submit.def2.model.request.{Def2_SubmitForeignPropertyBsasRequestBody, Def2_SubmitForeignPropertyBsasRequestData}
import v7.foreignPropertyBsas.submit.def2.model.request.SubmitForeignPropertyBsasFixtures._

class Def2_SubmitForeignPropertyBsasValidatorSpec extends UnitSpec with JsonErrorValidators {

  private implicit val correlationId: String = "1234"

  private val validNino          = "AA123456A"
  private val validCalculationId = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  private val validTaxYear       = "2024-25"

  private val parsedNino          = Nino(validNino)
  private val parsedCalculationId = CalculationId(validCalculationId)
  private val parsedTaxYear       = TaxYear.fromMtd(validTaxYear)

  private def entryWith(countryCode: String) =
    Json.parse(
      s"""
        |{
        |    "countryCode": "$countryCode",
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

  private val entry = entryWith(countryCode = "AFG")

  private val entryConsolidated =
    Json.parse(
      """
        |{
        |    "countryCode": "AFG",
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

  private def bodyWith(entries: JsValue*): JsObject =
    Json
      .parse(
        s"""
        |{
        |    "foreignProperty": {
        |        "countryLevelDetail": ${JsArray(entries)}
        |    }
        |}
      """.stripMargin
      )
      .as[JsObject]

  private val body: JsValue = bodyWith(entry)
  private val parsedBody    = body.as[Def2_SubmitForeignPropertyBsasRequestBody]

  private val fhlBodyJson =
    Json
      .parse(
        """
        |{
        |    "foreignFhlEea": {
        |        "income": {
        |            "totalRentsReceived": 1000.45
        |        },
        |        "expenses": {
        |            "premisesRunningCosts": 1001.00,
        |            "repairsAndMaintenance": -99999.99,
        |            "financialCosts": 200.50,
        |            "professionalFees": -99999.99,
        |            "costOfServices": -1000.45,
        |            "other": 500.00,
        |            "travelCosts": 100.00
        |        }
        |    }
        |}
      """.stripMargin
      )
      .as[JsObject]

  private val parsedFhlBody = fhlBodyJson.as[Def2_SubmitForeignPropertyBsasRequestBody]

  private val fhlBodyConsolidated = Json.parse(
    """
      |{
      |    "foreignFhlEea": {
      |        "income": {
      |            "totalRentsReceived": 1000.45
      |        },
      |        "expenses": {
      |            "consolidatedExpenses": 1000.45
      |        }
      |    }
      |}
    """.stripMargin
  )

  private val parsedFhlBodyConsolidated = fhlBodyConsolidated.as[Def2_SubmitForeignPropertyBsasRequestBody]

  private val propertyTypes: List[String] = List("foreignProperty", "foreignFhlEea")

  private def parsedWithOnlyZeroAdjustmentsBody(propertyType: String): Def2_SubmitForeignPropertyBsasRequestBody =
    mtdRequestWithOnlyZeroAdjustments(propertyType, zeroAdjustments = true).as[Def2_SubmitForeignPropertyBsasRequestBody]

  private def validator(nino: String, calculationId: String, taxYear: String, body: JsValue) =
    new Def2_SubmitForeignPropertyBsasValidator(nino, calculationId, taxYear, body)

  "running a validation" should {
    "return the parsed domain object" when {

      "passed a valid fhl request" in {
        val result = validator(validNino, validCalculationId, validTaxYear, fhlBodyJson).validateAndWrapResult()

        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedFhlBody)
        )
      }

      "passed a valid non-fhl request" in {
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()
        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedBody)
        )
      }

      "passed a valid fhl consolidated expenses request" in {
        val result = validator(validNino, validCalculationId, validTaxYear, fhlBodyConsolidated).validateAndWrapResult()
        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedFhlBodyConsolidated)
        )
      }

      "passed a valid non-fhl consolidated expenses request" in {
        val bodyConsolidated       = bodyWith(entryConsolidated)
        val parsedBodyConsolidated = bodyConsolidated.as[Def2_SubmitForeignPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, validTaxYear, bodyConsolidated).validateAndWrapResult()

        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedBodyConsolidated)
        )
      }

      "a minimal fhl request is supplied" in {
        val minimalFhlBody =
          Json.parse(
            """
              |{
              |    "foreignFhlEea": {
              |        "income": {
              |            "totalRentsReceived": 1000.45
              |        }
              |    }
              |}
            """.stripMargin
          )
        val parsedMinimalFhlBody = minimalFhlBody.as[Def2_SubmitForeignPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, validTaxYear, minimalFhlBody).validateAndWrapResult()

        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedMinimalFhlBody)
        )
      }

      "a minimal non-fhl request is supplied" in {
        val minimalBody = Json.parse(
          """
            |{
            |    "foreignProperty": {
            |        "countryLevelDetail": [
            |            {
            |                "countryCode": "FRA",
            |                "income": {
            |                    "totalRentsReceived": 1000.45
            |                }
            |            }
            |        ]
            |    }
            |}
          """.stripMargin
        )
        val parsedMinimalBody = minimalBody.as[Def2_SubmitForeignPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, validTaxYear, minimalBody).validateAndWrapResult()

        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedMinimalBody)
        )
      }

      "a valid request with a taxYear is supplied" in {
        val bodyWithConsolidatedEntry       = bodyWith(entryConsolidated)
        val parsedBodyWithConsolidatedEntry = bodyWithConsolidatedEntry.as[Def2_SubmitForeignPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, validTaxYear, bodyWithConsolidatedEntry).validateAndWrapResult()

        result shouldBe Right(
          Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedBodyWithConsolidatedEntry)
        )
      }

      propertyTypes.foreach { propertyType =>
        s"a valid $propertyType request with only zero adjustments set to true is supplied" in {
          val result =
            validator(
              validNino,
              validCalculationId,
              validTaxYear,
              mtdRequestWithOnlyZeroAdjustments(propertyType, zeroAdjustments = true)
            ).validateAndWrapResult()

          result shouldBe Right(
            Def2_SubmitForeignPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedWithOnlyZeroAdjustmentsBody(propertyType))
          )
        }
      }
    }

    "return NinoFormatError" when {
      "passed an invalid nino" in {
        val result = validator("A12344A", validCalculationId, validTaxYear, fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return CalculationIdFormatError" when {
      "passed an invalid calculationId" in {
        val result = validator(validNino, "12345", validTaxYear, fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, CalculationIdFormatError)
        )
      }
    }

    "return TaxYearFormatError" when {
      "passed a badly formatted tax year" in {
        val result = validator(validNino, validCalculationId, "not-a-tax-year", fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError" when {
      "passed a tax year range of more than one year" in {
        val result = validator(validNino, validCalculationId, "2022-24", fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return RuleBothPropertiesSuppliedError" when {
      "passed both fhl and non-fhl" in {
        val body   = fhlBodyJson ++ bodyWith(entry)
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleBothPropertiesSuppliedError)
        )
      }

      "passed both fhl and non-fhl even if they are empty" in {
        val body = Json.parse(
          """
            |{
            |    "foreignFhlEea": {},
            |    "foreignProperty": {}
            |}
          """.stripMargin
        )
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleBothPropertiesSuppliedError)
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

      "an object/array is empty or a mandatory field is missing" when {
        List(
          "/foreignFhlEea",
          "/foreignFhlEea/income",
          "/foreignFhlEea/expenses"
        ).foreach(path => testWith(fhlBodyJson.replaceWithEmptyObject(path), path))

        List(
          (bodyWith(), "/foreignProperty/countryLevelDetail"),
          (bodyWith(entry.replaceWithEmptyObject("/income")), "/foreignProperty/countryLevelDetail/0/income"),
          (bodyWith(entry.replaceWithEmptyObject("/expenses")), "/foreignProperty/countryLevelDetail/0/expenses"),
          (bodyWith(entry.removeProperty("/countryCode")), "/foreignProperty/countryLevelDetail/0/countryCode"),
          (bodyWith(entry.removeProperty("/income").removeProperty("/expenses")), "/foreignProperty/countryLevelDetail/0")
        ).foreach((testWith _).tupled)

        def testWith(body: JsValue, expectedPath: String): Unit =
          s"for $expectedPath" in {
            val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()
            result shouldBe Left(
              ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath(expectedPath))
            )
          }
      }

      "an object is empty except for an additional (non-schema) property" in {
        val body = Json.parse(
          """
            |{
            |    "foreignFhlEea": {
            |        "unknownField": 999.99
            |    }
            |}
          """.stripMargin
        )

        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/foreignFhlEea"))
        )
      }
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
          (
            (v: JsNumber) => bodyWith(entry.update("/income/totalRentsReceived", v)),
            "/foreignProperty/countryLevelDetail/0/income/totalRentsReceived"),
          (
            (v: JsNumber) => bodyWith(entry.update("/income/premiumsOfLeaseGrant", v)),
            "/foreignProperty/countryLevelDetail/0/income/premiumsOfLeaseGrant"),
          (
            (v: JsNumber) => bodyWith(entry.update("/income/otherPropertyIncome", v)),
            "/foreignProperty/countryLevelDetail/0/income/otherPropertyIncome"),
          (
            (v: JsNumber) => bodyWith(entry.update("/expenses/premisesRunningCosts", v)),
            "/foreignProperty/countryLevelDetail/0/expenses/premisesRunningCosts"),
          (
            (v: JsNumber) => bodyWith(entry.update("/expenses/repairsAndMaintenance", v)),
            "/foreignProperty/countryLevelDetail/0/expenses/repairsAndMaintenance"),
          ((v: JsNumber) => bodyWith(entry.update("/expenses/financialCosts", v)), "/foreignProperty/countryLevelDetail/0/expenses/financialCosts"),
          (
            (v: JsNumber) => bodyWith(entry.update("/expenses/professionalFees", v)),
            "/foreignProperty/countryLevelDetail/0/expenses/professionalFees"),
          ((v: JsNumber) => bodyWith(entry.update("/expenses/costOfServices", v)), "/foreignProperty/countryLevelDetail/0/expenses/costOfServices"),
          ((v: JsNumber) => bodyWith(entry.update("/expenses/other", v)), "/foreignProperty/countryLevelDetail/0/expenses/other"),
          ((v: JsNumber) => bodyWith(entry.update("/expenses/travelCosts", v)), "/foreignProperty/countryLevelDetail/0/expenses/travelCosts")
        ).foreach { case (body, path) => testWith(body, path) }

        List(
          (
            (v: JsNumber) => bodyWith(entry.update("/expenses/residentialFinancialCost", v)),
            "/foreignProperty/countryLevelDetail/0/expenses/residentialFinancialCost")
        ).foreach { case (body, path) => testWith(body, path, min = "0") }
      }

      "consolidated expenses is invalid" when {
        List(
          "/foreignFhlEea/expenses/consolidatedExpenses"
        ).foreach(path => testWith(fhlBodyConsolidated.update(path, _), path))

        List(
          (
            (v: JsNumber) => bodyWith(entryConsolidated.update("/expenses/consolidatedExpenses", v)),
            "/foreignProperty/countryLevelDetail/0/expenses/consolidatedExpenses")
        ).foreach { case (body, path) => testWith(body, path) }
      }

      "multiple fields are invalid" in {
        val path1 = "/foreignProperty/countryLevelDetail/0/expenses/travelCosts"
        val path2 = "/foreignProperty/countryLevelDetail/1/income/totalRentsReceived"

        val json =
          bodyWith(
            entryWith(countryCode = "ZWE").update("/expenses/travelCosts", JsNumber(0)),
            entryWith(countryCode = "AFG").update("/income/totalRentsReceived", JsNumber(123.123))
          )

        val result = validator(validNino, validCalculationId, validTaxYear, json).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            ValueFormatError.copy(
              paths = Some(List(path1, path2)), message = "The value must be between -99999999999.99 and 99999999999.99 (but cannot be 0 or 0.00)")
          )
        )
      }

      def testWith(body: JsNumber => JsValue, expectedPath: String, min: String = "-99999999999.99", max: String = "99999999999.99"): Unit =
        s"for $expectedPath" when {
          def doTest(value: JsNumber) = {
            val result = validator(validNino, validCalculationId, validTaxYear, body(value)).validateAndWrapResult()

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
        val body   = bodyWith(entryWith(countryCode = "QQQ"))
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleCountryCodeError.withPath("/foreignProperty/countryLevelDetail/0/countryCode"))
        )
      }

      "passed multiple invalid country codes" in {
        val body   = bodyWith(entryWith(countryCode = "QQQ"), entryWith(countryCode = "AAA"))
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleCountryCodeError.withPaths(
              List("/foreignProperty/countryLevelDetail/0/countryCode", "/foreignProperty/countryLevelDetail/1/countryCode"))
          )
        )
      }
    }

    "return RuleDuplicateCountryCodeError" when {
      "a country code is duplicated" in {
        val code   = "ZWE"
        val body   = bodyWith(entryWith(code), entryWith(code))
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleDuplicateCountryCodeError.forDuplicatedCodesAndPaths(
              code = code,
              paths = List(
                "/foreignProperty/countryLevelDetail/0/countryCode",
                "/foreignProperty/countryLevelDetail/1/countryCode"
              )
            )
          )
        )
      }

      "multiple country codes are duplicated" in {
        val code1  = "AFG"
        val code2  = "ZWE"
        val body   = bodyWith(entryWith(code1), entryWith(code2), entryWith(code1), entryWith(code2))
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(
              RuleDuplicateCountryCodeError
                .forDuplicatedCodesAndPaths(
                  code = code1,
                  paths = List(
                    "/foreignProperty/countryLevelDetail/0/countryCode",
                    "/foreignProperty/countryLevelDetail/2/countryCode"
                  )
                ),
              RuleDuplicateCountryCodeError
                .forDuplicatedCodesAndPaths(
                  code = code2,
                  paths = List(
                    "/foreignProperty/countryLevelDetail/1/countryCode",
                    "/foreignProperty/countryLevelDetail/3/countryCode"
                  )
                )
            ))
          )
        )
      }
    }

    "return RuleBothExpensesSuppliedError" when {
      "passed consolidated and separate fhl expenses" in {
        val body   = fhlBodyJson.update("foreignFhlEea/expenses/consolidatedExpenses", JsNumber(123.45))
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleBothExpensesError.withPath("/foreignFhlEea/expenses"))
        )
      }

      "passed consolidated and separate non-fhl expenses" in {
        val body = bodyWith(
          entryWith(countryCode = "ZWE").update("expenses/consolidatedExpenses", JsNumber(123.45)),
          entryWith(countryCode = "AFG").update("expenses/consolidatedExpenses", JsNumber(123.45))
        )
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleBothExpensesError.withPaths(
              List("/foreignProperty/countryLevelDetail/0/expenses", "/foreignProperty/countryLevelDetail/1/expenses")
            )
          )
        )
      }
    }

    "return RuleZeroAdjustmentsInvalidError" when {
      propertyTypes.foreach { propertyType =>
        s"passed only zero adjustments as false in $propertyType" in {
          val result = validator(
            validNino,
            validCalculationId,
            validTaxYear,
            mtdRequestWithOnlyZeroAdjustments(propertyType, zeroAdjustments = false)
          ).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(correlationId, RuleZeroAdjustmentsInvalidError.withPath(s"/$propertyType/zeroAdjustments"))
          )
        }
      }
    }

    "return RuleBothAdjustmentsSuppliedError" when {
      propertyTypes.foreach { propertyType =>
        s"passed zero adjustments as true and other adjustments in $propertyType" in {
          val result = validator(
            validNino,
            validCalculationId,
            validTaxYear,
            mtdRequestWithZeroAndOtherAdjustments(propertyType, zeroAdjustments = true)
          ).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(correlationId, RuleBothAdjustmentsSuppliedError.withPath(s"/$propertyType"))
          )
        }
      }
    }

    "return multiple errors" when {
      "passed a request containing multiple errors" in {
        val result = validator("A12344A", "not-a-calculation-id", validTaxYear, fhlBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(CalculationIdFormatError, NinoFormatError))
          )
        )
      }

      propertyTypes.foreach { propertyType =>
        s"passed zero adjustments as false and other adjustments in $propertyType" in {
          val result = validator(
            validNino,
            validCalculationId,
            validTaxYear,
            mtdRequestWithZeroAndOtherAdjustments(propertyType, zeroAdjustments = false)
          ).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(
              correlationId,
              BadRequestError,
              Some(
                List(
                  RuleBothAdjustmentsSuppliedError.withPath(s"/$propertyType"),
                  RuleZeroAdjustmentsInvalidError.withPath(s"/$propertyType/zeroAdjustments")
                )
              )
            )
          )
        }
      }
    }
  }

}
