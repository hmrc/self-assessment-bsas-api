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
    List(flattenErrors(List(
      JsonFormatValidation.validate[SubmitForeignPropertyBsasRequestBody](data.body)
    )))
  }

  private def incorrectOrEmptyBodySubmittedValidation: SubmitForeignPropertyRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[SubmitForeignPropertyBsasRequestBody]
    if (body.isEmpty) List(List(RuleIncorrectOrEmptyBodyError)) else NoValidationErrors
  }

  private def bodyFieldValidation: SubmitForeignPropertyRawData => List[List[MtdError]] = { data =>

    val model: SubmitForeignPropertyBsasRequestBody = data.body.as[SubmitForeignPropertyBsasRequestBody]

    def doValidationFor(fieldName: String, withValue: Option[BigDecimal])
                       (f: Seq[(Option[BigDecimal], String) => List[MtdError]]): List[MtdError] =
      f.flatMap(validation => validation(withValue, fieldName)).toList

    val withAdjustmentValidations: Seq[(Option[BigDecimal], String) => List[MtdError]] =
      Seq(AdjustmentValueValidation.validate, AdjustmentRangeValidation.validate)

    def validateForeignProperty(foreignProperty: ForeignProperty): List[MtdError] = {
      List(
        doValidationFor("/foreignProperty/income/rentIncome", foreignProperty.income.flatMap(_.rentIncome))(withAdjustmentValidations),
        doValidationFor("/foreignProperty/income/premiumsOfLeaseGrant", foreignProperty.income.flatMap(_.premiumsOfLeaseGrant))(withAdjustmentValidations),
        doValidationFor("/foreignProperty/income/foreignTaxTakenOff", foreignProperty.income.flatMap(_.foreignTaxTakenOff))(withAdjustmentValidations),
        doValidationFor("/foreignProperty/income/otherPropertyIncome", foreignProperty.income.flatMap(_.otherPropertyIncome))(withAdjustmentValidations),
        doValidationFor("/foreignProperty/expenses/premisesRunningCosts", foreignProperty.expenses.flatMap(_.premisesRunningCosts))(withAdjustmentValidations),
        doValidationFor("/foreignProperty/expenses/repairsAndMaintenance", foreignProperty.expenses.flatMap(_.repairsAndMaintenance))(withAdjustmentValidations),
        doValidationFor("/foreignProperty/expenses/financialCosts", foreignProperty.expenses.flatMap(_.financialCosts))(withAdjustmentValidations),
        doValidationFor("/foreignProperty/expenses/professionalFees", foreignProperty.expenses.flatMap(_.professionalFees))(withAdjustmentValidations),
        doValidationFor("/foreignProperty/expenses/travelCosts", foreignProperty.expenses.flatMap(_.travelCosts))(withAdjustmentValidations),
        doValidationFor("/foreignProperty/expenses/costOfServices", foreignProperty.expenses.flatMap(_.costOfServices))(withAdjustmentValidations),
        doValidationFor("/foreignProperty/expenses/residentialFinancialCost", foreignProperty.expenses.flatMap(_.residentialFinancialCost))(withAdjustmentValidations),
        doValidationFor("/foreignProperty/expenses/other", foreignProperty.expenses.flatMap(_.other))(withAdjustmentValidations),
        doValidationFor("/foreignProperty/expenses/consolidatedExpenses", foreignProperty.expenses.flatMap(_.consolidatedExpenses))(withAdjustmentValidations)
      ).flatten
    }

    def validateForeignFhlEea(foreignFhlEea: FhlEea): List[MtdError] = {
      List(
        doValidationFor("/foreignFhlEea/income/rentIncome", foreignFhlEea.income.flatMap(_.rentIncome))(withAdjustmentValidations),
        doValidationFor("/foreignFhlEea/expenses/premisesRunningCosts", foreignFhlEea.expenses.flatMap(_.premisesRunningCosts))(withAdjustmentValidations),
        doValidationFor("/foreignFhlEea/expenses/repairsAndMaintenance", foreignFhlEea.expenses.flatMap(_.repairsAndMaintenance))(withAdjustmentValidations),
        doValidationFor("/foreignFhlEea/expenses/financialCosts", foreignFhlEea.expenses.flatMap(_.financialCosts))(withAdjustmentValidations),
        doValidationFor("/foreignFhlEea/expenses/professionalFees", foreignFhlEea.expenses.flatMap(_.professionalFees))(withAdjustmentValidations),
        doValidationFor("/foreignFhlEea/expenses/travelCosts", foreignFhlEea.expenses.flatMap(_.travelCosts))(withAdjustmentValidations),
        doValidationFor("/foreignFhlEea/expenses/costOfServices", foreignFhlEea.expenses.flatMap(_.costOfServices))(withAdjustmentValidations),
        doValidationFor("/foreignFhlEea/expenses/other", foreignFhlEea.expenses.flatMap(_.other))(withAdjustmentValidations),
        doValidationFor("/foreignFhlEea/expenses/consolidatedExpenses", foreignFhlEea.expenses.flatMap(_.consolidatedExpenses))(withAdjustmentValidations)
      ).flatten
    }

    List(flattenErrors(List(
      model.foreignFhlEea.map(validateForeignFhlEea).getOrElse(NoValidationErrors),
      model.foreignProperty.map(validateForeignProperty).getOrElse(NoValidationErrors)
    )))

  }

  private def otherBodyFieldsValidator: SubmitForeignPropertyRawData => List[List[MtdError]] = {
    data =>

      val model: SubmitForeignPropertyBsasRequestBody = data.body.as[SubmitForeignPropertyBsasRequestBody]

      List(
        BothExpensesValidation.validate(model.foreignProperty.flatMap(_.expenses.map(_.params))),
        BothExpensesValidation.validate(model.foreignFhlEea.flatMap(_.expenses.map(_.params)))
      )
  }

  override def validate(data: SubmitForeignPropertyRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }
}