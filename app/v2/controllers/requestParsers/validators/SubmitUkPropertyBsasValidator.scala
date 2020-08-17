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
    List(
      JsonFormatValidation.validate[SubmitUKPropertyBsasRequestBody](data.body.json)
    )
  }

  private def incorrectOrEmptyBodyValidator: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>
    val model: SubmitUKPropertyBsasRequestBody = data.body.json.as[SubmitUKPropertyBsasRequestBody]

    val fhlBody = model.furnishedHolidayLet
    val nonFhlBody = model.nonFurnishedHolidayLet

    List(
      (fhlBody, nonFhlBody) match {
        // Check all objects not empty
        case (Some(_), Some(_)) | (None, None) |
             (Some(FurnishedHolidayLet(None, None)), _) |
             (_, Some(NonFurnishedHolidayLet(None, None))) => List(RuleIncorrectOrEmptyBodyError)
        // total FHL fields entered > 0
        case (Some(fhl), None) =>
          Seq(fhl.income, fhl.expenses).foldLeft[List[MtdError]](NoValidationErrors){
            (noErrors, body) => body match {
              case Some(FHLIncome(None)) |
                   Some(FHLExpenses(None, None, None, None, None, None, None, None)) => List(RuleIncorrectOrEmptyBodyError)
              case _ => noErrors
            }
          }
        // total non-FHL fields entered > 0
        case (None, Some(nonFhl)) =>
          Seq(nonFhl.income, nonFhl.expenses).foldLeft[List[MtdError]](NoValidationErrors){
            (noErrors, body) => body match {
              case Some(NonFHLIncome(None, None, None, None)) |
                   Some(NonFHLExpenses(None, None, None, None, None, None, None, None, None)) => List(RuleIncorrectOrEmptyBodyError)
              case _ => noErrors
            }
          }
        case _ => NoValidationErrors
      }
    )
  }

  private def adjustmentFieldValidator: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>

    val model: SubmitUKPropertyBsasRequestBody = data.body.json.as[SubmitUKPropertyBsasRequestBody]

    def doValidationFor(fieldName: String, withValue: Option[BigDecimal])
                       (f: Seq[(Option[BigDecimal], String) => List[MtdError]]): List[MtdError] =
      f.flatMap(validation => validation(withValue, fieldName)).toList

    val withAdjustmentValidations: Seq[(Option[BigDecimal], String) => List[MtdError]] =
      Seq(AdjustmentValueValidation.validate, AdjustmentRangeValidation.validate)

    (model.furnishedHolidayLet, model.nonFurnishedHolidayLet) match {
      case (None, Some(nonFurnishedHolidayLet)) =>
        val income: Option[NonFHLIncome] = nonFurnishedHolidayLet.income
        val expenses: Option[NonFHLExpenses] = nonFurnishedHolidayLet.expenses
        List(
          doValidationFor("rentIncome", income.flatMap(_.rentIncome))(withAdjustmentValidations),
          doValidationFor("premiumsOfLeaseGrant", income.flatMap(_.premiumsOfLeaseGrant))(withAdjustmentValidations),
          doValidationFor("reversePremiums", income.flatMap(_.reversePremiums))(withAdjustmentValidations),
          doValidationFor("otherPropertyIncome", income.flatMap(_.otherPropertyIncome))(withAdjustmentValidations),
          doValidationFor("premisesRunningCosts", expenses.flatMap(_.premisesRunningCosts))(withAdjustmentValidations),
          doValidationFor("repairsAndMaintenance", expenses.flatMap(_.repairsAndMaintenance))(withAdjustmentValidations),
          doValidationFor("financialCosts", expenses.flatMap(_.financialCosts))(withAdjustmentValidations),
          doValidationFor("professionalFees", expenses.flatMap(_.professionalFees))(withAdjustmentValidations),
          doValidationFor("travelCosts", expenses.flatMap(_.travelCosts))(withAdjustmentValidations),
          doValidationFor("costOfServices", expenses.flatMap(_.costOfServices))(withAdjustmentValidations),
          doValidationFor("residentialFinancialCost", expenses.flatMap(_.residentialFinancialCost))(withAdjustmentValidations),
          doValidationFor("other", expenses.flatMap(_.other))(withAdjustmentValidations),
          doValidationFor("consolidatedExpenses", expenses.flatMap(_.consolidatedExpenses))(withAdjustmentValidations)
        )
      case (Some(furnishedHolidayLet), None) =>
        val income: Option[FHLIncome] = furnishedHolidayLet.income
        val expenses: Option[FHLExpenses] = furnishedHolidayLet.expenses
        List(
          doValidationFor("rentIncome", income.flatMap(_.rentIncome))(withAdjustmentValidations),
          doValidationFor("premisesRunningCosts", expenses.flatMap(_.premisesRunningCosts))(withAdjustmentValidations),
          doValidationFor("repairsAndMaintenance", expenses.flatMap(_.repairsAndMaintenance))(withAdjustmentValidations),
          doValidationFor("financialCosts", expenses.flatMap(_.financialCosts))(withAdjustmentValidations),
          doValidationFor("professionalFees", expenses.flatMap(_.professionalFees))(withAdjustmentValidations),
          doValidationFor("travelCosts", expenses.flatMap(_.travelCosts))(withAdjustmentValidations),
          doValidationFor("costOfServices", expenses.flatMap(_.costOfServices))(withAdjustmentValidations),
          doValidationFor("other", expenses.flatMap(_.other))(withAdjustmentValidations),
          doValidationFor("consolidatedExpenses", expenses.flatMap(_.consolidatedExpenses))(withAdjustmentValidations)
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
