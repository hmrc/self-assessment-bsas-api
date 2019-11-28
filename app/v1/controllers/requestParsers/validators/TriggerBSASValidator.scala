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

import javax.inject.Inject

import config.FixedConfig
import utils.CurrentDateProvider
import v1.controllers.requestParsers.validators.validations.{DateValidation, _}
import v1.models.domain.BSAS
import v1.models.errors._
import v1.models.request.triggerBsas.TriggerBsasRawData

class TriggerBSASValidator @Inject()(val currentDateProvider: CurrentDateProvider) extends Validator[TriggerBsasRawData] with FixedConfig {

  private val validationSet = List(parameterFormatValidation, bodyFormatValidator, taxYearValidator,selfEmploymentIdValidator,
    dateFieldValidator, typeOfBusinessValidator, otherBodyFieldsValidator)

  private def parameterFormatValidation: TriggerBsasRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino)
    )
  }

  private def bodyFormatValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    List(
      JsonFormatValidation.validate[BSAS](data.body.json, RuleIncorrectOrEmptyBodyError)
    )
  }

  private def dateFieldValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    val req = data.body.json.as[BSAS]
    List(
      DateValidation.validate(req.startDate, StartDateFormatError),
      DateValidation.validate(req.endDate, EndDateFormatError)
    )
  }

  private def taxYearValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    val req = data.body.json.as[BSAS]
    List(
      TaxYearValidation.validate(req.accountingPeriod)
    )
  }

  private def selfEmploymentIdValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    val req = data.body.json.as[BSAS]
    List(
      req.selfEmploymentId.map(SelfEmploymentIdValidation.validate).getOrElse(Nil)
    )
  }

  private def typeOfBusinessValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    val req = data.body.json.as[BSAS]
    List(
      TypeOfBusinessValidation.validate(req.typeOfBusiness)
    )
  }


  private def otherBodyFieldsValidator: TriggerBsasRawData => List[List[MtdError]] = { data =>
    val req = data.body.json.as[BSAS]
    List(
      SelfEmploymentIdRuleValidation.validate(req.selfEmploymentId, req.typeOfBusiness),
      EndBeforeStartDateValidation.validate(req.startDate, req.endDate),
      MtdTaxYearValidation.validate(req.accountingPeriod, RuleTaxYearNotSupportedError),
      NotEndedAccountingPeriodValidation.validate(currentDateProvider.getCurrentDate().toString, req.endDate)
    )
  }

  override def validate(data: TriggerBsasRawData): List[MtdError] = run(validationSet, data)
}
