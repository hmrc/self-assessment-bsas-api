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

package v2.controllers.requestParsers.validators

import play.api.libs.json.{Json, OWrites}
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import v2.models.errors._
import v2.models.request.submitBsas.selfEmployment._
import v2.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._

class SubmitSelfEmploymentBsasValidatorSpec extends UnitSpec {
  val validator = new SubmitSelfEmploymentBsasValidator()

  val bsasId   = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  val nino     = "AA123456A"
  val seIncome: Income = Income(Some(100.49), Some(100.49))

  val seExpenses: Expenses = Expenses(
    costOfGoodsBought = Some(100.49),
    cisPaymentsToSubcontractors = Some(100.49),
    staffCosts = Some(100.49),
    travelCosts = Some(100.49),
    premisesRunningCosts = Some(100.49),
    maintenanceCosts = Some(100.49),
    adminCosts = Some(100.49),
    advertisingCosts = Some(100.49),
    businessEntertainmentCosts = Some(100.49),
    interest = Some(100.49),
    financialCharges = Some(100.49),
    badDebt = Some(100.49),
    professionalFees = Some(100.49),
    depreciation = Some(100.49),
    other = Some(100.49),
    consolidatedExpenses = Some(100.49)
  )

  val seAdditions: Additions = Additions(
    costOfGoodsBoughtDisallowable = Some(100.49),
    cisPaymentsToSubcontractorsDisallowable = Some(100.49),
    staffCostsDisallowable = Some(100.49),
    travelCostsDisallowable = Some(100.49),
    premisesRunningCostsDisallowable = Some(100.49),
    maintenanceCostsDisallowable = Some(100.49),
    adminCostsDisallowable = Some(100.49),
    advertisingCostsDisallowable = Some(100.49),
    businessEntertainmentCostsDisallowable = Some(100.49),
    interestDisallowable = Some(100.49),
    financialChargesDisallowable = Some(100.49),
    badDebtDisallowable = Some(100.49),
    professionalFeesDisallowable = Some(100.49),
    depreciationDisallowable = Some(100.49),
    otherDisallowable = Some(100.49)
  )

  def defaultBody(
                   income: Option[Income] = Some(seIncome),
                   expenses: Option[Expenses] = Some(seExpenses),
                   additions: Option[Additions] = Some(seAdditions)
                 ): SubmitSelfEmploymentBsasRequestBody = SubmitSelfEmploymentBsasRequestBody(income, additions, expenses)

  "validator" should {
    "return no errors" when {
      "valid parameters are provided" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, bsasId, AnyContentAsJson(mtdRequest))) shouldBe List()
      }

      "valid parameters are provided with only consolidated expenses" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, bsasId, AnyContentAsJson(mtdRequestWithOnlyConsolidatedExpenses))) shouldBe List()
      }

      "valid parameters are provided with only additions expenses" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, bsasId, AnyContentAsJson(mtdRequestWithOnlyAdditionsExpenses))) shouldBe List()
      }
    }
    "return a single error" when {
      "passed an invalid nino" in {
        validator.validate(SubmitSelfEmploymentBsasRawData("beans", bsasId, AnyContentAsJson(Json.toJson(defaultBody())))) shouldBe List(NinoFormatError)
      }
      "passed an invalid bsasId" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, "beans", AnyContentAsJson(Json.toJson(defaultBody())))) shouldBe List(BsasIdFormatError)
      }
      "passed an empty body" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, bsasId, AnyContentAsJson(Json.obj()))) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
      "passed an invalid body" in {
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, bsasId,
          AnyContentAsJson(Json.obj("income" -> "beans")))) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/income"))))
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, bsasId,
          AnyContentAsJson(Json.obj("income" -> "{}")))) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/income"))))
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, bsasId,
          AnyContentAsJson(Json.toJson(defaultBody(income = Some(Income(None, None))))))) shouldBe List(RuleIncorrectOrEmptyBodyError)
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, bsasId,
          AnyContentAsJson(Json.obj()))) shouldBe List(RuleIncorrectOrEmptyBodyError)
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, bsasId,
          AnyContentAsJson(mtdRequestWithBothExpenses))) shouldBe List(RuleBothExpensesError)
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, bsasId,
          AnyContentAsJson(mtdRequestWithAdditionsAndExpenses))) shouldBe List(RuleBothExpensesError)
      }

      val inputs: Seq[(String, BigDecimal => SubmitSelfEmploymentBsasRequestBody)] = Seq(
        ("/income/turnover", i => defaultBody(income = Some(seIncome.copy(turnover = Some(i))))),
        ("/income/other", i => defaultBody(income = Some(seIncome.copy(other = Some(i))))),
        ("/expenses/costOfGoodsBought", i => defaultBody(expenses = Some(seExpenses.copy(costOfGoodsBought = Some(i))))),
        ("/expenses/cisPaymentsToSubcontractors", i => defaultBody(expenses = Some(seExpenses.copy(cisPaymentsToSubcontractors = Some(i))))),
        ("/expenses/staffCosts", i => defaultBody(expenses = Some(seExpenses.copy(staffCosts = Some(i))))),
        ("/expenses/travelCosts", i => defaultBody(expenses = Some(seExpenses.copy(travelCosts = Some(i))))),
        ("/expenses/premisesRunningCosts", i => defaultBody(expenses = Some(seExpenses.copy(premisesRunningCosts = Some(i))))),
        ("/expenses/maintenanceCosts", i => defaultBody(expenses = Some(seExpenses.copy(maintenanceCosts = Some(i))))),
        ("/expenses/adminCosts", i => defaultBody(expenses = Some(seExpenses.copy(adminCosts = Some(i))))),
        ("/expenses/advertisingCosts", i => defaultBody(expenses = Some(seExpenses.copy(advertisingCosts = Some(i))))),
        ("/expenses/businessEntertainmentCosts", i => defaultBody(expenses = Some(seExpenses.copy(businessEntertainmentCosts = Some(i))))),
        ("/expenses/interest", i => defaultBody(expenses = Some(seExpenses.copy(interest = Some(i))))),
        ("/expenses/financialCharges", i => defaultBody(expenses = Some(seExpenses.copy(financialCharges = Some(i))))),
        ("/expenses/badDebt", i => defaultBody(expenses = Some(seExpenses.copy(badDebt = Some(i))))),
        ("/expenses/professionalFees", i => defaultBody(expenses = Some(seExpenses.copy(professionalFees = Some(i))))),
        ("/expenses/depreciation", i => defaultBody(expenses = Some(seExpenses.copy(depreciation = Some(i))))),
        ("/expenses/other", i => defaultBody(expenses = Some(seExpenses.copy(other = Some(i))))),
        ("/expenses/consolidatedExpenses", i => defaultBody(expenses = Some(seExpenses.copy(consolidatedExpenses = Some(i))))),
        ("/additions/costOfGoodsBoughtDisallowable", i => defaultBody(additions = Some(seAdditions.copy(costOfGoodsBoughtDisallowable = Some(i))))),
        ("/additions/cisPaymentsToSubcontractorsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(cisPaymentsToSubcontractorsDisallowable = Some(i))))),
        ("/additions/staffCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(staffCostsDisallowable = Some(i))))),
        ("/additions/travelCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(travelCostsDisallowable = Some(i))))),
        ("/additions/premisesRunningCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(premisesRunningCostsDisallowable = Some(i))))),
        ("/additions/maintenanceCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(maintenanceCostsDisallowable = Some(i))))),
        ("/additions/adminCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(adminCostsDisallowable = Some(i))))),
        ("/additions/advertisingCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(advertisingCostsDisallowable = Some(i))))),
        ("/additions/businessEntertainmentCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(businessEntertainmentCostsDisallowable = Some(i))))),
        ("/additions/interestDisallowable", i => defaultBody(additions = Some(seAdditions.copy(interestDisallowable = Some(i))))),
        ("/additions/financialChargesDisallowable", i => defaultBody(additions = Some(seAdditions.copy(financialChargesDisallowable = Some(i))))),
        ("/additions/badDebtDisallowable", i => defaultBody(additions = Some(seAdditions.copy(badDebtDisallowable = Some(i))))),
        ("/additions/professionalFeesDisallowable", i => defaultBody(additions = Some(seAdditions.copy(professionalFeesDisallowable = Some(i))))),
        ("/additions/depreciationDisallowable", i => defaultBody(additions = Some(seAdditions.copy(depreciationDisallowable = Some(i))))),
        ("/additions/otherDisallowable", i => defaultBody(additions = Some(seAdditions.copy(otherDisallowable = Some(i)))))
      )

      val testCases: Seq[(String, BigDecimal, String => MtdError)] = Seq(
        ("out of range", 999999999999.99, s => RuleAdjustmentRangeInvalid.copy(paths = Some(Seq(s)))),
        ("with zero value", 0, s => FormatAdjustmentValueError.copy(paths = Some(Seq(s)))),
        ("with more than 2dp", 999999999.999, s => FormatAdjustmentValueError.copy(paths = Some(Seq(s))))
      )

      testCases.foreach {
        case (test, fieldValue, error) =>
          inputs.foreach {
            case (fieldName, body) =>
              s"passed an $fieldName $test" in {
                // need to overwrite default writes for models as default writes are for DES fields
                implicit val incomeWrites: OWrites[Income] = Json.writes[Income]
                implicit val expensesWrites: OWrites[Expenses] = Json.writes[Expenses]
                implicit val additionsWrites: OWrites[Additions] = Json.writes[Additions]

                validator.validate(SubmitSelfEmploymentBsasRawData(nino, bsasId, AnyContentAsJson(
                  Json.toJson(body(fieldValue))(Json.writes[SubmitSelfEmploymentBsasRequestBody])))) shouldBe List(error(fieldName))
              }
          }
      }
    }
  }
}
