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

package v6.ukPropertyBsas.submit.def1

import common.errors.{RuleBothExpensesError, RuleBothPropertiesSuppliedError}
import org.scalatest.Assertion
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors._
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v6.ukPropertyBsas.submit.def1.model.request.{Def1_SubmitUkPropertyBsasRequestBody, Def1_SubmitUkPropertyBsasRequestData}

class Def1_SubmitUkPropertyBsasValidatorSpec extends UnitSpec with JsonErrorValidators {

  private implicit val correlationId: String = "1234"

  private val validNino          = "AA123456A"
  private val validCalculationId = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  private val validTaxYear       = "2023-24"

  private val parsedNino          = Nino(validNino)
  private val parsedCalculationId = CalculationId(validCalculationId)
  private val parsedTaxYear       = TaxYear.fromMtd(validTaxYear)

  private val ukPropertyBodyJson =
    Json
      .parse(
        s"""
       |{
       |   "ukProperty": {
       |      "income": {
       |         "totalRentsReceived": 1000.45,
       |         "premiumsOfLeaseGrant": 1000.45,
       |         "reversePremiums": 1000.45,
       |         "otherPropertyIncome": 1000.45
       |      },
       |      "expenses": {
       |         "premisesRunningCosts": 1000.45,
       |         "repairsAndMaintenance": 1000.45,
       |         "financialCosts": 1000.45,
       |         "professionalFees": 1000.45,
       |         "costOfServices": 1000.45,
       |         "residentialFinancialCost": 1000.45,
       |         "other": 1000.45,
       |         "travelCosts": 1000.45
       |      }
       |   }
       |}
       |""".stripMargin
      )
      .as[JsObject]

  private val parsedUkPropertyBody = ukPropertyBodyJson.as[Def1_SubmitUkPropertyBsasRequestBody]

  private val ukPropertyConsolidatedBodyJson =
    Json.parse(
      """
      |{
      |   "ukProperty": {
      |      "income": {
      |         "totalRentsReceived": 1000.45,
      |         "premiumsOfLeaseGrant": 1000.45,
      |         "reversePremiums": 1000.45,
      |         "otherPropertyIncome": 1000.45
      |      },
      |      "expenses": {
      |         "consolidatedExpenses": 1000.45
      |      }
      |   }
      |}""".stripMargin
    )

  private val parsedUkPropertyConsolidatedBody = ukPropertyConsolidatedBodyJson.as[Def1_SubmitUkPropertyBsasRequestBody]

  private val fhlBodyJson =
    Json
      .parse(
        s"""
       |{
       |   "furnishedHolidayLet": {
       |      "income": {
       |         "totalRentsReceived": 1000.45
       |      },
       |      "expenses": {
       |         "premisesRunningCosts": 1000.45,
       |         "repairsAndMaintenance": 1000.45,
       |         "financialCosts": 1000.45,
       |         "professionalFees": 1000.45,
       |         "costOfServices": 1000.45,
       |         "other": 1000.45,
       |         "travelCosts": 1000.45
       |      }
       |   }
       |}
       |""".stripMargin
      )
      .as[JsObject]

  private val parsedFhlBody = fhlBodyJson.as[Def1_SubmitUkPropertyBsasRequestBody]

  private val fhlConsolidatedBodyJson =
    Json.parse(
      s"""
       |{
       |   "furnishedHolidayLet": {
       |      "income": {
       |         "totalRentsReceived": 1000.45
       |      },
       |      "expenses": {
       |         "consolidatedExpenses": 1000.45
       |      }
       |   }
       |}
       |""".stripMargin
    )

  private val parsedFhlConsolidatedBody = fhlConsolidatedBodyJson.as[Def1_SubmitUkPropertyBsasRequestBody]

  private def validator(nino: String, calculationId: String, taxYear: String, body: JsValue) =
    new Def1_SubmitUkPropertyBsasValidator(nino, calculationId, taxYear, body)

  "running a validation" should {
    "return the parsed domain object" when {

      "given a valid fhl request" in {
        val result = validator(validNino, validCalculationId, validTaxYear, fhlBodyJson).validateAndWrapResult()

        result shouldBe Right(
          Def1_SubmitUkPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedFhlBody)
        )
      }

      "a valid TYS request is supplied" in {
        val result = validator(validNino, validCalculationId, validTaxYear, fhlBodyJson).validateAndWrapResult()

        result shouldBe Right(
          Def1_SubmitUkPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedFhlBody)
        )
      }

      "a valid ukProperty request is supplied" in {
        val result = validator(validNino, validCalculationId, validTaxYear, ukPropertyBodyJson).validateAndWrapResult()

        result shouldBe Right(
          Def1_SubmitUkPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedUkPropertyBody)
        )
      }

      "a valid fhl consolidated expenses request is supplied" in {
        val result = validator(validNino, validCalculationId, validTaxYear, fhlConsolidatedBodyJson).validateAndWrapResult()

        result shouldBe Right(
          Def1_SubmitUkPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedFhlConsolidatedBody)
        )
      }

      "a valid ukProperty consolidated expenses request is supplied" in {
        val result = validator(validNino, validCalculationId, validTaxYear, ukPropertyConsolidatedBodyJson).validateAndWrapResult()

        result shouldBe Right(
          Def1_SubmitUkPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedUkPropertyConsolidatedBody)
        )
      }

      "a minimal fhl request is supplied" in {
        val minimalJson =
          Json.parse(
            """
            |{
            |   "furnishedHolidayLet": {
            |      "income": {
            |         "totalRentsReceived": 1000.45
            |      }
            |   }
            |}
            |""".stripMargin
          )
        val parsedMinimal = minimalJson.as[Def1_SubmitUkPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, validTaxYear, minimalJson).validateAndWrapResult()
        result shouldBe Right(
          Def1_SubmitUkPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedMinimal)
        )
      }

      "a minimal ukProperty request is supplied" in {
        val minimalJson =
          Json.parse(
            """
              |{
              |   "ukProperty": {
              |      "income": {
              |         "totalRentsReceived": 1000.45
              |      }
              |   }
              |}
              |""".stripMargin
          )
        val parsedMinimal = minimalJson.as[Def1_SubmitUkPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, validTaxYear, minimalJson).validateAndWrapResult()
        result shouldBe Right(
          Def1_SubmitUkPropertyBsasRequestData(parsedNino, parsedCalculationId, parsedTaxYear, parsedMinimal)
        )
      }
    }

    "return NinoFormatError" when {
      "given an invalid nino" in {
        val result = validator("A12344A", validCalculationId, validTaxYear, fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return CalculationIdFormatError" when {
      "given an invalid calculationId" in {
        val result = validator(validNino, "12345", validTaxYear, fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, CalculationIdFormatError)
        )
      }
    }

    "return RuleBothPropertiesSuppliedError" when {
      "passed both fhl and ukProperty" in {
        val body   = fhlBodyJson ++ ukPropertyBodyJson
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleBothPropertiesSuppliedError)
        )
      }

      "passed both fhl and ukProperty even if they are empty" in {
        // Note: no other errors should be returned
        val body = Json.parse(
          s"""
               |{
               |  "furnishedHolidayLet": {},
               |  "ukProperty": []
               |}
               |""".stripMargin
        )
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleBothPropertiesSuppliedError)
        )
      }
    }

    "return RuleIncorrectOrEmptyBodyError" when {
      "given an empty body" in {
        val body   = Json.parse("{}")
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }

      "an object/array is empty or mandatory field is missing" when {

        List(
          "/furnishedHolidayLet",
          "/furnishedHolidayLet/income",
          "/furnishedHolidayLet/expenses"
        ).foreach(path => testWith(fhlBodyJson.replaceWithEmptyObject(path), path))

        List(
          "/ukProperty",
          "/ukProperty/income",
          "/ukProperty/expenses"
        ).foreach(path => testWith(ukPropertyBodyJson.replaceWithEmptyObject(path), path))

        def testWith(body: JsValue, expectedPath: String): Unit =
          s"for $expectedPath" in {
            val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()
            result shouldBe Left(
              ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath(expectedPath))
            )
          }
      }

      "an object is empty except for a additional (non-schema) property" in {
        val body = Json.parse("""{
                                |    "ukProperty": {
                                |       "unknownField": 999.99
                                |    }
                                |}""".stripMargin)

        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/ukProperty"))
        )
      }
    }

    "return ValueFormatError" when {
      "income or (non-consolidated) expenses is invalid" when {
        List(
          "/furnishedHolidayLet/income/totalRentsReceived",
          "/furnishedHolidayLet/expenses/premisesRunningCosts",
          "/furnishedHolidayLet/expenses/repairsAndMaintenance",
          "/furnishedHolidayLet/expenses/financialCosts",
          "/furnishedHolidayLet/expenses/professionalFees",
          "/furnishedHolidayLet/expenses/costOfServices",
          "/furnishedHolidayLet/expenses/other",
          "/furnishedHolidayLet/expenses/travelCosts"
        ).foreach(path => testWith(fhlBodyJson.update(path, _), path))

        List(
          "/ukProperty/income/totalRentsReceived",
          "/ukProperty/income/premiumsOfLeaseGrant",
          "/ukProperty/income/reversePremiums",
          "/ukProperty/income/otherPropertyIncome",
          "/ukProperty/expenses/premisesRunningCosts",
          "/ukProperty/expenses/repairsAndMaintenance",
          "/ukProperty/expenses/financialCosts",
          "/ukProperty/expenses/professionalFees",
          "/ukProperty/expenses/costOfServices",
          "/ukProperty/expenses/other",
          "/ukProperty/expenses/travelCosts"
        ).foreach(path => testWith(ukPropertyBodyJson.update(path, _), path))

        List(
          "/ukProperty/expenses/residentialFinancialCost"
        ).foreach(path => testWith(ukPropertyBodyJson.update(path, _), path, min = "-99999999999.99"))
      }

      "consolidated expenses is invalid" when {
        List(
          "/furnishedHolidayLet/expenses/consolidatedExpenses"
        ).foreach(path => testWith(fhlConsolidatedBodyJson.update(path, _), path))

        List(
          "/ukProperty/expenses/consolidatedExpenses"
        ).foreach(path => testWith(ukPropertyConsolidatedBodyJson.update(path, _), path))
      }

      "multiple fields are invalid" in {
        val path1 = "/ukProperty/income/totalRentsReceived"
        val path2 = "/ukProperty/expenses/premisesRunningCosts"

        val body = ukPropertyBodyJson
          .update(path1, JsNumber(0))
          .update(path2, JsNumber(123.123))

        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

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
          def doTest(value: JsNumber): Assertion = {
            val result = validator(validNino, validCalculationId, validTaxYear, body(value)).validateAndWrapResult()

            result shouldBe Left(
              ErrorWrapper(
                correlationId,
                ValueFormatError.forPathAndRangeExcludeZero(expectedPath, min, max)
              )
            )
          }

          "value is out of range" in doTest(JsNumber(BigDecimal(max) + 0.01))

          "value is zero" in doTest(JsNumber(0))
        }
    }

    "return RuleBothExpensesSuppliedError" when {
      "passed consolidated and separate expenses" in {
        val body   = fhlBodyJson.update("furnishedHolidayLet/expenses/consolidatedExpenses", JsNumber(123.45))
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleBothExpensesError.withPath("/furnishedHolidayLet/expenses"))
        )
      }

      "passed consolidated and separate ukProperty expenses" in {
        val body   = ukPropertyBodyJson.update("ukProperty/expenses/consolidatedExpenses", JsNumber(123.45))
        val result = validator(validNino, validCalculationId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleBothExpensesError.withPath("/ukProperty/expenses")
          )
        )
      }
    }

    "return TaxYearFormatError" when {
      "given a badly formatted tax year" in {
        val result = validator(validNino, validCalculationId, "202324", fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "given a tax year range of more than one year" in {
        val result = validator(validNino, validCalculationId, "2022-24", fhlBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return multiple errors" when {
      "the request has multiple errors" in {
        val result = validator("not-a-nino", "not-a-calculation-id", validTaxYear, fhlBodyJson).validateAndWrapResult()

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
