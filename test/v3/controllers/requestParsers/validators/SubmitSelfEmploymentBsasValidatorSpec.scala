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

import play.api.libs.json.{Json, OWrites}
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import v3.models.errors._
import v3.models.request.submitBsas.selfEmployment._
import v3.fixtures.selfEmployment.SubmitSelfEmploymentBsasFixtures._

class SubmitSelfEmploymentBsasValidatorSpec extends UnitSpec {
  val validator = new SubmitSelfEmploymentBsasValidator()

  val bsasId   = "a54ba782-5ef4-47f4-ab72-495406665ca9"
  val nino     = "AA123456A"
  val seIncome: Income = Income(Some(100.49), Some(100.49))

  val seExpenses: Expenses = Expenses(
    costOfGoodsAllowable = Some(100.49),
    paymentsToSubcontractorsAllowable = Some(100.49),
    wagesAndStaffCostsAllowable = Some(100.49),
    carVanTravelExpensesAllowable = Some(100.49),
    premisesRunningCostsAllowable = Some(100.49),
    maintenanceCostsAllowable = Some(100.49),
    adminCostsAllowable = Some(100.49),
    advertisingCostsAllowable = Some(100.49),
    businessEntertainmentCostsAllowable = Some(100.49),
    interestOnBankOtherLoansAllowable = Some(100.49),
    financeChargesAllowable = Some(100.49),
    irrecoverableDebtsAllowable = Some(100.49),
    professionalFeesAllowable = Some(100.49),
    depreciationAllowable = Some(100.49),
    otherExpensesAllowable = Some(100.49),
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
    advertisingCostsDisallowable = Some(100.49),
    businessEntertainmentCostsDisallowable = Some(100.49),
    interestOnBankOtherLoansDisallowable = Some(100.49),
    financeChargesDisallowable = Some(100.49),
    irrecoverableDebtsDisallowable = Some(100.49),
    professionalFeesDisallowable = Some(100.49),
    depreciationDisallowable = Some(100.49),
    otherExpensesDisallowable = Some(100.49)
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
        validator.validate(SubmitSelfEmploymentBsasRawData(nino, "beans", AnyContentAsJson(Json.toJson(defaultBody())))) shouldBe List(CalculationIdFormatError)
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
        ("/expenses/costOfGoodsBought", i => defaultBody(expenses = Some(seExpenses.copy(costOfGoodsAllowable = Some(i))))),
        ("/expenses/cisPaymentsToSubcontractors", i => defaultBody(expenses = Some(seExpenses.copy(paymentsToSubcontractorsAllowable = Some(i))))),
        ("/expenses/staffCosts", i => defaultBody(expenses = Some(seExpenses.copy(wagesAndStaffCostsAllowable = Some(i))))),
        ("/expenses/travelCosts", i => defaultBody(expenses = Some(seExpenses.copy(carVanTravelExpensesAllowable = Some(i))))),
        ("/expenses/premisesRunningCosts", i => defaultBody(expenses = Some(seExpenses.copy(premisesRunningCostsAllowable = Some(i))))),
        ("/expenses/maintenanceCosts", i => defaultBody(expenses = Some(seExpenses.copy(maintenanceCostsAllowable = Some(i))))),
        ("/expenses/adminCosts", i => defaultBody(expenses = Some(seExpenses.copy(adminCostsAllowable = Some(i))))),
        ("/expenses/advertisingCosts", i => defaultBody(expenses = Some(seExpenses.copy(advertisingCostsAllowable = Some(i))))),
        ("/expenses/businessEntertainmentCosts", i => defaultBody(expenses = Some(seExpenses.copy(businessEntertainmentCostsAllowable = Some(i))))),
        ("/expenses/interest", i => defaultBody(expenses = Some(seExpenses.copy(interestOnBankOtherLoansAllowable = Some(i))))),
        ("/expenses/financialCharges", i => defaultBody(expenses = Some(seExpenses.copy(financeChargesAllowable = Some(i))))),
        ("/expenses/badDebt", i => defaultBody(expenses = Some(seExpenses.copy(irrecoverableDebtsAllowable = Some(i))))),
        ("/expenses/professionalFees", i => defaultBody(expenses = Some(seExpenses.copy(professionalFeesAllowable = Some(i))))),
        ("/expenses/depreciation", i => defaultBody(expenses = Some(seExpenses.copy(depreciationAllowable = Some(i))))),
        ("/expenses/other", i => defaultBody(expenses = Some(seExpenses.copy(otherExpensesAllowable = Some(i))))),
        ("/expenses/consolidatedExpenses", i => defaultBody(expenses = Some(seExpenses.copy(consolidatedExpenses = Some(i))))),
        ("/additions/costOfGoodsBoughtDisallowable", i => defaultBody(additions = Some(seAdditions.copy(costOfGoodsDisallowable = Some(i))))),
        ("/additions/cisPaymentsToSubcontractorsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(paymentsToSubcontractorsDisallowable = Some(i))))),
        ("/additions/staffCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(wagesAndStaffCostsDisallowable = Some(i))))),
        ("/additions/travelCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(carVanTravelExpensesDisallowable = Some(i))))),
        ("/additions/premisesRunningCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(premisesRunningCostsDisallowable = Some(i))))),
        ("/additions/maintenanceCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(maintenanceCostsDisallowable = Some(i))))),
        ("/additions/adminCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(adminCostsDisallowable = Some(i))))),
        ("/additions/advertisingCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(advertisingCostsDisallowable = Some(i))))),
        ("/additions/businessEntertainmentCostsDisallowable", i => defaultBody(additions = Some(seAdditions.copy(businessEntertainmentCostsDisallowable = Some(i))))),
        ("/additions/interestDisallowable", i => defaultBody(additions = Some(seAdditions.copy(interestOnBankOtherLoansDisallowable = Some(i))))),
        ("/additions/financialChargesDisallowable", i => defaultBody(additions = Some(seAdditions.copy(financeChargesDisallowable = Some(i))))),
        ("/additions/badDebtDisallowable", i => defaultBody(additions = Some(seAdditions.copy(irrecoverableDebtsDisallowable = Some(i))))),
        ("/additions/professionalFeesDisallowable", i => defaultBody(additions = Some(seAdditions.copy(professionalFeesDisallowable = Some(i))))),
        ("/additions/depreciationDisallowable", i => defaultBody(additions = Some(seAdditions.copy(depreciationDisallowable = Some(i))))),
        ("/additions/otherDisallowable", i => defaultBody(additions = Some(seAdditions.copy(otherExpensesDisallowable = Some(i)))))
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
