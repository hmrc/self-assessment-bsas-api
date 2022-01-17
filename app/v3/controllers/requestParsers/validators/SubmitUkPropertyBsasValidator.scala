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

import config.FixedConfig
import v2.controllers.requestParsers.validators.validations.{JsonFormatValidation, _}
import v2.models.errors.{MtdError, RuleIncorrectOrEmptyBodyError}
import v2.models.request.submitBsas.ukProperty._

class SubmitUkPropertyBsasValidator extends Validator[SubmitUkPropertyBsasRawData] with FixedConfig {

  private val validationSet = List(
    parameterFormatValidator,
    bodyFormatValidator,
    incorrectOrEmptyBodyValidator,
    adjustmentFieldValidator,
    otherBodyFieldsValidator)

  private def parameterFormatValidator: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>

    List(
      NinoValidation.validate(data.nino),
      BsasIdValidation.validate(data.bsasId)
    )
  }

  private def bodyFormatValidator: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>
    List(flattenErrors(List(
      JsonFormatValidation.validate[SubmitUKPropertyBsasRequestBody](data.body.json)
    )))
  }

  private def incorrectOrEmptyBodyValidator: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>
    val model: SubmitUKPropertyBsasRequestBody = data.body.json.as[SubmitUKPropertyBsasRequestBody]
    List(
      if (model.isIncorrectOrEmptyBody) {
        List(RuleIncorrectOrEmptyBodyError)
      } else {
        NoValidationErrors
      }
    )
  }

  private def adjustmentFieldValidator: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>
    val model: SubmitUKPropertyBsasRequestBody = data.body.json.as[SubmitUKPropertyBsasRequestBody]

    def doValidationFor(fieldName: String, withValue: Option[BigDecimal]): List[MtdError] = {
      val validations: Seq[(Option[BigDecimal], String) => List[MtdError]] = Seq(AdjustmentValueValidation.validate, AdjustmentRangeValidation.validate)
      validations.flatMap(validation => validation(withValue, fieldName)).toList
    }

    def validateNonFHL(nonFurnishedHolidayLet: NonFurnishedHolidayLet): List[List[MtdError]] = {
      val income: Option[NonFHLIncome] = nonFurnishedHolidayLet.income
      val expenses: Option[NonFHLExpenses] = nonFurnishedHolidayLet.expenses
      List(
        doValidationFor("/nonFurnishedHolidayLet/income/rentIncome", income.flatMap(_.rentIncome)),
        doValidationFor("/nonFurnishedHolidayLet/income/premiumsOfLeaseGrant", income.flatMap(_.premiumsOfLeaseGrant)),
        doValidationFor("/nonFurnishedHolidayLet/income/reversePremiums", income.flatMap(_.reversePremiums)),
        doValidationFor("/nonFurnishedHolidayLet/income/otherPropertyIncome", income.flatMap(_.otherPropertyIncome)),
        doValidationFor("/nonFurnishedHolidayLet/expenses/premisesRunningCosts", expenses.flatMap(_.premisesRunningCosts)),
        doValidationFor("/nonFurnishedHolidayLet/expenses/repairsAndMaintenance", expenses.flatMap(_.repairsAndMaintenance)),
        doValidationFor("/nonFurnishedHolidayLet/expenses/financialCosts", expenses.flatMap(_.financialCosts)),
        doValidationFor("/nonFurnishedHolidayLet/expenses/professionalFees", expenses.flatMap(_.professionalFees)),
        doValidationFor("/nonFurnishedHolidayLet/expenses/travelCosts", expenses.flatMap(_.travelCosts)),
        doValidationFor("/nonFurnishedHolidayLet/expenses/costOfServices", expenses.flatMap(_.costOfServices)),
        doValidationFor("/nonFurnishedHolidayLet/expenses/residentialFinancialCost", expenses.flatMap(_.residentialFinancialCost)),
        doValidationFor("/nonFurnishedHolidayLet/expenses/other", expenses.flatMap(_.other)),
        doValidationFor("/nonFurnishedHolidayLet/expenses/consolidatedExpenses", expenses.flatMap(_.consolidatedExpenses))
      )
    }

    def validateFHL(furnishedHolidayLet: FurnishedHolidayLet): List[List[MtdError]] = {
      val income: Option[FHLIncome] = furnishedHolidayLet.income
      val expenses: Option[FHLExpenses] = furnishedHolidayLet.expenses
      List(
        doValidationFor("/furnishedHolidayLet/income/rentIncome", income.flatMap(_.rentIncome)),
        doValidationFor("/furnishedHolidayLet/expenses/premisesRunningCosts", expenses.flatMap(_.premisesRunningCosts)),
        doValidationFor("/furnishedHolidayLet/expenses/repairsAndMaintenance", expenses.flatMap(_.repairsAndMaintenance)),
        doValidationFor("/furnishedHolidayLet/expenses/financialCosts", expenses.flatMap(_.financialCosts)),
        doValidationFor("/furnishedHolidayLet/expenses/professionalFees", expenses.flatMap(_.professionalFees)),
        doValidationFor("/furnishedHolidayLet/expenses/travelCosts", expenses.flatMap(_.travelCosts)),
        doValidationFor("/furnishedHolidayLet/expenses/costOfServices", expenses.flatMap(_.costOfServices)),
        doValidationFor("/furnishedHolidayLet/expenses/other", expenses.flatMap(_.other)),
        doValidationFor("/furnishedHolidayLet/expenses/consolidatedExpenses", expenses.flatMap(_.consolidatedExpenses))
      )
    }

    List(flattenErrors((model.furnishedHolidayLet, model.nonFurnishedHolidayLet) match {
      case (None, Some(nonFurnishedHolidayLet)) => validateNonFHL(nonFurnishedHolidayLet)
      case (Some(furnishedHolidayLet), None) =>validateFHL(furnishedHolidayLet)
      case _ => List(List(RuleIncorrectOrEmptyBodyError))
    }))
  }

  private def otherBodyFieldsValidator: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>

    val model: SubmitUKPropertyBsasRequestBody = data.body.json.as[SubmitUKPropertyBsasRequestBody]

    List(
      BothExpensesValidation.validate(model.furnishedHolidayLet.flatMap(_.expenses.map(_.params))),
      BothExpensesValidation.validate(model.nonFurnishedHolidayLet.flatMap(_.expenses.map(_.params)))
    )
  }

  override def validate(data: SubmitUkPropertyBsasRawData): List[MtdError] = run(validationSet, data)
}
