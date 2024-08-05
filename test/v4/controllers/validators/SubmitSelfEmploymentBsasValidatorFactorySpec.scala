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

package v4.controllers.validators

import common.errors._
import org.scalatest.Assertion
import play.api.libs.json._
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors._
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v4.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._
import v4.models.request.submitBsas.selfEmployment.SubmitSelfEmploymentBsasRequestData

class SubmitSelfEmploymentBsasValidatorFactorySpec extends UnitSpec with JsonErrorValidators {

  private implicit val correlationId: String = "1234"

  private val validNino          = "AA123456A"
  private val validCalculationId = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  private val validTaxYear       = "2023-24"

  private val parsedNino          = Nino(validNino)
  private val parsedCalculationId = CalculationId(validCalculationId)
  private val parsedTaxYear       = TaxYear.fromMtd(validTaxYear)

  val validatorFactory = new SubmitSelfEmploymentBsasValidatorFactory

  private def validator(nino: String, calculationId: String, taxYear: Option[String], body: JsValue) =
    validatorFactory.validator(nino, calculationId, taxYear, body)

  "running a validation" should {
    "return the parsed domain object" when {

      "passed a valid request" in {
        val result = validator(validNino, validCalculationId, None, mtdRequestJson).validateAndWrapResult()

        result shouldBe Right(
          SubmitSelfEmploymentBsasRequestData(parsedNino, parsedCalculationId, None, parsedMtdRequestBody)
        )
      }

      "passed a valid request with only consolidated expenses" in {
        val result =
          validator(validNino, validCalculationId, None, mtdRequestWithOnlyConsolidatedExpenses).validateAndWrapResult()

        result shouldBe Right(
          SubmitSelfEmploymentBsasRequestData(parsedNino, parsedCalculationId, None, parsedMtdRequestWithOnlyConsolidatedExpensesBody)
        )
      }

      "passed valid parameters with only additions expenses" in {
        val result =
          validator(validNino, validCalculationId, None, mtdRequestWithOnlyAdditionsExpenses)
            .validateAndWrapResult()

        result shouldBe Right(
          SubmitSelfEmploymentBsasRequestData(parsedNino, parsedCalculationId, None, parsedMtdRequestWithOnlyAdditionsExpenses)
        )
      }

      "passed a valid TYS tax year" in {
        val result =
          validator(validNino, validCalculationId, Some(validTaxYear), mtdRequestWithOnlyAdditionsExpenses)
            .validateAndWrapResult()

        result shouldBe Right(
          SubmitSelfEmploymentBsasRequestData(parsedNino, parsedCalculationId, Some(parsedTaxYear), parsedMtdRequestWithOnlyAdditionsExpenses)
        )
      }
    }

    "return NinoFormatError" when {
      "passed an invalid nino" in {
        val result = validator("A12344A", validCalculationId, None, mtdRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return CalculationIdFormatError error" when {
      "passed an invalid calculationId" in {
        val result = validator(validNino, "12345", None, mtdRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, CalculationIdFormatError)
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

      "an object/array is empty or mandatory field is missing" when {
        List(
          "/income",
          "/income/turnover",
          "/income/other"
        ).foreach(path => testWith(mtdRequestJson.replaceWithEmptyObject(path), path))

        List(
          "/expenses",
          "/expenses/costOfGoodsAllowable",
          "/expenses/paymentsToSubcontractorsAllowable",
          "/expenses/wagesAndStaffCostsAllowable"
        ).foreach(path => testWith(mtdRequestJson.replaceWithEmptyObject(path), path))

        List(
          "/additions",
          "/additions/costOfGoodsDisallowable",
          "/additions/paymentsToSubcontractorsDisallowable"
        ).foreach(path => testWith(mtdRequestJson.replaceWithEmptyObject(path), path))

        def testWith(body: JsValue, expectedPath: String): Unit =
          s"for $expectedPath" in {
            val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()
            result shouldBe Left(
              ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath(expectedPath))
            )
          }
      }

      "income object is empty" in {
        val body   = Json.parse(""" { "income": {} }""")
        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/income"))
        )
      }

      "expenses object is empty" in {
        val body   = Json.parse(""" { "expenses": {} }""")
        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/expenses"))
        )
      }

      "additions object is empty" in {
        val body   = Json.parse(""" { "additions": {} }""")
        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/additions"))
        )
      }

      "a field is empty" in {
        val body   = Json.parse(""" { "wagesAndStaffCostsDisallowable": {} }""")
        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }

      "an object is invalid" in {
        val body   = Json.parse(""" { "income": "not-an-object" }""")
        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/income"))
        )
      }
    }

    "return ValueFormatError" when {
      "single fields are invalid" when {
        List(
          "/income/turnover",
          "/income/other",
          "/expenses/costOfGoodsAllowable",
          "/expenses/paymentsToSubcontractorsAllowable",
          "/expenses/wagesAndStaffCostsAllowable",
          "/expenses/carVanTravelExpensesAllowable",
          "/expenses/premisesRunningCostsAllowable",
          "/expenses/maintenanceCostsAllowable",
          "/expenses/adminCostsAllowable",
          "/expenses/interestOnBankOtherLoansAllowable",
          "/expenses/financeChargesAllowable",
          "/expenses/irrecoverableDebtsAllowable",
          "/expenses/professionalFeesAllowable",
          "/expenses/depreciationAllowable",
          "/expenses/otherExpensesAllowable",
          "/expenses/advertisingCostsAllowable",
          "/expenses/businessEntertainmentCostsAllowable",
          "/additions/costOfGoodsDisallowable",
          "/additions/paymentsToSubcontractorsDisallowable",
          "/additions/wagesAndStaffCostsDisallowable",
          "/additions/carVanTravelExpensesDisallowable",
          "/additions/premisesRunningCostsDisallowable",
          "/additions/maintenanceCostsDisallowable",
          "/additions/adminCostsDisallowable",
          "/additions/interestOnBankOtherLoansDisallowable",
          "/additions/financeChargesDisallowable",
          "/additions/irrecoverableDebtsDisallowable",
          "/additions/professionalFeesDisallowable",
          "/additions/depreciationDisallowable",
          "/additions/otherExpensesDisallowable",
          "/additions/advertisingCostsDisallowable",
          "/additions/businessEntertainmentCostsDisallowable"
        ).foreach(path => testWith(mtdRequestJson.update(path, _), path))
      }

      "consolidated expenses is invalid" when {
        List(
          "/expenses/consolidatedExpenses"
        ).foreach(path => testWith(mtdRequestWithOnlyConsolidatedExpenses.update(path, _), path))
      }

      "multiple fields are invalid" in {
        val path1 = "/income/turnover"
        val path2 = "/expenses/consolidatedExpenses"
        val path3 = "/additions/costOfGoodsDisallowable"

        val body = Json.parse(
          s"""{
             |	"income": {
             |		"turnover": 0
             |	},
             |	"expenses": {
             |		"consolidatedExpenses": 123.123
             |	},
             |  "additions": {
             |    "costOfGoodsDisallowable": 999999999999.99
             | }
             |}
             |""".stripMargin
        )

        val result = validator(validNino, validCalculationId, None, body).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(
              ValueFormatError
                .copy(paths = Some(List(path1, path2, path3)), message = "The value must be between -99999999999.99 and 99999999999.99"),
              RuleBothExpensesError.withPath("/expenses") // because there's consolidatedExpenses + an addition
            ))
          )
        )
      }

      def testWith(body: JsNumber => JsValue, expectedPath: String): Unit = s"for $expectedPath" when {
        def doTest(value: JsNumber): Assertion = {
          val result = validator(validNino, validCalculationId, None, body(value)).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(
              correlationId,
              ValueFormatError.forPathAndRange(expectedPath, "-99999999999.99", "99999999999.99")
            )
          )
        }

        "value is out of range" in doTest(JsNumber(99999999999.99 + 0.01))

        "value is zero" in doTest(JsNumber(0))
      }
    }

    "return RuleBothExpensesSuppliedError" when {
      "passed consolidated and separate expenses" in {
        val result = validator(validNino, validCalculationId, None, mtdRequestWithBothExpenses).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleBothExpensesError.withPath("/expenses"))
        )
      }
    }

    "return multiple errors" when {
      "passed multiple invalid fields" in {
        val result = validator("not-a-nino", "not-a-calculation-id", None, mtdRequestJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(CalculationIdFormatError, NinoFormatError))
          )
        )
      }
    }

    "return InvalidTaxYearParameterError" when {
      "passed a tax year before TYS" in {
        val result = validator(validNino, validCalculationId, Some("2022-23"), mtdRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, InvalidTaxYearParameterError)
        )
      }
    }
  }

  "return TaxYearFormatError" when {
    "passed an incorrectly formatted tax year" in {
      val result = validator(validNino, validCalculationId, Some("202324"), mtdRequestJson).validateAndWrapResult()
      result shouldBe Left(
        ErrorWrapper(correlationId, TaxYearFormatError)
      )
    }
  }

  "return RuleTaxYearRangeInvalidError error" when {
    "passed a tax year range of more than one year" in {
      val result = validator(validNino, validCalculationId, Some("2022-24"), mtdRequestJson).validateAndWrapResult()
      result shouldBe Left(
        ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
      )
    }
  }

}
