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

package v3.controllers.requestParsers.validators

import org.scalatest.Assertion
import play.api.libs.json._
import support.UnitSpec
import v3.models.errors._
import v3.models.request.submitBsas.ukProperty.SubmitUkPropertyBsasRawData
import v3.models.utils.JsonErrorValidators

class SubmitUkPropertyBsasValidatorSpec extends UnitSpec with JsonErrorValidators {

  private val validNino          = "AA123456A"
  private val validCalculationId = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  private val validTaxYear       = Some("2023-24")

  private val nonFhlBodyJson = Json.parse(
    s"""{
       |   "nonFurnishedHolidayLet": {
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

  private val nonFhlBodyConsolidatedJson =
    Json.parse("""{
                 |   "nonFurnishedHolidayLet": {
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
                 |}""".stripMargin)

  private val fhlBodyJson = Json.parse(
    s"""{
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

  private val fhlBodyConsolidatedJson = Json.parse(
    s"""{
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

  val validator = new SubmitUkPropertyBsasValidator

  "running a validation" should {
    "return no errors" when {
      "a valid fhl request is supplied" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = fhlBodyJson,
            taxYear = None
          )) shouldBe Nil
      }

      "a valid fhl TYS request is supplied" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = fhlBodyJson,
            taxYear = validTaxYear
          )) shouldBe Nil
      }

      "a valid non-fhl request is supplied" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = nonFhlBodyJson,
            taxYear = None
          )) shouldBe Nil
      }

      "a valid non-fhl TYS request is supplied" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = nonFhlBodyJson,
            taxYear = validTaxYear
          )) shouldBe Nil
      }

      "a valid fhl consolidated expenses request is supplied" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = fhlBodyConsolidatedJson,
            taxYear = None
          )) shouldBe Nil
      }

      "a valid non-fhl consolidated expenses request is supplied" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = nonFhlBodyConsolidatedJson,
            taxYear = None
          )) shouldBe Nil
      }

      "a minimal fhl request is supplied" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = Json.parse("""{
                                |   "furnishedHolidayLet": {
                                |      "income": {
                                |         "totalRentsReceived": 1000.45
                                |      }
                                |   }
                                |}
                                |""".stripMargin),
            taxYear = None
          )) shouldBe Nil
      }

      "a minimal non-fhl request is supplied" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = Json.parse("""{
                                |   "nonFurnishedHolidayLet": {
                                |      "income": {
                                |         "totalRentsReceived": 1000.45
                                |      }
                                |   }
                                |}
                                |""".stripMargin),
            taxYear = None
          )) shouldBe Nil
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = "A12344A",
            calculationId = validCalculationId,
            body = fhlBodyJson,
            taxYear = None
          )) shouldBe List(NinoFormatError)
      }
    }

    "return CalculationIdFormatError error" when {
      "an invalid calculationId is supplied" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = "12345",
            body = fhlBodyJson,
            taxYear = None
          )) shouldBe List(CalculationIdFormatError)
      }
    }

    "return RuleBothPropertiesSuppliedError" when {
      "both fhl and non-fhl are supplied" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = fhlBodyJson.as[JsObject] ++ nonFhlBodyJson.as[JsObject],
            taxYear = None
          )) shouldBe List(RuleBothPropertiesSuppliedError)
      }

      "both fhl and non-fhl are supplied even if they are empty" in {
        // Note: no other errors should be returned
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = Json.parse(
              s"""
                 |{
                 |  "furnishedHolidayLet": {},
                 |  "nonFurnishedHolidayLet": {}
                 |}
                 |""".stripMargin
            ),
            taxYear = None
          )) shouldBe List(RuleBothPropertiesSuppliedError)
      }
    }

    "return RuleIncorrectOrEmptyBodyError" when {
      "an empty body is submitted" in {
        validator.validate(SubmitUkPropertyBsasRawData(nino = validNino,
                                                       calculationId = validCalculationId,
                                                       body = Json.parse("""{}"""),
                                                       taxYear = None)) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "an object/array is empty or mandatory field is missing" when {

        Seq(
          "/furnishedHolidayLet",
          "/furnishedHolidayLet/income",
          "/furnishedHolidayLet/expenses",
        ).foreach(path => testWith(fhlBodyJson.replaceWithEmptyObject(path), path))

        Seq(
          "/nonFurnishedHolidayLet",
          "/nonFurnishedHolidayLet/income",
          "/nonFurnishedHolidayLet/expenses",
        ).foreach(path => testWith(nonFhlBodyJson.replaceWithEmptyObject(path), path))

        def testWith(body: JsValue, expectedPath: String): Unit =
          s"for $expectedPath" in {
            validator.validate(
              SubmitUkPropertyBsasRawData(
                nino = validNino,
                calculationId = validCalculationId,
                body = body,
                taxYear = None
              )) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq(expectedPath))))
          }
      }

      "an object is empty except for a additional (non-schema) property" in {
        val json = Json.parse("""{
            |    "nonFurnishedHolidayLet": {
            |       "unknownField": 999.99
            |    }
            |}""".stripMargin)

        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = json,
            taxYear = None
          )) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/nonFurnishedHolidayLet"))))
      }
    }

    "return ValueFormatError" when {
      "income or (non-consolidated) expenses is invalid" when {
        Seq(
          "/furnishedHolidayLet/income/totalRentsReceived",
          "/furnishedHolidayLet/expenses/premisesRunningCosts",
          "/furnishedHolidayLet/expenses/repairsAndMaintenance",
          "/furnishedHolidayLet/expenses/financialCosts",
          "/furnishedHolidayLet/expenses/professionalFees",
          "/furnishedHolidayLet/expenses/costOfServices",
          "/furnishedHolidayLet/expenses/other",
          "/furnishedHolidayLet/expenses/travelCosts",
        ).foreach(path => testWith(fhlBodyJson.update(path, _), path))

        Seq(
          "/nonFurnishedHolidayLet/income/totalRentsReceived",
          "/nonFurnishedHolidayLet/income/premiumsOfLeaseGrant",
          "/nonFurnishedHolidayLet/income/reversePremiums",
          "/nonFurnishedHolidayLet/income/otherPropertyIncome",
          "/nonFurnishedHolidayLet/expenses/premisesRunningCosts",
          "/nonFurnishedHolidayLet/expenses/repairsAndMaintenance",
          "/nonFurnishedHolidayLet/expenses/financialCosts",
          "/nonFurnishedHolidayLet/expenses/professionalFees",
          "/nonFurnishedHolidayLet/expenses/costOfServices",
          "/nonFurnishedHolidayLet/expenses/residentialFinancialCost",
          "/nonFurnishedHolidayLet/expenses/other",
          "/nonFurnishedHolidayLet/expenses/travelCosts",
        ).foreach(path => testWith(nonFhlBodyJson.update(path, _), path))
      }

      "consolidated expenses is invalid" when {
        Seq(
          "/furnishedHolidayLet/expenses/consolidatedExpenses",
        ).foreach(path => testWith(fhlBodyConsolidatedJson.update(path, _), path))

        Seq(
          "/nonFurnishedHolidayLet/expenses/consolidatedExpenses",
        ).foreach(path => testWith(nonFhlBodyConsolidatedJson.update(path, _), path))
      }

      "multiple fields are invalid" in {
        val path1 = "/nonFurnishedHolidayLet/income/totalRentsReceived"
        val path2 = "/nonFurnishedHolidayLet/expenses/premisesRunningCosts"

        val json = nonFhlBodyJson
          .update(path1, JsNumber(0))
          .update(path2, JsNumber(123.123))

        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = json,
            taxYear = None
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(Seq(path1, path2)), message = "The value must be between -99999999999.99 and 99999999999.99"))
      }

      def testWith(body: JsNumber => JsValue, expectedPath: String): Unit = s"for $expectedPath" when {
        def doTest(value: JsNumber): Assertion =
          validator.validate(
            SubmitUkPropertyBsasRawData(
              nino = validNino,
              calculationId = validCalculationId,
              body = body(value),
              taxYear = None
            )) shouldBe List(ValueFormatError.forPathAndRange(expectedPath, "-99999999999.99", "99999999999.99"))

        "value is out of range" in doTest(JsNumber(99999999999.99 + 0.01))

        "value is zero" in doTest(JsNumber(0))
      }
    }

    "return RuleBothExpensesSuppliedError" when {
      "consolidated and separate expenses provided for fhl" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = fhlBodyJson.update("furnishedHolidayLet/expenses/consolidatedExpenses", JsNumber(123.45)),
            taxYear = None
          )) shouldBe
          List(RuleBothExpensesError.copy(paths = Some(Seq("/furnishedHolidayLet/expenses"))))
      }

      "consolidated and separate expenses provided for non-fhl" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = nonFhlBodyJson.update("nonFurnishedHolidayLet/expenses/consolidatedExpenses", JsNumber(123.45)),
            taxYear = None
          )) shouldBe
          List(RuleBothExpensesError.copy(paths = Some(Seq("/nonFurnishedHolidayLet/expenses"))))
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year format is supplied" in {
        validator
          .validate(
            SubmitUkPropertyBsasRawData(
              nino = validNino,
              calculationId = validCalculationId,
              body = fhlBodyJson,
              taxYear = Some("202324")
            )) shouldBe List(TaxYearFormatError)
      }
    }

    "return InvalidTaxYearParameter" when {
      "an invalid tax year is supplied" in {
        validator
          .validate(
            SubmitUkPropertyBsasRawData(
              nino = validNino,
              calculationId = validCalculationId,
              body = fhlBodyJson,
              taxYear = Some("2022-23")
            )) shouldBe List(InvalidTaxYearParameterError)

      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors" in {
        validator.validate(
          SubmitUkPropertyBsasRawData(
            nino = "A12344A",
            calculationId = "badCalcId",
            body = fhlBodyJson,
            taxYear = None
          )) shouldBe
          List(NinoFormatError, CalculationIdFormatError)
      }
    }
  }
}
