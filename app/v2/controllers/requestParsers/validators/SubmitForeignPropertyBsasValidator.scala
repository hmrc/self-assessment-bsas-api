/*
 * Copyright 2020 HM Revenue & Customs
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

import v2.controllers.requestParsers.validators.validations._
import v2.models.errors.{MtdError, RuleIncorrectOrEmptyBodyError}
import v2.models.request.submitBsas.foreignProperty.{FhlEea, ForeignProperty, SubmitForeignPropertyBsasRequestBody, SubmitForeignPropertyRawData}

class SubmitForeignPropertyBsasValidator extends Validator[SubmitForeignPropertyRawData] {
  private val validationSet = List(
    parameterFormatValidation,
    bodyFormatValidation,
    incorrectOrEmptyBodySubmittedValidation,
    bodyFieldValidation,
    otherBodyFieldsValidator)

  private def parameterFormatValidation: SubmitForeignPropertyRawData => List[List[MtdError]] = (data: SubmitForeignPropertyRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BsasIdValidation.validate(data.bsasId)
    )
  }

  private def bodyFormatValidation: SubmitForeignPropertyRawData => List[List[MtdError]] = { data =>
    List(
      JsonFormatValidation.validate[SubmitForeignPropertyBsasRequestBody](data.body, RuleIncorrectOrEmptyBodyError)
    )
  }

  private def incorrectOrEmptyBodySubmittedValidation: SubmitForeignPropertyRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[SubmitForeignPropertyBsasRequestBody]
    if (body.isIncorrectOrEmptyBody) List(List(RuleIncorrectOrEmptyBodyError)) else NoValidationErrors
  }

  private def bodyFieldValidation: SubmitForeignPropertyRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[SubmitForeignPropertyBsasRequestBody]

    List(flattenErrors(
      List(
        body.foreignProperty.map(validateForeignPropertyRange).getOrElse(NoValidationErrors),
        body.foreignProperty.map(validateForeignPropertyValue).getOrElse(NoValidationErrors),
        body.fhlEea.map(validateFhlEeaRange).getOrElse(NoValidationErrors),
        body.fhlEea.map(validateFhlEeaValue).getOrElse(NoValidationErrors)
      )
    ))
  }

  private def otherBodyFieldsValidator: SubmitForeignPropertyRawData => List[List[MtdError]] = { data =>

    val model: SubmitForeignPropertyBsasRequestBody = data.body.as[SubmitForeignPropertyBsasRequestBody]

    List(
      BothExpensesValidation.validate(model.foreignProperty.flatMap(_.expenses.map(_.params))),
      BothExpensesValidation.validate(model.fhlEea.flatMap(_.expenses.map(_.params)))
    )
  }

  private def validateForeignPropertyRange(foreignProperty: ForeignProperty): List[MtdError] = {
    List(
      AdjustmentRangeValidation.validate(
        field = foreignProperty.income.flatMap(_.rentIncome),
        fieldName = "rentIncome",
        path = s"/foreignProperty/income/rentIncome"
      ),
      AdjustmentRangeValidation.validate(
        field = foreignProperty.income.flatMap(_.premiumsOfLeaseGrant),
        fieldName = "premiumsOfLeaseGrant",
        path = s"/foreignProperty/income/premiumsOfLeaseGrant"
      ),
      AdjustmentRangeValidation.validate(
        field = foreignProperty.income.flatMap(_.foreignTaxTakenOff),
        fieldName = "foreignTaxTakenOff",
        path = s"/foreignProperty/income/foreignTaxTakenOff"
      ),
      AdjustmentRangeValidation.validate(
        field = foreignProperty.income.flatMap(_.otherPropertyIncome),
        fieldName = "otherPropertyIncome",
        path = s"/foreignProperty/income/otherPropertyIncome"
      ),
      AdjustmentRangeValidation.validate(
        field = foreignProperty.expenses.flatMap(_.premisesRunningCosts),
        fieldName = "premisesRunningCosts",
        path = s"/foreignProperty/expenses/premisesRunningCosts"
      ),
      AdjustmentRangeValidation.validate(
        field = foreignProperty.expenses.flatMap(_.repairsAndMaintenance),
        fieldName = "repairsAndMaintenance",
        path = s"/foreignProperty/expenses/repairsAndMaintenance"
      ),
      AdjustmentRangeValidation.validate(
        field = foreignProperty.expenses.flatMap(_.financialCosts),
        fieldName = "financialCosts",
        path = s"/foreignProperty/expenses/financialCosts"
      ),
      AdjustmentRangeValidation.validate(
        field = foreignProperty.expenses.flatMap(_.professionalFees),
        fieldName = "professionalFees",
        path = s"/foreignProperty/expenses/professionalFees"
      ),
      AdjustmentRangeValidation.validate(
        field = foreignProperty.expenses.flatMap(_.travelCosts),
        fieldName = "travelCosts",
        path = s"/foreignProperty/expenses/travelCosts"
      ),
      AdjustmentRangeValidation.validate(
        field = foreignProperty.expenses.flatMap(_.costOfServices),
        fieldName = "costOfServices",
        path = s"/foreignProperty/expenses/costOfServices"
      ),
      AdjustmentRangeValidation.validate(
        field = foreignProperty.expenses.flatMap(_.residentialFinancialCost),
        fieldName = "residentialFinancialCost",
        path = s"/foreignProperty/expenses/residentialFinancialCost"
      ),
      AdjustmentRangeValidation.validate(
        field = foreignProperty.expenses.flatMap(_.other),
        fieldName = "other",
        path = s"/foreignProperty/expenses/other"
      ),
      AdjustmentRangeValidation.validate(
        field = foreignProperty.expenses.flatMap(_.consolidatedExpenses),
        fieldName = "consolidatedExpenses",
        path = s"/foreignProperty/expenses/consolidatedExpenses"
      )
    ).flatten
  }

  private def validateForeignPropertyValue(foreignProperty: ForeignProperty): List[MtdError] = {
    List(
      AdjustmentValueValidation.validate(
        field = foreignProperty.income.flatMap(_.rentIncome),
        fieldName = "rentIncome",
        path = s"/foreignProperty/income/rentIncome"
      ),
      AdjustmentValueValidation.validate(
        field = foreignProperty.income.flatMap(_.premiumsOfLeaseGrant),
        fieldName = "premiumsOfLeaseGrant",
        path = s"/foreignProperty/income/premiumsOfLeaseGrant"
      ),
      AdjustmentValueValidation.validate(
        field = foreignProperty.income.flatMap(_.foreignTaxTakenOff),
        fieldName = "foreignTaxTakenOff",
        path = s"/foreignProperty/income/foreignTaxTakenOff"
      ),
      AdjustmentValueValidation.validate(
        field = foreignProperty.income.flatMap(_.otherPropertyIncome),
        fieldName = "otherPropertyIncome",
        path = s"/foreignProperty/income/otherPropertyIncome"
      ),
      AdjustmentValueValidation.validate(
        field = foreignProperty.expenses.flatMap(_.premisesRunningCosts),
        fieldName = "premisesRunningCosts",
        path = s"/foreignProperty/expenses/premisesRunningCosts"
      ),
      AdjustmentValueValidation.validate(
        field = foreignProperty.expenses.flatMap(_.repairsAndMaintenance),
        fieldName = "repairsAndMaintenance",
        path = s"/foreignProperty/expenses/repairsAndMaintenance"
      ),
      AdjustmentValueValidation.validate(
        field = foreignProperty.expenses.flatMap(_.financialCosts),
        fieldName = "financialCosts",
        path = s"/foreignProperty/expenses/financialCosts"
      ),
      AdjustmentValueValidation.validate(
        field = foreignProperty.expenses.flatMap(_.professionalFees),
        fieldName = "professionalFees",
        path = s"/foreignProperty/expenses/professionalFees"
      ),
      AdjustmentValueValidation.validate(
        field = foreignProperty.expenses.flatMap(_.travelCosts),
        fieldName = "travelCosts",
        path = s"/foreignProperty/expenses/travelCosts"
      ),
      AdjustmentValueValidation.validate(
        field = foreignProperty.expenses.flatMap(_.costOfServices),
        fieldName = "costOfServices",
        path = s"/foreignProperty/expenses/costOfServices"
      ),
      AdjustmentValueValidation.validate(
        field = foreignProperty.expenses.flatMap(_.residentialFinancialCost),
        fieldName = "residentialFinancialCost",
        path = s"/foreignProperty/expenses/residentialFinancialCost"
      ),
      AdjustmentValueValidation.validate(
        field = foreignProperty.expenses.flatMap(_.other),
        fieldName = "other",
        path = s"/foreignProperty/expenses/other"
      ),
      AdjustmentValueValidation.validate(
        field = foreignProperty.expenses.flatMap(_.consolidatedExpenses),
        fieldName = "consolidatedExpenses",
        path = s"/foreignProperty/expenses/consolidatedExpenses"
      )
    ).flatten
  }

  private def validateFhlEeaRange(fhlEea: FhlEea): List[MtdError] = {
    List(
      AdjustmentRangeValidation.validate(
        field = fhlEea.income.flatMap(_.rentIncome),
        fieldName = "rentIncome",
        path = s"/fhlEea/income/rentIncome"
      ),
      AdjustmentRangeValidation.validate(
        field = fhlEea.expenses.flatMap(_.premisesRunningCosts),
        fieldName = "premisesRunningCosts",
        path = s"/fhlEea/expenses/premisesRunningCosts"
      ),
      AdjustmentRangeValidation.validate(
        field = fhlEea.expenses.flatMap(_.repairsAndMaintenance),
        fieldName = "repairsAndMaintenance",
        path = s"/fhlEea/expenses/repairsAndMaintenance"
      ),
      AdjustmentRangeValidation.validate(
        field = fhlEea.expenses.flatMap(_.financialCosts),
        fieldName = "financialCosts",
        path = s"/fhlEea/expenses/financialCosts"
      ),
      AdjustmentRangeValidation.validate(
        field = fhlEea.expenses.flatMap(_.professionalFees),
        fieldName = "professionalFees",
        path = s"/fhlEea/expenses/professionalFees"
      ),
      AdjustmentRangeValidation.validate(
        field = fhlEea.expenses.flatMap(_.travelCosts),
        fieldName = "travelCosts",
        path = s"/fhlEea/expenses/travelCosts"
      ),
      AdjustmentRangeValidation.validate(
        field = fhlEea.expenses.flatMap(_.costOfServices),
        fieldName = "costOfServices",
        path = s"/fhlEea/expenses/costOfServices"
      ),
      AdjustmentRangeValidation.validate(
        field = fhlEea.expenses.flatMap(_.residentialFinancialCost),
        fieldName = "residentialFinancialCost",
        path = s"/fhlEea/expenses/residentialFinancialCost"
      ),
      AdjustmentRangeValidation.validate(
        field = fhlEea.expenses.flatMap(_.other),
        fieldName = "other",
        path = s"/fhlEea/expenses/other"
      ),
      AdjustmentRangeValidation.validate(
        field = fhlEea.expenses.flatMap(_.consolidatedExpenses),
        fieldName = "consolidatedExpenses",
        path = s"/fhlEea/expenses/consolidatedExpenses"
      )
    ).flatten
  }

  private def validateFhlEeaValue(fhlEea: FhlEea): List[MtdError] = {
    List(
      AdjustmentValueValidation.validate(
        field = fhlEea.income.flatMap(_.rentIncome),
        fieldName = "rentIncome",
        path = s"/fhlEea/income/rentIncome"
      ),
      AdjustmentValueValidation.validate(
        field = fhlEea.expenses.flatMap(_.premisesRunningCosts),
        fieldName = "premisesRunningCosts",
        path = s"/fhlEea/expenses/premisesRunningCosts"
      ),
      AdjustmentValueValidation.validate(
        field = fhlEea.expenses.flatMap(_.repairsAndMaintenance),
        fieldName = "repairsAndMaintenance",
        path = s"/fhlEea/expenses/repairsAndMaintenance"
      ),
      AdjustmentValueValidation.validate(
        field = fhlEea.expenses.flatMap(_.financialCosts),
        fieldName = "financialCosts",
        path = s"/fhlEea/expenses/financialCosts"
      ),
      AdjustmentValueValidation.validate(
        field = fhlEea.expenses.flatMap(_.professionalFees),
        fieldName = "professionalFees",
        path = s"/fhlEea/expenses/professionalFees"
      ),
      AdjustmentValueValidation.validate(
        field = fhlEea.expenses.flatMap(_.travelCosts),
        fieldName = "travelCosts",
        path = s"/fhlEea/expenses/travelCosts"
      ),
      AdjustmentValueValidation.validate(
        field = fhlEea.expenses.flatMap(_.costOfServices),
        fieldName = "costOfServices",
        path = s"/fhlEea/expenses/costOfServices"
      ),
      AdjustmentValueValidation.validate(
        field = fhlEea.expenses.flatMap(_.residentialFinancialCost),
        fieldName = "residentialFinancialCost",
        path = s"/fhlEea/expenses/residentialFinancialCost"
      ),
      AdjustmentValueValidation.validate(
        field = fhlEea.expenses.flatMap(_.other),
        fieldName = "other",
        path = s"/fhlEea/expenses/other"
      ),
      AdjustmentValueValidation.validate(
        field = fhlEea.expenses.flatMap(_.consolidatedExpenses),
        fieldName = "consolidatedExpenses",
        path = s"/fhlEea/expenses/consolidatedExpenses"
      )
    ).flatten
  }

  override def validate(data: SubmitForeignPropertyRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }
}