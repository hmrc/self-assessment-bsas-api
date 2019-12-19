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
import v1.models.request.submitBsas.SubmitUKPropertyBsasRequestBody
import v1.models.request.submitBsas.selfEmployment.{SubmitSeBsasRawData, SubmitSeBsasRequestBody}

class SubmitSeBsasValidator extends Validator[SubmitSeBsasRawData] with FixedConfig {

  private val validationSet = List(parameterFormatValidator, bodyFormatValidator, adjustmentFieldValidator)

  private def parameterFormatValidator: SubmitSeBsasRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino),
      BsasIdValidation.validate(data.bsasId)
    )
  }

  private def bodyFormatValidator: SubmitSeBsasRawData => List[List[MtdError]] = { data =>
    List(
      JsonFormatValidation.validate[SubmitSeBsasRequestBody](data.body.json, RuleIncorrectOrEmptyBodyError)
    )
  }

  private def adjustmentFieldValidator: SubmitSeBsasRawData => List[List[MtdError]] = { data =>

    def toFieldNameMap[T <: Product](r: T): Map[String, Option[BigDecimal]] = {
      for ((k, Some(v)) <- r.getClass.getDeclaredFields.map(_.getName).zip(r.productIterator.to).toMap) yield k -> Some(v.asInstanceOf[BigDecimal])
    }

    val model: SubmitSeBsasRequestBody = data.body.json.as[SubmitSeBsasRequestBody]

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

  override def validate(data: SubmitSeBsasRawData): List[MtdError] = run(validationSet, data)
}
