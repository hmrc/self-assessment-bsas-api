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
import javax.inject.Inject
import play.api.libs.json.JsLookupResult
import utils.CurrentDateProvider
import v1.controllers.requestParsers.validators.validations.{DateValidation, _}
import v1.models.errors._
import v1.models.request.triggerBsas.{TriggerBsasRawData, TriggerBsasRequestBody}

class TriggerBSASValidator @Inject()(val currentDateProvider: CurrentDateProvider) extends Validator[TriggerBsasRawData] with FixedConfig {

  private val validationSet = List(parameterFormatValidation, selfEmploymentIdValidator,
    dateFieldValidator, typeOfBusinessValidator, bodyFormatValidator, otherBodyFieldsValidator)

  private def parameterFormatValidation: TriggerBsasRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino)
    )
  }

  private def bodyFormatValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    List(
      JsonFormatValidation.validate[TriggerBsasRequestBody](data.body.json, RuleIncorrectOrEmptyBodyError)
    )
  }

  private def dateFieldValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    val startDate: JsLookupResult = data.body.json \ "accountingPeriod" \ "startDate"
    val endDate: JsLookupResult = data.body.json \ "accountingPeriod" \ "endDate"
    List(
      JsonValidation.validate(startDate)(DateValidation.validate(StartDateFormatError)),
      JsonValidation.validate(endDate)(DateValidation.validate(EndDateFormatError))
    )
  }

  private def selfEmploymentIdValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    List(
      JsonValidation.validate(data.body.json \ "selfEmploymentId")(SelfEmploymentIdValidation.validate)
    )
  }

  private def typeOfBusinessValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    List(
      JsonValidation.validate(data.body.json \ "typeOfBusiness")(TypeOfBusinessValidation.validate)
    )
  }


  private def otherBodyFieldsValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    val req = data.body.json.as[TriggerBsasRequestBody]
    List(
      SelfEmploymentIdRuleValidation.validate(req.selfEmploymentId, req.typeOfBusiness),
      EndBeforeStartDateValidation.validate(req.accountingPeriod.startDate, req.accountingPeriod.endDate),
      NotEndedAccountingPeriodValidation.validate(currentDateProvider.getCurrentDate().toString, req.accountingPeriod.endDate)
    )
  }

  override def validate(data: TriggerBsasRawData): List[MtdError] = run(validationSet, data)
}
