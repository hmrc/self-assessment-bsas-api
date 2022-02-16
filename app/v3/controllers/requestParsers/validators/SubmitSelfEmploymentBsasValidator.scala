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

import v3.controllers.requestParsers.validators.validations._
import v3.models.errors._
import v3.models.request.submitBsas.selfEmployment._

class SubmitSelfEmploymentBsasValidator extends Validator[SubmitSelfEmploymentBsasRawData] {

  private val validationSet = List(
    parameterFormatValidation,
    bodyFormatValidation,
    adjustmentFieldValidation,
    bothExpensesSuppliedValidation
  )

  private def parameterFormatValidation: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino),
      CalculationIdValidation.validate(data.calculationId)
    )
  }

  private def bodyFormatValidation: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>
    List(
      flattenErrors(
        List(
          JsonFormatValidation.validateAndCheckNonEmpty[SubmitSelfEmploymentBsasRequestBody](data.body.json)
        )))
  }

  private def adjustmentFieldValidation: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>
    val model: SubmitSelfEmploymentBsasRequestBody = data.body.json.as[SubmitSelfEmploymentBsasRequestBody]

    List(
      flattenErrors(
        List(
          model.income.map(validateIncome),
          model.expenses.map(validateExpenses),
          model.additions.map(validateAdditions)
        ).flatten
      )
    )
  }

  private def validateIncome(income: Income) = {
    List(
      NumberValidation.validateAdjustment(
        field = income.turnover,
        path = "/income/turnover",
        ),
      NumberValidation.validateAdjustment(
        field = income.other,
        path = "/income/other",
      )
    ).flatten
  }

  private def validateExpenses(expenses: Expenses) = {
    List(
      NumberValidation.validateAdjustment(
        field = expenses.costOfGoodsAllowable,
        path = "/expenses/costOfGoodsAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.paymentsToSubcontractorsAllowable,
        path = "/expenses/paymentsToSubcontractorsAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.wagesAndStaffCostsAllowable,
        path = "/expenses/wagesAndStaffCostsAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.carVanTravelExpensesAllowable,
        path = "/expenses/carVanTravelExpensesAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.premisesRunningCostsAllowable,
        path = "/expenses/premisesRunningCostsAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.maintenanceCostsAllowable,
        path = "/expenses/maintenanceCostsAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.adminCostsAllowable,
        path = "/expenses/adminCostsAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.interestOnBankOtherLoansAllowable,
        path = "/expenses/interestOnBankOtherLoansAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.financeChargesAllowable,
        path = "/expenses/financeChargesAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.irrecoverableDebtsAllowable,
        path = "/expenses/irrecoverableDebtsAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.professionalFeesAllowable,
        path = "/expenses/professionalFeesAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.depreciationAllowable,
        path = "/expenses/depreciationAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.otherExpensesAllowable,
        path = "/expenses/otherExpensesAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.advertisingCostsAllowable,
        path = "/expenses/advertisingCostsAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.businessEntertainmentCostsAllowable,
        path = "/expenses/businessEntertainmentCostsAllowable",
      ),
      NumberValidation.validateAdjustment(
        field = expenses.consolidatedExpenses,
        path = "/expenses/consolidatedExpenses",
      )
    ).flatten
  }

  private def validateAdditions(additions: Additions) = {
    List(
      NumberValidation.validateAdjustment(
        field = additions.costOfGoodsDisallowable,
        path = "/additions/costOfGoodsDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.paymentsToSubcontractorsDisallowable,
        path = "/additions/paymentsToSubcontractorsDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.wagesAndStaffCostsDisallowable,
        path = "/additions/wagesAndStaffCostsDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.carVanTravelExpensesDisallowable,
        path = "/additions/carVanTravelExpensesDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.premisesRunningCostsDisallowable,
        path = "/additions/premisesRunningCostsDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.maintenanceCostsDisallowable,
        path = "/additions/maintenanceCostsDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.adminCostsDisallowable,
        path = "/additions/adminCostsDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.interestOnBankOtherLoansDisallowable,
        path = "/additions/interestOnBankOtherLoansDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.financeChargesDisallowable,
        path = "/additions/financeChargesDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.irrecoverableDebtsDisallowable,
        path = "/additions/irrecoverableDebtsDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.professionalFeesDisallowable,
        path = "/additions/professionalFeesDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.depreciationDisallowable,
        path = "/additions/depreciationDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.otherExpensesDisallowable,
        path = "/additions/otherExpensesDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.advertisingCostsDisallowable,
        path = "/additions/advertisingCostsDisallowable",
      ),
      NumberValidation.validateAdjustment(
        field = additions.businessEntertainmentCostsDisallowable,
        path = "/additions/businessEntertainmentCostsDisallowable",
      )
    ).flatten
  }

  private def bothExpensesSuppliedValidation: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>
    val model = data.body.json.as[SubmitSelfEmploymentBsasRequestBody]

    model.expenses
      .map(e =>
        List(
          BothExpensesValidation.bothExpensesValidation(
            expenses = e,
            additions = Additions,
            path = "/expenses",
          )))
      .getOrElse(Nil)
  }

  override def validate(data: SubmitSelfEmploymentBsasRawData): List[MtdError] = run(validationSet, data)

}
