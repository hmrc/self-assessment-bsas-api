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

package v1.controllers.requestParsers.validators

import config.FixedConfig
import v1.controllers.requestParsers.validators.validations._
import v1.models.errors.{MtdError, RuleBothExpensesError, RuleIncorrectOrEmptyBodyError}
import v1.models.request.submitBsas.selfEmployment.{SubmitSelfEmploymentBsasRawData, SubmitSelfEmploymentBsasRequestBody}

class SubmitSelfEmploymentBsasValidator extends Validator[SubmitSelfEmploymentBsasRawData] with FixedConfig {

  private val validationSet = List(parameterFormatValidator, bodyFormatValidator, incorrectOrEmptyBodyValidator,
    adjustmentFieldValidator, bothExpensesValidator)

  private def parameterFormatValidator: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino),
      BsasIdValidation.validate(data.bsasId)
    )
  }

  private def bodyFormatValidator: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>
    List(Validator.flattenErrors(List(
      JsonFormatValidation.validate[SubmitSelfEmploymentBsasRequestBody](data.body.json)
    )))
  }

  private def incorrectOrEmptyBodyValidator: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>
    val model: SubmitSelfEmploymentBsasRequestBody = data.body.json.as[SubmitSelfEmploymentBsasRequestBody]
    List(
      if(model.isIncorrectOrEmptyBodyError) {
        List(RuleIncorrectOrEmptyBodyError)
      }
      else {
        NoValidationErrors
      }
    )
  }

  private def adjustmentFieldValidator: SubmitSelfEmploymentBsasRawData => List[List[MtdError]] = { data =>

    def toFieldNameMap[T <: Product](r: T): Map[String, Option[BigDecimal]] = {
      for ((k, Some(v)) <- r.getClass.getDeclaredFields.map(_.getName).zip(r.productIterator.to).toMap) yield k -> Some(v.asInstanceOf[BigDecimal])
    }

    val model: SubmitSelfEmploymentBsasRequestBody = data.body.json.as[SubmitSelfEmploymentBsasRequestBody]

    val allFields: Option[List[(String, Option[BigDecimal])]] = for {
      incomeFields <- model.income.map(toFieldNameMap).map(_.map { case(k, v) => s"income.$k" -> v})
      expensesFields <- model.expenses.map(toFieldNameMap).map(_.map { case(k, v) => s"expenses.$k" -> v})
      additionsFields <- model.additions.map(toFieldNameMap).map(_.map { case(k, v) => s"additions.$k" -> v})
    } yield {
      (incomeFields ++ expensesFields ++ additionsFields).toList
    }

    def callValidation(tu: List[(String, Option[BigDecimal])]): List[List[MtdError]] = tu.map {
      case (fieldName, value) => AdjustmentValueValidation.validate(value, fieldName) ++ AdjustmentRangeValidation.validate(value, fieldName)
    }

    allFields.map(callValidation).getOrElse(Nil)
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
