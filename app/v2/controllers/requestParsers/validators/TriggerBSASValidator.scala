/*
 * Copyright 2022 HM Revenue & Customs
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
import javax.inject.Inject
import utils.CurrentDateProvider
import v2.controllers.requestParsers.validators.validations._
import v2.models.domain.TypeOfBusiness
import v2.models.errors._
import v2.models.request.triggerBsas.{TriggerBsasRawData, TriggerBsasRequestBody}

class TriggerBSASValidator @Inject()(val currentDateProvider: CurrentDateProvider) extends Validator[TriggerBsasRawData] with FixedConfig {

  private val validationSet = List(parameterFormatValidation, incorrectOrEmptyBodyValidation, bodyFormatValidation, bodyRuleValidation)

  private def parameterFormatValidation: TriggerBsasRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino)
    )
  }

  private def incorrectOrEmptyBodyValidation: TriggerBsasRawData => List[List[MtdError]] = { data =>
    List(
      JsonFormatValidation.validate[TriggerBsasRequestBody](data.body.json)
    )
  }

  private def bodyFormatValidation: TriggerBsasRawData => List[List[MtdError]] = { data =>
    val req = data.body.json.as[TriggerBsasRequestBody]

    List(
      DateValidation.validate(StartDateFormatError)(req.accountingPeriod.startDate),
      DateValidation.validate(EndDateFormatError)(req.accountingPeriod.endDate),
      BusinessIdValidation.validate(req.businessId),
      TypeOfBusinessValidation.validate(req.typeOfBusiness)
    )
  }

  private def bodyRuleValidation: TriggerBsasRawData => List[List[MtdError]] = { data =>
    val req            = data.body.json.as[TriggerBsasRequestBody]
    val typeOfBusiness = TypeOfBusiness.parser(req.typeOfBusiness)
    List(
      EndBeforeStartDateValidation.validate(req.accountingPeriod.startDate, req.accountingPeriod.endDate),
      AccountingPeriodNotSupportedValidation.validate(typeOfBusiness, req.accountingPeriod.endDate)
    )
  }

  override def validate(data: TriggerBsasRawData): List[MtdError] = run(validationSet, data)
}
