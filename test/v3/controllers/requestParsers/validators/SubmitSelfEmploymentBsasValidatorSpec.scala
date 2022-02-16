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

import play.api.libs.json.{JsNumber, _}
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import v3.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._
import v3.models.errors._
import v3.models.request.submitBsas.selfEmployment._
import v3.models.utils.JsonErrorValidators

class SubmitSelfEmploymentBsasValidatorSpec extends UnitSpec with JsonErrorValidators {

  val validator = new SubmitSelfEmploymentBsasValidator()
  val calculationId = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  val nino = "AA123456A"

  "validator" should {
    "return no errors" when {
      "valid parameters are provided" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, calculationId, AnyContentAsJson(mtdRequest))) shouldBe List()
      }

      "valid parameters are provided with only consolidated expenses" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, calculationId, AnyContentAsJson(mtdRequestWithOnlyConsolidatedExpenses))) shouldBe List()
      }

      "valid parameters are provided with only additions expenses" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, calculationId, AnyContentAsJson(mtdRequestWithOnlyAdditionsExpenses))) shouldBe List()
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino = "A12344A", calculationId = calculationId, AnyContentAsJson(mtdRequest))) shouldBe
          List(NinoFormatError)
      }
    }

    "return CalculationIdFormatError error" when {
      "an invalid calculationId is supplied" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino = nino, calculationId = "12345", AnyContentAsJson(mtdRequest))) shouldBe
          List(CalculationIdFormatError)
      }
    }

    "return RuleIncorrectOrEmptyBodyError" when {
      "an empty body is submitted" in {
        validator.validate(
          SubmitSelfEmploymentBsasRawData(
            nino = nino,
            calculationId = calculationId,
            AnyContentAsJson(Json.obj())
          )) shouldBe List(
          RuleIncorrectOrEmptyBodyError)
      }

      "an object/array is empty or mandatory field is missing" when {

        Seq(
          "/income",
          "/income/turnover",
          "/income/other"
        ).foreach(path => testWith(AnyContentAsJson(mtdRequest.replaceWithEmptyObject(path)), path))

        Seq(
          "/expenses",
          "/expenses/costOfGoodsAllowable",
          "/expenses/paymentsToSubcontractorsAllowable",
          "/expenses/wagesAndStaffCostsAllowable"
        ).foreach(path => testWith(AnyContentAsJson(mtdRequest.replaceWithEmptyObject(path)), path))

        Seq(
          "/additions",
          "/additions/costOfGoodsDisallowable",
          "/additions/paymentsToSubcontractorsDisallowable"
        ).foreach(path => testWith(AnyContentAsJson(mtdRequest.replaceWithEmptyObject(path)), path))

        def testWith(body: AnyContentAsJson, expectedPath: String): Unit =
          s"for $expectedPath" in {
            validator.validate(
              SubmitSelfEmploymentBsasRawData(
                nino,
                calculationId,
                body
              )) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq(expectedPath))))
          }
      }

      "income object is empty" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(
          nino,
          calculationId,
          AnyContentAsJson(Json.obj("/income" -> JsObject.empty)))
        ) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "expenses object is empty" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(
          nino,
          calculationId,
          AnyContentAsJson(Json.obj("/expenses" -> JsObject.empty)))
        ) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "additions object is empty" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(
          nino,
          calculationId,
          AnyContentAsJson(Json.obj("/additions" -> JsObject.empty)))
        ) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "fields are empty" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(
          nino,
          calculationId,
          AnyContentAsJson(Json.obj("wagesAndStaffCostsDisallowable" -> JsObject.empty))
        )) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "object is invalid" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(
          nino,
          calculationId,
          AnyContentAsJson(Json.obj("income" -> "beans")))
        ) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/income"))))
      }
    }

    "return ValueFormatError" when {
      "single fields are invalid" when {
        Seq(
          "/income/turnover",
          "/income/other",
          "/expenses/consolidatedExpenses",
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
        ).foreach(path => testWith(mtdRequest.update(path, _), path))
      }

      "consolidated expenses is invalid" when {
        Seq(
          "/expenses/consolidatedExpenses",
        ).foreach(path => testWith(mtdRequestWithOnlyConsolidatedExpenses.update(path, _), path))
      }

      "multiple fields are invalid" in {
        val path1 = "/income/turnover"
        val path2 = "/expenses/consolidatedExpenses"
        val path3 = "/additions/costOfGoodsDisallowable"

        val json: JsValue = Json.parse(
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

        validator.validate(
          SubmitSelfEmploymentBsasRawData(
            nino = nino,
            calculationId = calculationId,
            AnyContentAsJson(json)
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(Seq(path1, path2, path3)), message = "The value must be between -99999999999.99 and 99999999999.99"))
      }

      def testWith(body: JsNumber => JsValue, expectedPath: String): Unit = s"for $expectedPath" when {
        def doTest(value: JsNumber) =
          validator.validate(
            SubmitSelfEmploymentBsasRawData(
              nino,
              calculationId,
              body = AnyContentAsJson(body(value))
            )) shouldBe List(ValueFormatError.forPathAndRange(expectedPath, "-99999999999.99", "99999999999.99"))

        "value is out of range" in doTest(JsNumber(99999999999.99 + 0.01))

        "value is zero" in doTest(JsNumber(0))
      }
    }

    "return RuleBothExpensesSuppliedError" when {
      "consolidated and separate expenses provided" when {
        Seq(
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
          "/expenses/consolidatedExpenses",
        ).foreach(path => testWith(mtdRequestWithBothExpenses.update(path, _), path))
      }

      def testWith(body: JsNumber => JsValue, expectedPath: String): Unit = s"for $expectedPath" when {
        def doTest(value: JsNumber) =
          validator.validate(
            SubmitSelfEmploymentBsasRawData(
              nino,
              calculationId,
              body = AnyContentAsJson(body(value))
            )) shouldBe List(RuleBothExpensesError.copy(paths = Some(Seq(expectedPath))))
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino = "A12344A", calculationId = "badCalcId", AnyContentAsJson(mtdRequest))) shouldBe
          List(NinoFormatError, CalculationIdFormatError)
      }
    }
  }
}
