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

import play.api.libs.json._
import support.UnitSpec
import v3.models.utils.JsonErrorValidators
import v3.models.errors._
import v3.models.request.submitBsas.foreignProperty.SubmitForeignPropertyRawData

class SubmitForeignPropertyBsasValidatorSpec extends UnitSpec with JsonErrorValidators {

  private val validNino          = "AA123456A"
  private val validCalculationId = "a54ba782-5ef4-47f4-ab72-495406665ca9"

  private def entryWith(countryCode: String) =
    Json.parse(s"""{
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
    Json.parse("""{
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

  private def nonFhlBodyWith(nonFhlEntries: JsValue*) = Json.parse(
    s"""{
       |  "nonFurnishedHolidayLet": ${JsArray(nonFhlEntries)}
       |}
       |""".stripMargin
  )

  private val nonFhlBody = nonFhlBodyWith(entry)

  private val fhlBody = Json.parse(
    s"""{
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

  val validator = new SubmitForeignPropertyBsasValidator

  "running a validation" should {
    "return no errors" when {
      "a valid fhl request is supplied" in {
        validator.validate(SubmitForeignPropertyRawData(nino = validNino, calculationId = validCalculationId, body = fhlBody)) shouldBe Nil
      }

      "a valid non-fhl request is supplied" in {
        validator.validate(SubmitForeignPropertyRawData(nino = validNino, calculationId = validCalculationId, body = nonFhlBody)) shouldBe Nil
      }

      "a valid fhl consolidated expenses request is supplied" in {
        validator.validate(SubmitForeignPropertyRawData(nino = validNino, calculationId = validCalculationId, body = fhlBodyConsolidated)) shouldBe Nil
      }

      "a valid non-fhl consolidated expenses request is supplied" in {
        validator.validate(
          SubmitForeignPropertyRawData(nino = validNino, calculationId = validCalculationId, body = nonFhlBodyWith(entryConsolidated))) shouldBe Nil
      }

      "a minimal fhl request is supplied" in {
        validator.validate(
          SubmitForeignPropertyRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = Json.parse("""{
                                |  "foreignFhlEea": {
                                |    "income": {
                                |      "totalRentsReceived": 1000.45
                                |    }
                                |  }
                                |}
                                |""".stripMargin)
          )) shouldBe Nil
      }

      "a minimal non-fhl request is supplied" in {
        validator.validate(
          SubmitForeignPropertyRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = Json.parse("""{
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
          )) shouldBe Nil
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(SubmitForeignPropertyRawData(nino = "A12344A", calculationId = validCalculationId, body = fhlBody)) shouldBe
          List(NinoFormatError)
      }
    }

    "return CalculationIdFormatError error" when {
      "an invalid calculationId is supplied" in {
        validator.validate(SubmitForeignPropertyRawData(nino = validNino, calculationId = "12345", body = fhlBody)) shouldBe
          List(CalculationIdFormatError)
      }
    }

    "return RuleBothPropertiesSuppliedError" when {
      "both fhl and non-fhl are supplied" in {
        validator.validate(
          SubmitForeignPropertyRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = fhlBody.as[JsObject] ++ nonFhlBodyWith(entry).as[JsObject]
          )) shouldBe List(RuleBothPropertiesSuppliedError)
      }

      "both fhl and non-fhl are supplied even if they are empty" in {
        // Note: no other errors should be returned
        validator.validate(
          SubmitForeignPropertyRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = Json.parse(
              s"""
             |{
             |  "foreignFhlEea": {},
             |  "nonFurnishedHolidayLet": []
             |}
             |""".stripMargin
            )
          )) shouldBe List(RuleBothPropertiesSuppliedError)
      }
    }

    "return RuleIncorrectOrEmptyBodyError" when {
      "an empty body is submitted" in {
        validator.validate(SubmitForeignPropertyRawData(nino = validNino, calculationId = validCalculationId, body = Json.parse("""{}"""))) shouldBe List(
          RuleIncorrectOrEmptyBodyError)
      }

      "an object/array is empty or mandatory field is missing" when {

        Seq(
          "/foreignFhlEea",
          "/foreignFhlEea/income",
          "/foreignFhlEea/expenses",
        ).foreach(path => testWith(fhlBody.replaceWithEmptyObject(path), path))

        Seq(
          (nonFhlBodyWith(), "/nonFurnishedHolidayLet"),
          (nonFhlBodyWith(entry.replaceWithEmptyObject("/income")), "/nonFurnishedHolidayLet/0/income"),
          (nonFhlBodyWith(entry.replaceWithEmptyObject("/expenses")), "/nonFurnishedHolidayLet/0/expenses"),
          (nonFhlBodyWith(entry.removeProperty("/countryCode")), "/nonFurnishedHolidayLet/0/countryCode"),
          (nonFhlBodyWith(entry.removeProperty("/income").removeProperty("/expenses")), "/nonFurnishedHolidayLet/0"),
        ).foreach((testWith _).tupled)

        def testWith(body: JsValue, expectedPath: String): Unit =
          s"for $expectedPath" in {
            validator.validate(
              SubmitForeignPropertyRawData(
                nino = validNino,
                calculationId = validCalculationId,
                body
              )) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq(expectedPath))))
          }
      }

      "an object is empty except for a additional (non-schema) property" in {
        val json = Json.parse("""{
                                |    "foreignFhlEea":{
                                |       "unknownField": 999.99
                                |    }
                                |}""".stripMargin)

        validator.validate(
          SubmitForeignPropertyRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = json
          )) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/foreignFhlEea"))))
      }

      "return ValueFormatError" when {
        "income or (non-consolidated) expenses is invalid" when {
          Seq(
            "/foreignFhlEea/income/totalRentsReceived",
            "/foreignFhlEea/expenses/premisesRunningCosts",
            "/foreignFhlEea/expenses/repairsAndMaintenance",
            "/foreignFhlEea/expenses/financialCosts",
            "/foreignFhlEea/expenses/professionalFees",
            "/foreignFhlEea/expenses/costOfServices",
            "/foreignFhlEea/expenses/other",
            "/foreignFhlEea/expenses/travelCosts",
          ).foreach(path => testWith(fhlBody.update(path, _), path))

          Seq(
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/income/totalRentsReceived", v)), "/nonFurnishedHolidayLet/0/income/totalRentsReceived"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/income/premiumsOfLeaseGrant", v)),
             "/nonFurnishedHolidayLet/0/income/premiumsOfLeaseGrant"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/income/otherPropertyIncome", v)), "/nonFurnishedHolidayLet/0/income/otherPropertyIncome"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/premisesRunningCosts", v)),
             "/nonFurnishedHolidayLet/0/expenses/premisesRunningCosts"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/repairsAndMaintenance", v)),
             "/nonFurnishedHolidayLet/0/expenses/repairsAndMaintenance"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/financialCosts", v)), "/nonFurnishedHolidayLet/0/expenses/financialCosts"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/professionalFees", v)), "/nonFurnishedHolidayLet/0/expenses/professionalFees"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/costOfServices", v)), "/nonFurnishedHolidayLet/0/expenses/costOfServices"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/residentialFinancialCost", v)),
             "/nonFurnishedHolidayLet/0/expenses/residentialFinancialCost"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/other", v)), "/nonFurnishedHolidayLet/0/expenses/other"),
            ((v: JsNumber) => nonFhlBodyWith(entry.update("/expenses/travelCosts", v)), "/nonFurnishedHolidayLet/0/expenses/travelCosts"),
          ).foreach((testWith _).tupled)
        }

        "consolidated expenses is invalid" when {
          Seq(
            "/foreignFhlEea/expenses/consolidatedExpenses",
          ).foreach(path => testWith(fhlBodyConsolidated.update(path, _), path))

          Seq(
            ((v: JsNumber) => nonFhlBodyWith(entryConsolidated.update("/expenses/consolidatedExpenses", v)),
             "/nonFurnishedHolidayLet/0/expenses/consolidatedExpenses")
          ).foreach(p => (testWith _).tupled(p))
        }

        "multiple fields are invalid" in {
          val path1 = "/nonFurnishedHolidayLet/0/expenses/travelCosts"
          val path2 = "/nonFurnishedHolidayLet/1/income/totalRentsReceived"

          val json =
            nonFhlBodyWith(
              entryWith(countryCode = "ZWE").update("/expenses/travelCosts", JsNumber(0)),
              entryWith(countryCode = "AFG").update("/income/totalRentsReceived", JsNumber(123.123))
            )

          validator.validate(
            SubmitForeignPropertyRawData(
              nino = validNino,
              calculationId = validCalculationId,
              body = json
            )) shouldBe List(
            ValueFormatError.copy(paths = Some(Seq(path1, path2)), message = "The value must be between -99999999999.99 and 99999999999.99"))
        }

        def testWith(body: JsNumber => JsValue, expectedPath: String): Unit = s"for $expectedPath" when {
          def doTest(value: JsNumber) =
            validator.validate(
              SubmitForeignPropertyRawData(
                nino = validNino,
                calculationId = validCalculationId,
                body = body(value)
              )) shouldBe List(ValueFormatError.forPathAndRange(expectedPath, "-99999999999.99", "99999999999.99"))

          "value is out of range" in doTest(JsNumber(99999999999.99 + 0.01))

          "value is zero" in doTest(JsNumber(0))
        }
      }

      "return RuleCountryCodeError" when {
        "an invalid country code is provided" in {
          validator.validate(
            SubmitForeignPropertyRawData(
              nino = validNino,
              calculationId = validCalculationId,
              body = nonFhlBodyWith(entryWith(countryCode = "QQQ"))
            )) shouldBe List(RuleCountryCodeError.copy(paths = Some(Seq("/nonFurnishedHolidayLet/0/countryCode"))))
        }

        "multiple invalid country codes are provided" in {
          validator.validate(
            SubmitForeignPropertyRawData(
              nino = validNino,
              calculationId = validCalculationId,
              body = nonFhlBodyWith(entryWith(countryCode = "QQQ"), entryWith(countryCode = "AAA"))
            )) shouldBe List(
            RuleCountryCodeError.copy(paths = Some(Seq("/nonFurnishedHolidayLet/0/countryCode", "/nonFurnishedHolidayLet/1/countryCode"))))
        }
      }

      "return CountryCodeFormatError" when {
        "an invalid country code is provided" in {
          validator.validate(
            SubmitForeignPropertyRawData(
              nino = validNino,
              calculationId = validCalculationId,
              body = nonFhlBodyWith(entryWith(countryCode = "XXXX"))
            )) shouldBe List(CountryCodeFormatError.copy(paths = Some(Seq("/nonFurnishedHolidayLet/0/countryCode"))))
        }
      }

      "return RuleDuplicateCountryCodeError" when {
        "a country code is duplicated" in {
          val code = "ZWE"
          validator.validate(
            SubmitForeignPropertyRawData(
              nino = validNino,
              calculationId = validCalculationId,
              body = nonFhlBodyWith(entryWith(code), entryWith(code))
            )) shouldBe List(
            RuleDuplicateCountryCodeError
              .forDuplicatedCodesAndPaths(code = code, paths = Seq("/nonFurnishedHolidayLet/0/countryCode", "/nonFurnishedHolidayLet/1/countryCode")))
        }

        "multiple country codes are duplicated" in {
          val code1 = "AFG"
          val code2 = "ZWE"
          validator.validate(
            SubmitForeignPropertyRawData(
              nino = validNino,
              calculationId = validCalculationId,
              body = nonFhlBodyWith(entryWith(code1), entryWith(code2), entryWith(code1), entryWith(code2))
            )) should contain theSameElementsAs List(
            RuleDuplicateCountryCodeError
              .forDuplicatedCodesAndPaths(code = code1,
                                          paths = Seq("/nonFurnishedHolidayLet/0/countryCode", "/nonFurnishedHolidayLet/2/countryCode")),
            RuleDuplicateCountryCodeError
              .forDuplicatedCodesAndPaths(code = code2,
                                          paths = Seq("/nonFurnishedHolidayLet/1/countryCode", "/nonFurnishedHolidayLet/3/countryCode")),
          )
        }
      }

      "return RuleBothExpensesSuppliedError" when {
        "consolidated and separate expenses provided for fhl" in {
          validator.validate(
            SubmitForeignPropertyRawData(
              nino = validNino,
              calculationId = validCalculationId,
              body = fhlBody.update("foreignFhlEea/expenses/consolidatedExpenses", JsNumber(123.45))
            )) shouldBe
            List(RuleBothExpensesError.copy(paths = Some(Seq("/foreignFhlEea/expenses"))))
        }

        "consolidated and separate expenses provided for non-fhl" in {
          validator.validate(SubmitForeignPropertyRawData(
            nino = validNino,
            calculationId = validCalculationId,
            body = nonFhlBodyWith(
              entryWith(countryCode = "ZWE").update("expenses/consolidatedExpenses", JsNumber(123.45)),
              entryWith(countryCode = "AFG").update("expenses/consolidatedExpenses", JsNumber(123.45))
            )
          )) shouldBe
            List(RuleBothExpensesError.copy(paths = Some(Seq("/nonFurnishedHolidayLet/0/expenses", "/nonFurnishedHolidayLet/1/expenses"))))
        }
      }

      "return multiple errors" when {
        "request supplied has multiple errors" in {
          validator.validate(SubmitForeignPropertyRawData(nino = "A12344A", calculationId = "badCalcId", body = fhlBody)) shouldBe
            List(NinoFormatError, CalculationIdFormatError)
        }
      }
    }
  }
}
