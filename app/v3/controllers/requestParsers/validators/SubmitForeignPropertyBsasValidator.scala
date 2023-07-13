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
import v3.controllers.requestParsers.validators.validations._
import v3.models.errors._
import v3.models.request.submitBsas.foreignProperty._

import scala.annotation.nowarn

class SubmitForeignPropertyBsasValidator extends Validator[SubmitForeignPropertyRawData] {
  private val validationSet = List(
    parameterFormatValidation,
    parameterValidation,
    validateOnePropertyOnly,
    bodyFormatValidation,
    bodyFieldValidation
  )

  override def validate(data: SubmitForeignPropertyRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

  private def parameterFormatValidation: SubmitForeignPropertyRawData => List[List[MtdError]] = (data: SubmitForeignPropertyRawData) => {
    List(
      NinoValidation.validate(data.nino),
      CalculationIdValidation.validate(data.calculationId),
      TaxYearValidation.validate(data.taxYear)
    )
  }

  private def parameterValidation: SubmitForeignPropertyRawData => List[List[MtdError]] = (data: SubmitForeignPropertyRawData) => {
    List(
      data.taxYear.map(TaxYearTYSParameterValidation.validate).getOrElse(Nil)
    )
  }

  private def validateOnePropertyOnly: SubmitForeignPropertyRawData => List[List[MtdError]] = { data =>
    if (Seq("foreignFhlEea", "nonFurnishedHolidayLet").forall(field => (data.body \ field).isDefined)) {
      List(List(RuleBothPropertiesSuppliedError))
    } else {
      Nil
    }
  }

  @nowarn("cat=lint-byname-implicit")
  private def bodyFormatValidation: SubmitForeignPropertyRawData => List[List[MtdError]] = { data =>
    JsonFormatValidation.validateAndCheckNonEmpty[SubmitForeignPropertyBsasRequestBody](data.body) match {
      case Nil => NoValidationErrors
      case schemaErrors => List(schemaErrors)
    }
  }

  private def bodyFieldValidation: SubmitForeignPropertyRawData => List[List[MtdError]] = { data =>
    val body: SubmitForeignPropertyBsasRequestBody = data.body.as[SubmitForeignPropertyBsasRequestBody]

    List(
      flattenErrors(
        List(
          body.foreignFhlEea.map(validateForeignFhlEea).getOrElse(NoValidationErrors),
          body.nonFurnishedHolidayLet
            .map(_.zipWithIndex.toList.flatMap {
              case (entry, i) => validateForeignProperty(entry, i)
            })
            .getOrElse(NoValidationErrors),
          body.foreignFhlEea.flatMap(_.expenses.map(validateForeignFhlEeaConsolidatedExpenses)).getOrElse(NoValidationErrors),
          body.nonFurnishedHolidayLet
            .map(_.zipWithIndex.toList.map {
              case (entry, i) =>
                entry.expenses.map(expenditure => validateForeignPropertyConsolidatedExpenses(expenditure, i)).getOrElse(NoValidationErrors)
            })
            .getOrElse(Nil)
            .flatten,
          duplicateCountryCodeValidation(body)
        ))
    )
  }

  private def validateForeignFhlEea(foreignFhlEea: FhlEea) = {
    List(
      NumberValidation.validateAdjustment(
        field = foreignFhlEea.income.flatMap(_.totalRentsReceived),
        path = "/foreignFhlEea/income/totalRentsReceived"
      ),
      NumberValidation.validateAdjustment(
        field = foreignFhlEea.expenses.flatMap(_.premisesRunningCosts),
        path = "/foreignFhlEea/expenses/premisesRunningCosts"
      ),
      NumberValidation.validateAdjustment(
        field = foreignFhlEea.expenses.flatMap(_.repairsAndMaintenance),
        path = "/foreignFhlEea/expenses/repairsAndMaintenance"
      ),
      NumberValidation.validateAdjustment(
        field = foreignFhlEea.expenses.flatMap(_.financialCosts),
        path = "/foreignFhlEea/expenses/financialCosts"
      ),
      NumberValidation.validateAdjustment(
        field = foreignFhlEea.expenses.flatMap(_.professionalFees),
        path = "/foreignFhlEea/expenses/professionalFees"
      ),
      NumberValidation.validateAdjustment(
        field = foreignFhlEea.expenses.flatMap(_.travelCosts),
        path = "/foreignFhlEea/expenses/travelCosts"
      ),
      NumberValidation.validateAdjustment(
        field = foreignFhlEea.expenses.flatMap(_.costOfServices),
        path = "/foreignFhlEea/expenses/costOfServices"
      ),
      NumberValidation.validateAdjustment(
        field = foreignFhlEea.expenses.flatMap(_.other),
        path = "/foreignFhlEea/expenses/other"
      ),
      NumberValidation.validateAdjustment(
        field = foreignFhlEea.expenses.flatMap(_.consolidatedExpenses),
        path = "/foreignFhlEea/expenses/consolidatedExpenses"
      )
    ).flatten
  }

  private def validateForeignProperty(foreignProperty: ForeignProperty, index: Int) = {
    List(
      CountryCodeValidation.validate(
        field = foreignProperty.countryCode,
        path = s"/nonFurnishedHolidayLet/$index/countryCode"
      ),
      NumberValidation.validateAdjustment(
        field = foreignProperty.income.flatMap(_.totalRentsReceived),
        path = s"/nonFurnishedHolidayLet/$index/income/totalRentsReceived"
      ),
      NumberValidation.validateAdjustment(
        field = foreignProperty.income.flatMap(_.premiumsOfLeaseGrant),
        path = s"/nonFurnishedHolidayLet/$index/income/premiumsOfLeaseGrant"
      ),
      NumberValidation.validateAdjustment(
        field = foreignProperty.income.flatMap(_.otherPropertyIncome),
        path = s"/nonFurnishedHolidayLet/$index/income/otherPropertyIncome"
      ),
      NumberValidation.validateAdjustment(
        field = foreignProperty.expenses.flatMap(_.premisesRunningCosts),
        path = s"/nonFurnishedHolidayLet/$index/expenses/premisesRunningCosts"
      ),
      NumberValidation.validateAdjustment(
        field = foreignProperty.expenses.flatMap(_.repairsAndMaintenance),
        path = s"/nonFurnishedHolidayLet/$index/expenses/repairsAndMaintenance"
      ),
      NumberValidation.validateAdjustment(
        field = foreignProperty.expenses.flatMap(_.financialCosts),
        path = s"/nonFurnishedHolidayLet/$index/expenses/financialCosts"
      ),
      NumberValidation.validateAdjustment(
        field = foreignProperty.expenses.flatMap(_.professionalFees),
        path = s"/nonFurnishedHolidayLet/$index/expenses/professionalFees"
      ),
      NumberValidation.validateAdjustment(
        field = foreignProperty.expenses.flatMap(_.travelCosts),
        path = s"/nonFurnishedHolidayLet/$index/expenses/travelCosts"
      ),
      NumberValidation.validateAdjustment(
        field = foreignProperty.expenses.flatMap(_.costOfServices),
        path = s"/nonFurnishedHolidayLet/$index/expenses/costOfServices"
      ),
      NumberValidation.validateAdjustment(
        field = foreignProperty.expenses.flatMap(_.residentialFinancialCost),
        path = s"/nonFurnishedHolidayLet/$index/expenses/residentialFinancialCost"
      ),
      NumberValidation.validateAdjustment(
        field = foreignProperty.expenses.flatMap(_.other),
        path = s"/nonFurnishedHolidayLet/$index/expenses/other"
      ),
      NumberValidation.validateAdjustment(
        field = foreignProperty.expenses.flatMap(_.consolidatedExpenses),
        path = s"/nonFurnishedHolidayLet/$index/expenses/consolidatedExpenses"
      )
    ).flatten
  }

  private def validateForeignFhlEeaConsolidatedExpenses(expenses: FhlEeaExpenses): List[MtdError] = {
    BothExpensesValidation.validate(
      expenses = expenses,
      path = "/foreignFhlEea/expenses"
    )
  }

  private def validateForeignPropertyConsolidatedExpenses(expenses: ForeignPropertyExpenses, index: Int): List[MtdError] = {
    BothExpensesValidation.validate(
      expenses = expenses,
      path = s"/nonFurnishedHolidayLet/$index/expenses"
    )
  }

  private def duplicateCountryCodeValidation(body: SubmitForeignPropertyBsasRequestBody): List[MtdError] = {
    body.nonFurnishedHolidayLet
      .map { entries =>
        entries.zipWithIndex
          .map {
            case (entry, idx) => (entry.countryCode, s"/nonFurnishedHolidayLet/$idx/countryCode")
          }
          .groupBy(_._1)
          .collect {
            case (code, codeAndPaths) if codeAndPaths.size >= 2 =>
              RuleDuplicateCountryCodeError.forDuplicatedCodesAndPaths(code, codeAndPaths.map(_._2))
          }
          .toList
      }
      .getOrElse(Nil)
  }
}
