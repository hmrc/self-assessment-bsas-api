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

package v3.controllers.requestParsers.validators

import api.controllers.requestParsers.validators.Validator
import api.controllers.requestParsers.validators.validations._
import api.models.errors.MtdError
import config.FixedConfig
import v3.controllers.requestParsers.validators.validations._
import v3.models.errors._
import v3.models.request.submitBsas.ukProperty._

import scala.annotation.nowarn

class SubmitUkPropertyBsasValidator extends Validator[SubmitUkPropertyBsasRawData] with FixedConfig {

  private val validationSet =
    List(parameterFormatValidator, parameterRuleValidation, validateOnePropertyOnly, bodyFormatValidation, bodyFieldValidation)

  override def validate(data: SubmitUkPropertyBsasRawData): List[MtdError] = run(validationSet, data)

  private def parameterFormatValidator: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino),
      CalculationIdValidation.validate(data.calculationId),
      data.taxYear.map(TaxYearValidation.validate).getOrElse(Nil)
    )
  }

  private def parameterRuleValidation: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>
    List(
      data.taxYear.map(TaxYearTYSParameterValidation.validate).getOrElse(Nil)
    )
  }

  private def validateOnePropertyOnly: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>
    if (Seq("furnishedHolidayLet", "nonFurnishedHolidayLet").forall(field => (data.body \ field).isDefined)) {
      List(List(RuleBothPropertiesSuppliedError))
    } else {
      Nil
    }
  }

  @nowarn("cat=lint-byname-implicit")
  private def bodyFormatValidation: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>
    JsonFormatValidation.validateAndCheckNonEmpty[SubmitUKPropertyBsasRequestBody](data.body) match {
      case Nil => NoValidationErrors
      case schemaErrors => List(schemaErrors)
    }
  }

  private def bodyFieldValidation: SubmitUkPropertyBsasRawData => List[List[MtdError]] = { data =>
    val body: SubmitUKPropertyBsasRequestBody = data.body.as[SubmitUKPropertyBsasRequestBody]

    List(
      flattenErrors(
        List(
          body.furnishedHolidayLet.map(validateFhl).getOrElse(NoValidationErrors),
          body.nonFurnishedHolidayLet.map(validateNonFhl).getOrElse(NoValidationErrors),
          body.furnishedHolidayLet.flatMap(_.expenses.map(validateFhlConsolidatedExpenses)).getOrElse(NoValidationErrors),
          body.nonFurnishedHolidayLet.flatMap(_.expenses.map(validateNonFhlConsolidatedExpenses)).getOrElse(NoValidationErrors),
        ))
    )
  }

  private def validateFhl(fhl: FurnishedHolidayLet) = {
    List(
      NumberValidation.validateAdjustment(
        field = fhl.income.flatMap(_.totalRentsReceived),
        path = "/furnishedHolidayLet/income/totalRentsReceived"
      ),
      NumberValidation.validateAdjustment(
        field = fhl.expenses.flatMap(_.premisesRunningCosts),
        path = "/furnishedHolidayLet/expenses/premisesRunningCosts"
      ),
      NumberValidation.validateAdjustment(
        field = fhl.expenses.flatMap(_.repairsAndMaintenance),
        path = "/furnishedHolidayLet/expenses/repairsAndMaintenance"
      ),
      NumberValidation.validateAdjustment(
        field = fhl.expenses.flatMap(_.financialCosts),
        path = "/furnishedHolidayLet/expenses/financialCosts"
      ),
      NumberValidation.validateAdjustment(
        field = fhl.expenses.flatMap(_.professionalFees),
        path = "/furnishedHolidayLet/expenses/professionalFees"
      ),
      NumberValidation.validateAdjustment(
        field = fhl.expenses.flatMap(_.travelCosts),
        path = "/furnishedHolidayLet/expenses/travelCosts"
      ),
      NumberValidation.validateAdjustment(
        field = fhl.expenses.flatMap(_.costOfServices),
        path = "/furnishedHolidayLet/expenses/costOfServices"
      ),
      NumberValidation.validateAdjustment(
        field = fhl.expenses.flatMap(_.other),
        path = "/furnishedHolidayLet/expenses/other"
      ),
      NumberValidation.validateAdjustment(
        field = fhl.expenses.flatMap(_.consolidatedExpenses),
        path = "/furnishedHolidayLet/expenses/consolidatedExpenses"
      )
    ).flatten
  }

  private def validateNonFhl(nonFhl: NonFurnishedHolidayLet) = {
    List(
      NumberValidation.validateAdjustment(
        field = nonFhl.income.flatMap(_.totalRentsReceived),
        path = "/nonFurnishedHolidayLet/income/totalRentsReceived"
      ),
      NumberValidation.validateAdjustment(
        field = nonFhl.income.flatMap(_.premiumsOfLeaseGrant),
        path = "/nonFurnishedHolidayLet/income/premiumsOfLeaseGrant"
      ),
      NumberValidation.validateAdjustment(
        field = nonFhl.income.flatMap(_.reversePremiums),
        path = "/nonFurnishedHolidayLet/income/reversePremiums"
      ),
      NumberValidation.validateAdjustment(
        field = nonFhl.income.flatMap(_.otherPropertyIncome),
        path = "/nonFurnishedHolidayLet/income/otherPropertyIncome"
      ),
      NumberValidation.validateAdjustment(
        field = nonFhl.expenses.flatMap(_.premisesRunningCosts),
        path = "/nonFurnishedHolidayLet/expenses/premisesRunningCosts"
      ),
      NumberValidation.validateAdjustment(
        field = nonFhl.expenses.flatMap(_.repairsAndMaintenance),
        path = "/nonFurnishedHolidayLet/expenses/repairsAndMaintenance"
      ),
      NumberValidation.validateAdjustment(
        field = nonFhl.expenses.flatMap(_.financialCosts),
        path = "/nonFurnishedHolidayLet/expenses/financialCosts"
      ),
      NumberValidation.validateAdjustment(
        field = nonFhl.expenses.flatMap(_.professionalFees),
        path = "/nonFurnishedHolidayLet/expenses/professionalFees"
      ),
      NumberValidation.validateAdjustment(
        field = nonFhl.expenses.flatMap(_.travelCosts),
        path = "/nonFurnishedHolidayLet/expenses/travelCosts"
      ),
      NumberValidation.validateAdjustment(
        field = nonFhl.expenses.flatMap(_.costOfServices),
        path = "/nonFurnishedHolidayLet/expenses/costOfServices"
      ),
      NumberValidation.validateAdjustment(
        field = nonFhl.expenses.flatMap(_.residentialFinancialCost),
        path = "/nonFurnishedHolidayLet/expenses/residentialFinancialCost"
      ),
      NumberValidation.validateAdjustment(
        field = nonFhl.expenses.flatMap(_.other),
        path = "/nonFurnishedHolidayLet/expenses/other"
      ),
      NumberValidation.validateAdjustment(
        field = nonFhl.expenses.flatMap(_.consolidatedExpenses),
        path = "/nonFurnishedHolidayLet/expenses/consolidatedExpenses"
      )
    ).flatten
  }

  private def validateFhlConsolidatedExpenses(expenses: FHLExpenses): List[MtdError] = {
    BothExpensesValidation.validate(
      expenses = expenses,
      path = "/furnishedHolidayLet/expenses"
    )
  }

  private def validateNonFhlConsolidatedExpenses(expenses: NonFHLExpenses): List[MtdError] = {
    BothExpensesValidation.validate(
      expenses = expenses,
      path = "/nonFurnishedHolidayLet/expenses"
    )
  }
}
