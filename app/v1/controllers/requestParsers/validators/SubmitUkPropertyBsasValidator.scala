/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators

import config.FixedConfig
import v1.controllers.requestParsers.validators.validations._
import v1.models.errors.{MtdError, RuleIncorrectOrEmptyBodyError}
import v1.models.request.submitBsas.{SubmitUkPropertyBsasRawData, SubmitUKPropertyBsasRequestBody}

class SubmitUkPropertyBsasValidator extends Validator[SubmitUkPropertyBsasRawData] with FixedConfig {

  private val validationSet = List(parameterFormatValidator, bodyFormatValidator, adjustmentFieldValidator,
    otherBodyFieldsValidator)

  private def parameterFormatValidator: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>

    List(
      NinoValidation.validate(data.nino),
      BsasIdValidation.validate(data.bsasId)
    )
  }

  private def bodyFormatValidator: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>

    List(
      JsonFormatValidation.validate[SubmitUKPropertyBsasRequestBody](data.body.json, RuleIncorrectOrEmptyBodyError)
    )
  }

  private def adjustmentFieldValidator: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>

    val model: SubmitUKPropertyBsasRequestBody = data.body.json.as[SubmitUKPropertyBsasRequestBody]

    (model.furnishedHolidayLet, model.nonFurnishedHolidayLet) match {
      case (None, Some(nonFurnishedHolidayLet)) =>
        List(
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.income.flatMap(_.rentIncome), "rentIncome"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.income.flatMap(_.premiumsOfLeaseGrant), "premiumsOfLeaseGrant"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.income.flatMap(_.reversePremiums), "reversePremiums"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.income.flatMap(_.otherPropertyIncome), "otherPropertyIncome"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.premisesRunningCosts), "premisesRunningCosts"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.repairsAndMaintenance), "repairsAndMaintenance"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.financialCosts), "financialCosts"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.professionalFees), "professionalFees"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.travelCosts), "travelCosts"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.travelCosts), "travelCosts"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.costOfServices), "costOfServices"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.residentialFinancialCost), "residentialFinancialCost"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.other), "other"),
          AdjustmentValueValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.consolidatedExpenses), "consolidatedExpensesnonFurnishedHolidayLet"),

          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.income.flatMap(_.rentIncome), "rentIncome"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.income.flatMap(_.premiumsOfLeaseGrant), "premiumsOfLeaseGrant"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.income.flatMap(_.reversePremiums), "reversePremiums"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.income.flatMap(_.otherPropertyIncome), "otherPropertyIncome"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.premisesRunningCosts), "premisesRunningCosts"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.repairsAndMaintenance), "repairsAndMaintenance"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.financialCosts), "financialCosts"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.professionalFees), "professionalFees"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.travelCosts), "travelCosts"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.travelCosts), "travelCosts"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.costOfServices), "costOfServices"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.residentialFinancialCost), "residentialFinancialCost"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.other), "other"),
          AdjustmentRangeValidation.validate(nonFurnishedHolidayLet.expenses.flatMap(_.consolidatedExpenses), "consolidatedExpenses")
        )
      case (Some(furnishedHolidayLet), None) =>
        List(
          AdjustmentValueValidation.validate(furnishedHolidayLet.income.flatMap(_.rentIncome), "rentIncome"),
          AdjustmentValueValidation.validate(furnishedHolidayLet.expenses.flatMap(_.premisesRunningCosts), "premisesRunningCosts"),
          AdjustmentValueValidation.validate(furnishedHolidayLet.expenses.flatMap(_.repairsAndMaintenance), "repairsAndMaintenance"),
          AdjustmentValueValidation.validate(furnishedHolidayLet.expenses.flatMap(_.financialCosts), "financialCosts"),
          AdjustmentValueValidation.validate(furnishedHolidayLet.expenses.flatMap(_.professionalFees), "professionalFees"),
          AdjustmentValueValidation.validate(furnishedHolidayLet.expenses.flatMap(_.costOfServices), "costOfServices"),
          AdjustmentValueValidation.validate(furnishedHolidayLet.expenses.flatMap(_.travelCosts), "travelCosts"),
          AdjustmentValueValidation.validate(furnishedHolidayLet.expenses.flatMap(_.other), "other"),
          AdjustmentValueValidation.validate(furnishedHolidayLet.expenses.flatMap(_.consolidatedExpenses), "consolidatedExpense"),

          AdjustmentRangeValidation.validate(furnishedHolidayLet.income.flatMap(_.rentIncome), "rentIncome"),
          AdjustmentRangeValidation.validate(furnishedHolidayLet.expenses.flatMap(_.premisesRunningCosts), "premisesRunningCosts"),
          AdjustmentRangeValidation.validate(furnishedHolidayLet.expenses.flatMap(_.repairsAndMaintenance), "repairsAndMaintenance"),
          AdjustmentRangeValidation.validate(furnishedHolidayLet.expenses.flatMap(_.financialCosts), "financialCosts"),
          AdjustmentRangeValidation.validate(furnishedHolidayLet.expenses.flatMap(_.professionalFees), "professionalFees"),
          AdjustmentRangeValidation.validate(furnishedHolidayLet.expenses.flatMap(_.costOfServices), "costOfServices"),
          AdjustmentRangeValidation.validate(furnishedHolidayLet.expenses.flatMap(_.travelCosts), "travelCosts"),
          AdjustmentRangeValidation.validate(furnishedHolidayLet.expenses.flatMap(_.other), "other"),
          AdjustmentRangeValidation.validate(furnishedHolidayLet.expenses.flatMap(_.consolidatedExpenses), "consolidatedExpenses")
        )
      case _ => List(List(RuleIncorrectOrEmptyBodyError))
    }
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
