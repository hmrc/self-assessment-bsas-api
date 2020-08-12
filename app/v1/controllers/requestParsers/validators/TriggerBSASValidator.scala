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
import utils.CurrentDateProvider
import v1.controllers.requestParsers.validators.validations.{DateValidation, _}
import v1.models.errors._
import v1.models.request.triggerBsas.{TriggerBsasRawData, TriggerBsasRequestBody}

class TriggerBSASValidator @Inject()(val currentDateProvider: CurrentDateProvider) extends Validator[TriggerBsasRawData] with FixedConfig {

  private val validationSet = List(parameterFormatValidation, bodyFormatValidator, typeOfBusinessValidator,
    selfEmploymentIdValidator, dateFieldValidator, otherBodyFieldsValidator)

  private def parameterFormatValidation: TriggerBsasRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino)
    )
  }

  private def bodyFormatValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    List(
      JsonFormatValidation.validate[TriggerBsasRequestBody](data.body.json)
    )
  }

  private def dateFieldValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    val requestBodyData = data.body.json.as[TriggerBsasRequestBody]

    List(
      DateValidation.validate(StartDateFormatError)(requestBodyData.accountingPeriod.startDate),
      DateValidation.validate(EndDateFormatError)(requestBodyData.accountingPeriod.endDate)
    )
  }

  private def selfEmploymentIdValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    List(
      SelfEmploymentIdValidation.validateOption(data.body.json.as[TriggerBsasRequestBody].selfEmploymentId)
    )
  }

  private def typeOfBusinessValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    List(
      TypeOfBusinessValidation.validate(data.body.json.as[TriggerBsasRequestBody].typeOfBusiness.toString),
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
