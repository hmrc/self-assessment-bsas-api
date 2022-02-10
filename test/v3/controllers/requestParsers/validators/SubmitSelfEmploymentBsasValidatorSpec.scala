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
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import v3.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._
import v3.models.errors._
import v3.models.request.submitBsas.selfEmployment._

class SubmitSelfEmploymentBsasValidatorSpec extends UnitSpec {
  val validator = new SubmitSelfEmploymentBsasValidator()

  val calculationId = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  val nino = "AA123456A"

  val seIncome: Income = Income(
    Some(100.49),
    Some(100.49)
  )

  val seExpenses: Expenses = Expenses(
    costOfGoodsAllowable = Some(100.49),
    paymentsToSubcontractorsAllowable = Some(100.49),
    wagesAndStaffCostsAllowable = Some(100.49),
    carVanTravelExpensesAllowable = Some(100.49),
    premisesRunningCostsAllowable = Some(100.49),
    maintenanceCostsAllowable = Some(100.49),
    adminCostsAllowable = Some(100.49),
    interestOnBankOtherLoansAllowable = Some(100.49),
    financeChargesAllowable = Some(100.49),
    irrecoverableDebtsAllowable = Some(100.49),
    professionalFeesAllowable = Some(100.49),
    depreciationAllowable = Some(100.49),
    otherExpensesAllowable = Some(100.49),
    advertisingCostsAllowable = Some(100.49),
    businessEntertainmentCostsAllowable = Some(100.49),
    consolidatedExpenses = Some(100.49)
  )

  val seAdditions: Additions = Additions(
    costOfGoodsDisallowable = Some(100.49),
    paymentsToSubcontractorsDisallowable = Some(100.49),
    wagesAndStaffCostsDisallowable = Some(100.49),
    carVanTravelExpensesDisallowable = Some(100.49),
    premisesRunningCostsDisallowable = Some(100.49),
    maintenanceCostsDisallowable = Some(100.49),
    adminCostsDisallowable = Some(100.49),
    interestOnBankOtherLoansDisallowable = Some(100.49),
    financeChargesDisallowable = Some(100.49),
    irrecoverableDebtsDisallowable = Some(100.49),
    professionalFeesDisallowable = Some(100.49),
    depreciationDisallowable = Some(100.49),
    otherExpensesDisallowable = Some(100.49),
    advertisingCostsDisallowable = Some(100.49),
    businessEntertainmentCostsDisallowable = Some(100.49)
  )

  def defaultBody(
                   income: Option[Income] = Some(seIncome),
                   expenses: Option[Expenses] = Some(seExpenses),
                   additions: Option[Additions] = Some(seAdditions)
                 ): SubmitSelfEmploymentBsasRequestBody = SubmitSelfEmploymentBsasRequestBody(income, expenses,additions)

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
        validator.validate(SubmitSelfEmploymentBsasRawData(nino = nino, calculationId = calculationId, AnyContentAsJson(Json.obj()))) shouldBe List(
          RuleIncorrectOrEmptyBodyError)
      }

      "object is empty" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(
          nino,
          calculationId,
          AnyContentAsJson(Json.obj("income" -> "{}")))
        ) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/income"))))
      }

      "fields are empty" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(
          nino,
          calculationId,
          AnyContentAsJson(Json.toJson(defaultBody(income = Some(Income(None, None))))))
        ) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "object is invalid" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(
          nino,
          calculationId,
          AnyContentAsJson(Json.obj("income" -> "beans")))
        ) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/income"))))
      }
    }

    "return RuleBothExpensesError" when {
      "both expenses are present" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(
          nino,
          calculationId,
          AnyContentAsJson(mtdRequestWithBothExpenses))
        ) shouldBe List(RuleBothExpensesError)
      }
    }

    "return ValueFormatError" when {
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
    }

    "return multiple errors" when {
      "request supplied has multiple errors" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino = "A12344A", calculationId = "badCalcId", AnyContentAsJson(mtdRequest))) shouldBe
          List(NinoFormatError, CalculationIdFormatError)
      }
    }
  }
}
