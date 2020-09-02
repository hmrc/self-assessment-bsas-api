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
    if (body.isIncorrectOrEmptyBody) {
      List(List(RuleIncorrectOrEmptyBodyError))
    } else {
      NoValidationErrors
    }
  }

  private def bodyFieldValidation: SubmitForeignPropertyRawData => List[List[MtdError]] = { data =>

    val model: SubmitForeignPropertyBsasRequestBody = data.body.as[SubmitForeignPropertyBsasRequestBody]

    def doValidationFor(fieldName: String, withValue: Option[BigDecimal]): List[MtdError] = {
      val validations: Seq[(Option[BigDecimal], String) => List[MtdError]] = Seq(AdjustmentValueValidation.validate, AdjustmentRangeValidation.validate)
      validations.flatMap(validation => validation(withValue, fieldName)).toList
    }

    def validateForeignProperty(foreignProperty: ForeignProperty): List[List[MtdError]] = {
      List(
        doValidationFor("/foreignProperty/income/rentIncome", foreignProperty.income.flatMap(_.rentIncome)),
        doValidationFor("/foreignProperty/income/premiumsOfLeaseGrant", foreignProperty.income.flatMap(_.premiumsOfLeaseGrant)),
        doValidationFor("/foreignProperty/income/foreignTaxTakenOff", foreignProperty.income.flatMap(_.foreignTaxTakenOff)),
        doValidationFor("/foreignProperty/income/otherPropertyIncome", foreignProperty.income.flatMap(_.otherPropertyIncome)),
        doValidationFor("/foreignProperty/expenses/premisesRunningCosts", foreignProperty.expenses.flatMap(_.premisesRunningCosts)),
        doValidationFor("/foreignProperty/expenses/repairsAndMaintenance", foreignProperty.expenses.flatMap(_.repairsAndMaintenance)),
        doValidationFor("/foreignProperty/expenses/financialCosts", foreignProperty.expenses.flatMap(_.financialCosts)),
        doValidationFor("/foreignProperty/expenses/professionalFees", foreignProperty.expenses.flatMap(_.professionalFees)),
        doValidationFor("/foreignProperty/expenses/travelCosts", foreignProperty.expenses.flatMap(_.travelCosts)),
        doValidationFor("/foreignProperty/expenses/costOfServices", foreignProperty.expenses.flatMap(_.costOfServices)),
        doValidationFor("/foreignProperty/expenses/residentialFinancialCost", foreignProperty.expenses.flatMap(_.residentialFinancialCost)),
        doValidationFor("/foreignProperty/expenses/other", foreignProperty.expenses.flatMap(_.other)),
        doValidationFor("/foreignProperty/expenses/consolidatedExpenses", foreignProperty.expenses.flatMap(_.consolidatedExpenses))
      )
    }

    def validateForeignFhlEea(foreignFhlEea: FhlEea): List[List[MtdError]] = {
      List(
        doValidationFor("/foreignFhlEea/income/rentIncome", foreignFhlEea.income.flatMap(_.rentIncome)),
        doValidationFor("/foreignFhlEea/expenses/premisesRunningCosts", foreignFhlEea.expenses.flatMap(_.premisesRunningCosts)),
        doValidationFor("/foreignFhlEea/expenses/repairsAndMaintenance", foreignFhlEea.expenses.flatMap(_.repairsAndMaintenance)),
        doValidationFor("/foreignFhlEea/expenses/financialCosts", foreignFhlEea.expenses.flatMap(_.financialCosts)),
        doValidationFor("/foreignFhlEea/expenses/professionalFees", foreignFhlEea.expenses.flatMap(_.professionalFees)),
        doValidationFor("/foreignFhlEea/expenses/travelCosts", foreignFhlEea.expenses.flatMap(_.travelCosts)),
        doValidationFor("/foreignFhlEea/expenses/costOfServices", foreignFhlEea.expenses.flatMap(_.costOfServices)),
        doValidationFor("/foreignFhlEea/expenses/other", foreignFhlEea.expenses.flatMap(_.other)),
        doValidationFor("/foreignFhlEea/expenses/consolidatedExpenses", foreignFhlEea.expenses.flatMap(_.consolidatedExpenses))
      )
    }

    List(flattenErrors(
      (model.foreignFhlEea, model.foreignProperty) match {
        case (Some(foreignFhlEea), None) => validateForeignFhlEea(foreignFhlEea)
        case (None, Some(foreignProperty)) => validateForeignProperty(foreignProperty)
        case _ => List(List(RuleIncorrectOrEmptyBodyError))
      }
    ))
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