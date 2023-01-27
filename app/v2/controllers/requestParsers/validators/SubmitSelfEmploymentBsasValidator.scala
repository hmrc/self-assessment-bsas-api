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

import config.FixedConfig
import v2.controllers.requestParsers.validators.validations._
import v2.models.errors.{MtdError, RuleBothExpensesError, RuleIncorrectOrEmptyBodyError}
import v2.models.request.submitBsas.selfEmployment._

class SubmitSelfEmploymentBsasValidator extends Validator[SubmitSelfEmploymentBsasRawData] with FixedConfig {

  private val validationSet =
    List(parameterFormatValidator, bodyFormatValidator, incorrectOrEmptyBodyValidator, adjustmentFieldValidator, bothExpensesValidator)

  private def parameterFormatValidator: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino),
      BsasIdValidation.validate(data.bsasId)
    )
  }

  private def bodyFormatValidator: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>
    List(
      flattenErrors(
        List(
          JsonFormatValidation.validate[SubmitSelfEmploymentBsasRequestBody](data.body.json)
        )))
  }

  private def incorrectOrEmptyBodyValidator: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>
    val model: SubmitSelfEmploymentBsasRequestBody = data.body.json.as[SubmitSelfEmploymentBsasRequestBody]
    List(
      if (model.isIncorrectOrEmptyBodyError) {
        List(RuleIncorrectOrEmptyBodyError)
      } else {
        NoValidationErrors
      }
    )
  }

  private def adjustmentFieldValidator: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>
    val model: SubmitSelfEmploymentBsasRequestBody = data.body.json.as[SubmitSelfEmploymentBsasRequestBody]

    def doValidationFor(fieldName: String, withValue: Option[BigDecimal]): List[MtdError] = {
      val validations: Seq[(Option[BigDecimal], String) => List[MtdError]] =
        Seq(AdjustmentValueValidation.validate, AdjustmentRangeValidation.validate)
      validations.flatMap(validation => validation(withValue, fieldName)).toList
    }

    def validateIncome(income: Income): List[MtdError] =
      List(
        doValidationFor("/income/turnover", income.turnover),
        doValidationFor("/income/other", income.other)
      ).flatten

    def validateAdditions(additions: Additions): List[MtdError] =
      List(
        doValidationFor("/additions/costOfGoodsBoughtDisallowable", additions.costOfGoodsBoughtDisallowable),
        doValidationFor("/additions/cisPaymentsToSubcontractorsDisallowable", additions.cisPaymentsToSubcontractorsDisallowable),
        doValidationFor("/additions/staffCostsDisallowable", additions.staffCostsDisallowable),
        doValidationFor("/additions/travelCostsDisallowable", additions.travelCostsDisallowable),
        doValidationFor("/additions/premisesRunningCostsDisallowable", additions.premisesRunningCostsDisallowable),
        doValidationFor("/additions/maintenanceCostsDisallowable", additions.maintenanceCostsDisallowable),
        doValidationFor("/additions/adminCostsDisallowable", additions.adminCostsDisallowable),
        doValidationFor("/additions/advertisingCostsDisallowable", additions.advertisingCostsDisallowable),
        doValidationFor("/additions/businessEntertainmentCostsDisallowable", additions.businessEntertainmentCostsDisallowable),
        doValidationFor("/additions/interestDisallowable", additions.interestDisallowable),
        doValidationFor("/additions/financialChargesDisallowable", additions.financialChargesDisallowable),
        doValidationFor("/additions/badDebtDisallowable", additions.badDebtDisallowable),
        doValidationFor("/additions/professionalFeesDisallowable", additions.professionalFeesDisallowable),
        doValidationFor("/additions/depreciationDisallowable", additions.depreciationDisallowable),
        doValidationFor("/additions/otherDisallowable", additions.otherDisallowable)
      ).flatten

    def validateExpenses(expenses: Expenses): List[MtdError] =
      List(
        doValidationFor("/expenses/costOfGoodsBought", expenses.costOfGoodsBought),
        doValidationFor("/expenses/cisPaymentsToSubcontractors", expenses.cisPaymentsToSubcontractors),
        doValidationFor("/expenses/staffCosts", expenses.staffCosts),
        doValidationFor("/expenses/travelCosts", expenses.travelCosts),
        doValidationFor("/expenses/premisesRunningCosts", expenses.premisesRunningCosts),
        doValidationFor("/expenses/maintenanceCosts", expenses.maintenanceCosts),
        doValidationFor("/expenses/adminCosts", expenses.adminCosts),
        doValidationFor("/expenses/advertisingCosts", expenses.advertisingCosts),
        doValidationFor("/expenses/businessEntertainmentCosts", expenses.businessEntertainmentCosts),
        doValidationFor("/expenses/interest", expenses.interest),
        doValidationFor("/expenses/financialCharges", expenses.financialCharges),
        doValidationFor("/expenses/badDebt", expenses.badDebt),
        doValidationFor("/expenses/professionalFees", expenses.professionalFees),
        doValidationFor("/expenses/depreciation", expenses.depreciation),
        doValidationFor("/expenses/other", expenses.other),
        doValidationFor("/expenses/consolidatedExpenses", expenses.consolidatedExpenses),
      ).flatten

    List(
      flattenErrors(
        List(
          model.income.map(validateIncome),
          model.additions.map(validateAdditions),
          model.expenses.map(validateExpenses),
        ).flatten
      )
    )
  }

  private def bothExpensesValidator: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>
    val model: SubmitSelfEmploymentBsasRequestBody = data.body.json.as[SubmitSelfEmploymentBsasRequestBody]

    List(
      if(model.expenses.exists(_.isBothSupplied)
        || (!(model.additions.isEmpty || model.additions.exists(_.isEmpty))
        && (!(model.expenses.isEmpty || model.expenses.exists(_.isConsolidatedExpensesEmpty))))){
        List(RuleBothExpensesError)
      } else {
        NoValidationErrors
      }
    )
  }

  override def validate(data: SubmitSelfEmploymentBsasRawData): List[MtdError] = run(validationSet, data)
}
