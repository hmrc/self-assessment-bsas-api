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

package v6.ukPropertyBsas.submit.def3

import common.errors.RuleBothExpensesError
import org.scalatest.Assertion
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsNumber, JsValue, Json}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors._
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v6.ukPropertyBsas.submit.def3.model.request.SubmitUKPropertyBsasRequestBodyFixtures.fullRequestJson
import v6.ukPropertyBsas.submit.def3.model.request.{Def3_SubmitUkPropertyBsasRequestBody, Def3_SubmitUkPropertyBsasRequestData}

class Def3_SubmitUkPropertyBsasValidatorSpec extends UnitSpec with JsonErrorValidators {

  private implicit val correlationId: String = "1234"

  private val validNino          = "AA123456A"
  private val validCalculationId = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  private val validTaxYear       = "2023-24"

  private val parsedNino          = Nino(validNino)
  private val parsedCalculationId = CalculationId(validCalculationId)
  private val parsedTaxYear       = TaxYear.fromMtd(validTaxYear)

  private val parsedFullBody = fullRequestJson.as[Def3_SubmitUkPropertyBsasRequestBody]

  private val consolidatedBodyJson =
    Json.parse(
      s"""
       |{
       |"ukProperty": {
       |  "income": {
       |     "totalRentsReceived": 1000.45,
       |     "premiumsOfLeaseGrant": 1000.45,
       |     "reversePremiums": 1000.45,
       |     "otherPropertyIncome": 1000.45
       |  },
       |  "expenses": {
       |     "consolidatedExpenses": 1000.45
       |    }
       |  }
       |}
       |""".stripMargin
    )

  private val invalidUkPropertyConsolidatedBodyJson =
    Json.parse(
      """
        |{
        |      "income": {
        |         "totalRentsReceived": 1000.45,
        |         "premiumsOfLeaseGrant": 1000.45,
        |         "reversePremiums": 1000.45,
        |         "otherPropertyIncome": 1000.45
        |      },
        |      "expenses": {
        |         "consolidatedExpenses": 1000.45
        |   }
        |}""".stripMargin
    )

  private val parsedConsolidatedBody = consolidatedBodyJson.as[Def3_SubmitUkPropertyBsasRequestBody]

  private def validator(nino: String, calculationId: String, taxYear: Option[String], body: JsValue) =
    new Def3_SubmitUkPropertyBsasValidator(nino, calculationId, taxYear, body)

  "running a validation" should {
    "return the parsed domain object" when {

      "given a valid request" in {
        val result = validator(validNino, validCalculationId, None, fullRequestJson).validateAndWrapResult()

        result shouldBe Right(
          Def3_SubmitUkPropertyBsasRequestData(parsedNino, parsedCalculationId, None, parsedFullBody)
        )
      }

      "a valid TYS request is supplied" in {
        val result = validator(validNino, validCalculationId, Some(validTaxYear), fullRequestJson).validateAndWrapResult()

        result shouldBe Right(
          Def3_SubmitUkPropertyBsasRequestData(parsedNino, parsedCalculationId, Some(parsedTaxYear), parsedFullBody)
        )
      }

      "a valid consolidated expenses request is supplied" in {
        val result = validator(validNino, validCalculationId, None, consolidatedBodyJson).validateAndWrapResult()

        result shouldBe Right(
          Def3_SubmitUkPropertyBsasRequestData(parsedNino, parsedCalculationId, None, parsedConsolidatedBody)
        )
      }

      "a minimal request is supplied" in {
        val minimalJson =
          Json.parse(
            """
            |{
            |"ukProperty": {
            |  "income": {
            |     "totalRentsReceived": 1000.45
            |   }
            |  }
            |}
            |""".stripMargin
          )
        val parsedMinimal = minimalJson.as[Def3_SubmitUkPropertyBsasRequestBody]

        val result = validator(validNino, validCalculationId, None, minimalJson).validateAndWrapResult()
        result shouldBe Right(
          Def3_SubmitUkPropertyBsasRequestData(parsedNino, parsedCalculationId, None, parsedMinimal)
        )
      }
    }

    "return NinoFormatError" when {
      "given an invalid nino" in {
        val result = validator("A12344A", validCalculationId, None, fullRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return CalculationIdFormatError" when {
      "given an invalid calculationId" in {
        val result = validator(validNino, "12345", None, fullRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, CalculationIdFormatError)
        )
      }
    }

    "return RuleIncorrectOrEmptyBodyError" when {
      "given an empty body" in {
        val body   = Json.parse("{}")
        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }

      "an object/array is empty or mandatory field is missing" when {

        List(
          "/ukProperty/income",
          "/ukProperty/expenses"
        ).foreach(path => testWith(fullRequestJson.replaceWithEmptyObject(path), path))

        def testWith(body: JsValue, expectedPath: String): Unit =
          s"for $expectedPath" in {
            val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()
            result shouldBe Left(
              ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath(expectedPath))
            )
          }
      }

      "an object is empty except for a additional (non-schema) property" in {
        val body = Json.parse("""{
                                |"ukProperty": {
                                |  "expenses": {
                                |   "unknownField": 999.99
                                |   }
                                |  }
                                |}""".stripMargin)

        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/ukProperty/expenses"))
        )
      }

      "an invalid non-fhl consolidated expenses request is supplied for a previous schema" in {
        val result = validator(validNino, validCalculationId, None, invalidUkPropertyConsolidatedBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }
    }

    "return ValueFormatError" when {
      "income or (non-consolidated) expenses is invalid" when {
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
          "/ukProperty/expenses/travelCosts"
        ).foreach(path => testWith(fullRequestJson.update(path, _), path))
      }

      "consolidated expenses is invalid" when {
        List(
          "/ukProperty/expenses/consolidatedExpenses"
        ).foreach(path => testWith(consolidatedBodyJson.update(path, _), path))
      }

      "non negative fields have negative values" when {
        List(
          "/ukProperty/expenses/residentialFinancialCost",
          "/ukProperty/expenses/other"
        ).foreach(path => testWith(fullRequestJson.update(path, _), path, min = "0"))
      }

      "multiple fields are invalid" in {
        val path1 = "/ukProperty/income/totalRentsReceived"
        val path2 = "/ukProperty/expenses/premisesRunningCosts"

        val body = fullRequestJson
          .update(path1, JsNumber(0))
          .update(path2, JsNumber(123.123))

        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            ValueFormatError.copy(paths = Some(List(path2, path1)), message = "The value must be between -99999999999.99 and 99999999999.99")
          )
        )
      }

      def testWith(body: JsNumber => JsValue, expectedPath: String, min: String = "-99999999999.99", max: String = "99999999999.99"): Unit =
        s"for $expectedPath" when {
          def doTest(value: JsNumber): Assertion = {
            val result = validator(validNino, validCalculationId, None, body(value)).validateAndWrapResult()

            result shouldBe Left(
              ErrorWrapper(
                correlationId,
                ValueFormatError.forPathAndRange(expectedPath, min, max)
              )
            )
          }

          "value is out of range" in doTest(JsNumber(BigDecimal(max) + 0.01))

          "value is zero" in doTest(JsNumber(0))
        }
    }

    "return RuleBothExpensesSuppliedError" when {
      "passed consolidated and separate expenses" in {
        val body   = fullRequestJson.update("/ukProperty/expenses/consolidatedExpenses", JsNumber(123.45))
        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleBothExpensesError.withPath("/ukProperty/expenses"))
        )
      }
    }

    "return TaxYearFormatError" when {
      "given a badly formatted tax year" in {
        val result = validator(validNino, validCalculationId, Some("202324"), fullRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "given a tax year range of more than one year" in {
        val result = validator(validNino, validCalculationId, Some("2022-24"), fullRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return InvalidTaxYearParameterError" when {
      "given a tax year before TYS" in {
        val result = validator(validNino, validCalculationId, Some("2022-23"), fullRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, InvalidTaxYearParameterError)
        )
      }
    }

    "return multiple errors" when {
      "the request has multiple errors" in {
        val result = validator("not-a-nino", "not-a-calculation-id", None, fullRequestJson).validateAndWrapResult()

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
